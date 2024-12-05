package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.LightColorModel;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.service.WorkDayService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author : qinman
 * @date : 2024-12-04
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class WorkDayServiceImpl implements WorkDayService {

    private final CalendarConfigApi calendarConfigApi;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

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
        return (int) days;
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
                CalendarConfigModel calendarConfigModel = calendarConfigApi.findByYear(tenantId, sdf.format(startDate)).getData();
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
    public LightColorModel getLightColor(Date startDate, Date endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int lightColor = 0;
        String title = "";
        LightColorModel lightColorModel = new LightColorModel();
        try {
            if (null != startDate && null != endDate) {
                int days;
                CalendarConfigModel calendarConfigModel = calendarConfigApi.findByYear(tenantId, sdf.format(startDate)).getData();
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    days = daysBetween(startDate, endDate, everyYearHoliday);
                } else {
                    days = daysBetween(startDate, endDate);
                }
                if (days <= 10 && days > 7) {
                    lightColor = 1;
                    title = "该文件10个工作日内即将超时。";
                } else if (days <= 7 && days > 5) {
                    lightColor = 2;
                    title = "该文件7个工作日内即将超时。";
                } else if (days <= 5 && days > 3) {
                    lightColor = 3;
                    title = "该文件5个工作日内即将超时。";
                } else if (days <= 3 && days > 0) {
                    lightColor = 4;
                    title = "该文件3个工作日内即将超时。";
                } else if (days <= 0) {
                    lightColor = 5;
                    title = "该文件已超时。";
                }
                lightColorModel.setColor(lightColor);
                lightColorModel.setTitle(title);
            }
        } catch (Exception e) {
            LOGGER.error("获取红绿灯状态异常", e);
        }
        return lightColorModel;
    }
}
