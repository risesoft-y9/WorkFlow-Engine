package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.service.WorkDayService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author : qinman
 * @date : 2024-12-04
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class WorkDayServiceImpl implements WorkDayService {

    private final CalendarConfigApi calendarConfigApi;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

    // 获取两个日期时间的相隔天数
    public int daysBetween(Date startDate, Date endDate) {
        long days = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateTemp = sdf.parse(sdf.format(startDate));
            Date endDateTemp = sdf.parse(sdf.format(endDate));
            long difference = (endDateTemp.getTime() - startDateTemp.getTime()) / 86400000;
            days = Math.abs(difference) + 1;
        } catch (ParseException e) {
            LOGGER.error("日期格式错误", e);
        }
        return (int)days;
    }

    // 获取两个日期之间相隔天数，去除节假日
    public int daysBetween(Date startDate, Date endDate, String everyYearHoliday) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int days = 0;
        Calendar cal = Calendar.getInstance();
        Date startDateTemp = sdf.parse(sdf.format(startDate));
        Date endDateTemp = sdf.parse(sdf.format(endDate));
        // startDateTemp不大于endDateTemp则进入
        while (startDateTemp.compareTo(endDateTemp) != 1) {
            cal.setTime(startDateTemp);
            String time1 = sdf.format(startDateTemp);
            // 节假日包含开始时间，则天数减1
            if (everyYearHoliday.contains(time1)) {
                // 开始时间加1天继续判断
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startDateTemp = sdf.parse(sdf.format(cal.getTime()));
            } else {
                days += 1;
                // 开始时间加1天继续判断
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startDateTemp = sdf.parse(sdf.format(cal.getTime()));
            }
        }
        return days;
    }

    @Override
    public int getDay(Date startDate, Date endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int days = -1;
        try {
            if (null != startDate && null != endDate) {
                CalendarConfigModel calendarConfigModel =
                    calendarConfigApi.findByYear(tenantId, sdf.format(startDate)).getData();
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    days = daysBetween(startDate, endDate, everyYearHoliday);
                } else {
                    days = daysBetween(startDate, endDate);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return days;
    }

    @Override
    public String getDate(Date startDate, int days) throws ParseException {
        SimpleDateFormat sdfMmd = new SimpleDateFormat("yyyy-MM-dd");
        if (days <= 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            startDate = sdfMmd.parse(sdfMmd.format(cal.getTime()));
            return sdfMmd.format(startDate);
        }
        Calendar cal = Calendar.getInstance();
        CalendarConfigModel calendarConfigModel =
            calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), sdf.format(startDate)).getData();
        String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
        String endDate = "";
        if (StringUtils.isNotBlank(everyYearHoliday)) {
            int i = 1;
            while (i < days) {
                String startDateString = sdfMmd.format(startDate);
                if (!everyYearHoliday.contains(startDateString)) {
                    i++;
                }
                cal.setTime(startDate);
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startDate = sdfMmd.parse(sdfMmd.format(cal.getTime()));
            }
            endDate = sdfMmd.format(startDate);
        }
        return endDate;
    }

    @Override
    public TaskRelatedModel getLightColor(Date startDate, Date endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int lightColor = 0;
        try {
            if (null != startDate && null != endDate) {
                int days;
                CalendarConfigModel calendarConfigModel =
                    calendarConfigApi.findByYear(tenantId, sdf.format(startDate)).getData();
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    days = daysBetween(startDate, endDate, everyYearHoliday);
                } else {
                    days = daysBetween(startDate, endDate);
                }
                if (days <= 10 && days > 7) {
                    lightColor = 1;
                } else if (days <= 7 && days > 5) {
                    lightColor = 2;
                } else if (days <= 5 && days > 3) {
                    lightColor = 3;
                } else if (days <= 3 && days > 0) {
                    lightColor = 4;
                } else if (days <= 0) {
                    lightColor = 5;
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取红绿灯状态异常", e);
        }
        return new TaskRelatedModel(TaskRelatedEnum.LIGHTCOLOR.getValue(), String.valueOf(lightColor));
    }
}
