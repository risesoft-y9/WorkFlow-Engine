package net.risesoft.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 计算天数
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/sign")
@Slf4j
public class SignController {

    private final CalendarConfigApi calendarConfigApi;

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
            LOGGER.error("日期格式错误", e);
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
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/getDay")
    public Y9Result<String> getDay(@RequestParam String startDate, @RequestParam String endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String day = "";
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String year = startDate.substring(0, 4);
                CalendarConfigModel calendarConfigModel = calendarConfigApi.findByYear(tenantId, year).getData();
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    day = daysBetween(startDate, endDate, everyYearHoliday);
                } else {
                    day = daysBetween(startDate, endDate);
                }
            }
            return Y9Result.success(day, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取天数失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 有生云请假办件，计算请假天数和小时
     *
     * @param type 计算类型
     * @param leaveStartTime 开始日期
     * @param leaveEndTime 结束日期
     * @param startSel 上午下午选项
     * @param endSel 上午下午选项
     * @param selStartTime 开始时间节点
     * @param selEndTime 结束时间节点
     * @return Y9Result<String>
     */
    @RequestMapping("/getDayOrHour")
    public Y9Result<String> getDayOrHour(@RequestParam(required = false) String type, String leaveStartTime, String leaveEndTime, @RequestParam(required = false) String startSel, @RequestParam(required = false) String endSel, @RequestParam(required = false) String selStartTime,
        @RequestParam(required = false) String selEndTime, @RequestParam(required = false) String leaveType) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr;
            CalendarConfigModel calendarConfig = calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]).getData();
            dayStr = calendarConfig.getEveryYearHoliday();
            switch (type) {
                case "天": {
                    boolean isdel = true;
                    if (StringUtils.isNotBlank(leaveType) && (leaveType.equals("离京报备") || leaveType.equals("产假") || leaveType.equals("婚假") || leaveType.equals("陪产假"))) {// 产假不排除节假日，直接算天数
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
                }
                case "半天": {
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
                }
                case "小时": {
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
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            // 减去中间包含的1.5个小时
                            if (Integer.parseInt(selStartTime.split(":")[0]) < 12 && Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                                waitTime = waitTime - 1.5;
                            }
                            return Y9Result.success(String.valueOf(waitTime), "获取成功");
                        }
                    }

                    if (leaveType.equals("哺乳假")) {
                        // 计算天数
                        String tmp = leaveStartTime;
                        int num = 0;
                        while (tmp.compareTo(leaveEndTime) <= 0) {
                            LOGGER.debug("tmp={}", tmp);
                            if (!dayStr.contains(tmp)) {
                                num++;
                            }
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                        }
                        // 计算小时
                        if (StringUtils.isBlank(selStartTime)) {
                            selStartTime = "09:00";
                        }
                        if (StringUtils.isBlank(selEndTime)) {
                            selEndTime = "17:30";
                        }
                        long time = sdf.parse(selEndTime).getTime() - sdf.parse(selStartTime).getTime();
                        double hours = (double)time / (60 * 60 * 1000);
                        BigDecimal a = BigDecimal.valueOf(hours);
                        double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                        // 减去中间包含的1.5个小时
                        if (Integer.parseInt(selStartTime.split(":")[0]) < 12 && Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                            waitTime = waitTime - 1.5;
                        }
                        return Y9Result.success(String.valueOf(num * waitTime), "获取成功");
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
                                double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                                if (Integer.parseInt(selStartTime.split(":")[0]) < 12) {
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
                                double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                                if (Integer.parseInt(selEndTime.split(":")[0]) > 12) {
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
            }
        } catch (Exception e) {
            LOGGER.error("获取请假时长失败", e);
            return Y9Result.failure("获取失败");
        }
        return Y9Result.success("", "获取成功");
    }

}
