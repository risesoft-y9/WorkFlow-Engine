package net.risesoft.service.impl;

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
import net.risesoft.util.Y9DateTimeUtils;
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

    // 获取两个日期时间的相隔天数
    public int daysBetween(Date startDate, Date endDate) {
        long days = 0;
        try {
            Date startDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(startDate));
            Date endDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(endDate));
            long difference = (endDateTemp.getTime() - startDateTemp.getTime()) / 86400000;
            days = Math.abs(difference) + 1;
        } catch (Exception e) {
            LOGGER.error("日期格式错误", e);
        }
        return (int)days;
    }

    // 获取两个日期之间相隔天数，去除节假日
    public int daysBetween(Date startDate, Date endDate, String everyYearHoliday) {
        int days = 0;
        try {
            Calendar cal = Calendar.getInstance();
            Date startDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(startDate));
            Date endDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(endDate));
            // startDateTemp不大于endDateTemp则进入
            while (startDateTemp.compareTo(endDateTemp) <= 0) {
                cal.setTime(startDateTemp);
                String time1 = Y9DateTimeUtils.formatDate(startDateTemp);
                // 节假日包含开始时间，则天数减1
                if (!everyYearHoliday.contains(time1)) {
                    days += 1;
                }
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(cal.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    calendarConfigApi.findByYear(tenantId, Y9DateTimeUtils.getYear(startDate)).getData();
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
    public String getDate(Date date, int days) throws Exception {
        if (days <= 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return Y9DateTimeUtils.formatDate(cal.getTime());
        }
        Calendar cal = Calendar.getInstance();
        CalendarConfigModel calendarConfigModel =
            calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), Y9DateTimeUtils.formatDate(date)).getData();
        String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
        String endDate = "";
        if (StringUtils.isNotBlank(everyYearHoliday)) {
            int i = 1;
            while (i < days) {
                String startDateString = Y9DateTimeUtils.formatDate(date);
                if (!everyYearHoliday.contains(startDateString)) {
                    i++;
                }
                assert date != null;
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH, +1);
                date = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(cal.getTime()));
            }
            endDate = Y9DateTimeUtils.formatDate(date);
        }
        return endDate;
    }

    @Override
    public TaskRelatedModel getLightColor(Date startDate, Date endDate) {
        try {
            if (null != startDate && null != endDate) {
                int days = calculateWorkDays(startDate, endDate);
                int lightColor = determineLightColorLevel(days);
                if (lightColor > 0) {
                    return new TaskRelatedModel(TaskRelatedEnum.LIGHTCOLOR.getValue(), String.valueOf(lightColor));
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取红绿灯状态异常", e);
        }
        return null;
    }

    /**
     * 计算两个日期间的工作日天数
     */
    private int calculateWorkDays(Date startDate, Date endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        CalendarConfigModel calendarConfigModel =
            calendarConfigApi.findByYear(tenantId, Y9DateTimeUtils.getYear(startDate)).getData();
        String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
        if (StringUtils.isNotBlank(everyYearHoliday)) {
            return daysBetween(startDate, endDate, everyYearHoliday);
        } else {
            return daysBetween(startDate, endDate);
        }
    }

    /**
     * 根据天数确定红绿灯等级
     */
    private int determineLightColorLevel(int days) {
        if (days <= 0) {
            return 5;
        } else if (days <= 3) {
            return 4;
        } else if (days <= 5) {
            return 3;
        } else if (days <= 7) {
            return 2;
        } else if (days <= 10) {
            return 1;
        }
        return 0;
    }
}
