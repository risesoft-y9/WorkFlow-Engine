package net.risesoft.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping(value = "/vue/sign")
@Slf4j
public class SignController {

    public static String getMD5(String psw) throws Exception {
        if (StringUtils.isEmpty(psw)) {
            return null;
        }
        return DigestUtils.sha1Hex(psw);
    }

    @Autowired
    private CalendarConfigApi calendarConfigApi;

    // 两个日期时间相隔天数
    public String daysBetween(String startTime, String endTime) {
        String day = "0";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startTime1 = sdf.parse(startTime);// 转成yyyy-MM-dd格式
            Date endTime1 = sdf.parse(endTime);// 转成yyyy-MM-dd格式
            long difference = (endTime1.getTime() - startTime1.getTime()) / 86400000;
            day = String.valueOf(Math.abs(difference) + 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    // 两个日期之间相隔天数，去除节假日
    public String daysBetween(String startTime, String endTime, String everyYearHoliday) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int days = 0;
        Calendar cal = Calendar.getInstance();
        Date startTime1 = sdf.parse(startTime);// 转成yyyy-MM-dd格式
        Date endTime1 = sdf.parse(endTime);// 转成yyyy-MM-dd格式
        while (startTime1.compareTo(endTime1) != 1) {// startTime1不大于endTime1则进入
            cal.setTime(startTime1);
            String time1 = sdf.format(startTime1);
            if (everyYearHoliday.contains(time1)) {// 节假日包含开始时间，则天数减1
                cal.add(Calendar.DAY_OF_MONTH, +1);// 开始时间加1天继续判断
                startTime1 = sdf.parse(sdf.format(cal.getTime()));
            } else {
                days += 1;
                cal.add(Calendar.DAY_OF_MONTH, +1);// 开始时间加1天继续判断
                startTime1 = sdf.parse(sdf.format(cal.getTime()));
            }
        }
        return String.valueOf(days);
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/getDay")
    public Y9Result<String> getDay(@RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String day = "";
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String year = startDate.substring(0, 4);
                CalendarConfigModel calendarConfigModel = calendarConfigApi.findByYear(tenantId, year);
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    day = daysBetween(startDate, endDate, everyYearHoliday);
                } else {
                    day = daysBetween(startDate, endDate);
                }
            }
            return Y9Result.success(day, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 有生云请假办件，计算请假天数和小时
     *
     * @param type
     * @param leaveStartTime
     * @param leaveEndTime
     * @param startSel
     * @param endSel
     * @param selStartTime
     * @param selEndTime
     * @return
     */
    @RequestMapping("/getDayOrHour")
    public Y9Result<String> getDayOrHour(String type, String leaveStartTime, String leaveEndTime, String startSel,
        String endSel, String selStartTime, String selEndTime, String leaveType) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr = "";
            CalendarConfigModel calendarConfig =
                calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]);
            dayStr = calendarConfig.getEveryYearHoliday();
            if (type.equals("天")) {
                boolean isdel = true;
                if (StringUtils.isNotBlank(leaveType) && (leaveType.equals("离京报备") || leaveType.equals("产假")
                    || leaveType.equals("婚假") || leaveType.equals("陪产假"))) {// 产假不排除节假日，直接算天数
                    isdel = false;
                }
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (isdel && dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                }
                String tmp = leaveStartTime;
                int num = 0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp={}", tmp);
                    if (isdel) {
                        if (!dayStr.contains(tmp)) {
                            num++;
                        }
                    } else {
                        num++;
                    }
                    tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                }
                return Y9Result.success(String.valueOf(num), "获取成功");
            } else if (type.equals("半天")) {
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                }
                String tmp = leaveStartTime;
                int num = 0;
                double start = 0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp={}", tmp);
                    if (!dayStr.contains(tmp)) {
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(startSel) && startSel.equals("下午")) {// 开始日期选择下午，算半天
                            start += 0.5;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(endSel) && endSel.equals("上午")) {// 结束日期选择上午，算半天
                            start += 0.5;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        num++;
                    }
                    tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                }
                if (start > 0) {
                    return Y9Result.success(String.valueOf(num + start), "获取成功");
                }
                return Y9Result.success(String.valueOf(num), "获取成功");
            } else if (type.equals("小时")) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    } else {// 同一天，计算时间
                        if (StringUtils.isBlank(selStartTime)) {
                            selStartTime = "09:00";
                        }
                        if (StringUtils.isBlank(selEndTime)) {
                            selEndTime = "17:30";
                        }
                        long time = sdf.parse(selEndTime).getTime() - sdf.parse(selStartTime).getTime();
                        double hours = (double)time / (60 * 60 * 1000);
                        BigDecimal a = BigDecimal.valueOf(hours);
                        double waitTime = a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        // 减去中间包含的1.5个小时
                        if (Integer.valueOf(selStartTime.split(":")[0]) < 12
                            && Integer.valueOf(selEndTime.split(":")[0]) > 12) {
                            waitTime = waitTime - 1.5;
                        }
                        return Y9Result.success(String.valueOf(waitTime), "获取成功");
                    }
                }

                String tmp = leaveStartTime;
                double timeCount = 0.0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp={}", tmp);
                    if (!dayStr.contains(tmp)) {
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(selStartTime)) {// 开始日期选择时间
                            long time = sdf.parse("17:30").getTime() - sdf.parse(selStartTime).getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            if (Integer.valueOf(selStartTime.split(":")[0]) < 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(selEndTime)) {// 结束日期选择时间
                            long time = sdf.parse(selEndTime).getTime() - sdf.parse("09:00").getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            if (Integer.valueOf(selEndTime.split(":")[0]) > 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        timeCount = timeCount + 7;// 其余时间每天加7小时
                    }
                    tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                }
                return Y9Result.success(String.valueOf(timeCount), "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("获取失败");
        }
        return Y9Result.success("", "获取成功");
    }

}
