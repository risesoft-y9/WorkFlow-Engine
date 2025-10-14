package net.risesoft.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.service.ItemWorkDayService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author : qinman
 * @date : 2024-12-04
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemWorkDayServiceImpl implements ItemWorkDayService {

    private final CalendarConfigApi calendarConfigApi;

    // 获取两个日期时间的相隔天数
    public int daysBetween(Date startDate, Date endDate) throws Exception {
        long days = 0;
        try {
            Date startDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(startDate));
            Date endDateTemp = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(endDate));
            long difference = (endDateTemp.getTime() - startDateTemp.getTime()) / 86400000;
            days = Math.abs(difference) + 1;
        } catch (ParseException e) {
            LOGGER.error("日期格式错误", e);
        }
        return (int)days;
    }

    // 获取两个日期之间相隔天数，去除节假日
    public int daysBetween(Date startDate, Date endDate, String everyYearHoliday) throws Exception {
        int days = 0;
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
            date = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(cal.getTime()));
            return Y9DateTimeUtils.formatDate(date);
        }
        Calendar cal = Calendar.getInstance();
        CalendarConfigModel calendarConfigModel =
            calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), Y9DateTimeUtils.getYear(date)).getData();
        String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
        String endDate = "";
        if (StringUtils.isNotBlank(everyYearHoliday)) {
            int i = 1;
            while (i < days) {
                String startDateString = Y9DateTimeUtils.formatDate(date);
                if (!everyYearHoliday.contains(startDateString)) {
                    i++;
                }
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH, +1);
                date = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(cal.getTime()));
            }
            endDate = Y9DateTimeUtils.formatDate(date);
        }
        return endDate;
    }

    @Override
    public List<String> getDb(int days) {
        Date currentDate = new Date();
        String endDate = "";
        String startDate = "";
        try {
            endDate = this.getDate(currentDate, days);
            switch (days) {
                case 3:
                    startDate = Y9DateTimeUtils.formatDate(currentDate);
                    break;
                case 5:
                    startDate = this.getDate(currentDate, 4);
                    break;
                case 7:
                    startDate = this.getDate(currentDate, 6);
                    break;
                case 10:
                    startDate = this.getDate(currentDate, 8);
                    break;
                default:
                    startDate = "2025-01-01";
            }
        } catch (Exception e) {
            LOGGER.error("日期格式错误", e);
        }
        return List.of(startDate, endDate);
    }
}
