package net.risesoft.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.Y9DateTimeUtils;
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
@RequestMapping(value = "/vue/sign", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class SignController {

    private static final String STARTTIME_KEY = "09:00";
    private static final String ENDTIME_KEY = "17:30";
    private final CalendarConfigApi calendarConfigApi;

    // 获取两个日期时间的相隔天数
    public String daysBetween(String startDate, String endDate) {
        String day = "0";
        try {
            Date startTime = Y9DateTimeUtils.parseDate(startDate);
            Date endTime = Y9DateTimeUtils.parseDate(endDate);
            long difference = (endTime.getTime() - startTime.getTime()) / 86400000;
            day = String.valueOf(Math.abs(difference) + 1);
        } catch (Exception e) {
            LOGGER.error("日期格式错误", e);
        }
        return day;
    }

    // 获取两个日期之间相隔天数，去除节假日
    public String daysBetween(String startDate, String endDate, String everyYearHoliday) {
        try {
            return String.valueOf(calculateWorkDays(startDate, endDate, everyYearHoliday));
        } catch (Exception e) {
            LOGGER.error("计算工作日天数异常", e);
            return "0";
        }
    }

    // 新增公共方法用于计算不含节假日的工作日天数
    private int calculateWorkDays(String startDate, String endDate, String holidayStr) throws Exception {
        int count = 0;
        String currentDate = startDate;
        while (currentDate.compareTo(endDate) <= 0) {
            if (!holidayStr.contains(currentDate)) {
                count++;
            }
            currentDate = getNextDay(currentDate);
        }
        return count;
    }

    /**
     * 获取两个日期之间相隔天数（有节假日的情况去除节假日）
     * 
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return Y9Result<String>
     */
    @GetMapping(value = "/getDay")
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
     * 获取请假办件，计算请假天数和小时
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
    @GetMapping("/getDayOrHour")
    public Y9Result<String> getDayOrHour(@RequestParam(required = false) String type, String leaveStartTime,
        String leaveEndTime, @RequestParam(required = false) String startSel,
        @RequestParam(required = false) String endSel, @RequestParam(required = false) String selStartTime,
        @RequestParam(required = false) String selEndTime, @RequestParam(required = false) String leaveType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String year = leaveEndTime.split("-")[0];
            CalendarConfigModel calendarConfig = calendarConfigApi.findByYear(tenantId, year).getData();
            String holidayStr = calendarConfig.getEveryYearHoliday();
            String result = calculateLeaveDuration(type, leaveStartTime, leaveEndTime, startSel, endSel, selStartTime,
                selEndTime, leaveType, holidayStr);
            return Y9Result.success(result, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取请假时长失败", e);
            return Y9Result.failure("获取失败");
        }
    }

    /**
     * 计算天数和小时
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
    @GetMapping("/getCommonDayOrHour")
    public Y9Result<String> getCommonDayOrHour(@RequestParam(required = false) String type, String leaveStartTime,
        String leaveEndTime, @RequestParam(required = false) String startSel,
        @RequestParam(required = false) String endSel, @RequestParam(required = false) String selStartTime,
        @RequestParam(required = false) String selEndTime) {
        try {
            String result = calculateCommonLeaveDuration(type, leaveStartTime, leaveEndTime, startSel, endSel,
                selStartTime, selEndTime);
            return Y9Result.success(result, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取时间日期时长失败", e);
            return Y9Result.failure("获取失败");
        }
    }

    /**
     * 计算请假时长
     */
    private String calculateLeaveDuration(String type, String leaveStartTime, String leaveEndTime, String startSel,
        String endSel, String selStartTime, String selEndTime, String leaveType, String holidayStr) throws Exception {
        switch (type) {
            case "天":
                return calculateDays(leaveStartTime, leaveEndTime, leaveType, holidayStr);
            case "半天":
                return calculateHalfDays(leaveStartTime, leaveEndTime, startSel, endSel, holidayStr);
            case "小时":
                return calculateHours(leaveStartTime, leaveEndTime, selStartTime, selEndTime, leaveType, holidayStr);
            default:
                LOGGER.error("无效的计算类型: {}", type);
                return "";
        }
    }

    /**
     * 计算天数
     */
    private String calculateDays(String leaveStartTime, String leaveEndTime, String leaveType, String holidayStr)
        throws Exception {
        boolean excludeHolidays = shouldExcludeHolidays(leaveType);
        if (leaveStartTime.equals(leaveEndTime) && excludeHolidays && holidayStr.contains(leaveStartTime)) {
            return "0";
        }
        int count = 0;
        String currentDate = leaveStartTime;
        while (currentDate.compareTo(leaveEndTime) <= 0) {
            if (excludeHolidays) {
                if (!holidayStr.contains(currentDate)) {
                    count++;
                }
            } else {
                count++;
            }
            currentDate = getNextDay(currentDate);
        }
        return String.valueOf(count);
    }

    /**
     * 判断是否需要排除节假日
     */
    private boolean shouldExcludeHolidays(String leaveType) {
        return !StringUtils.isNotBlank(leaveType) || (!"离京报备".equals(leaveType) && !"产假".equals(leaveType)
            && !"婚假".equals(leaveType) && !"陪产假".equals(leaveType));
    }

    /**
     * 计算半天数
     */
    private String calculateHalfDays(String leaveStartTime, String leaveEndTime, String startSel, String endSel,
        String holidayStr) throws Exception {
        if (leaveStartTime.equals(leaveEndTime)) {
            if (holidayStr.contains(leaveStartTime)) {
                return "0";
            }
        }
        return calculateHalfDaysInternal(leaveStartTime, leaveEndTime, startSel, endSel, holidayStr, true);
    }

    /**
     * 计算通用半天数（不考虑节假日）
     */
    private String calculateCommonHalfDays(String leaveStartTime, String leaveEndTime, String startSel, String endSel)
        throws Exception {
        if (leaveStartTime.equals(leaveEndTime)) {
            return "0";
        }
        return calculateHalfDaysInternal(leaveStartTime, leaveEndTime, startSel, endSel, null, false);
    }

    /**
     * 计算半天数的内部实现方法
     *
     * @param leaveStartTime 开始时间
     * @param leaveEndTime 结束时间
     * @param startSel 开始时间选项（上午/下午）
     * @param endSel 结束时间选项（上午/下午）
     * @param holidayStr 节假日字符串（为null时表示不考虑节假日）
     * @param checkHoliday 是否检查节假日
     * @return 计算结果
     */
    private String calculateHalfDaysInternal(String leaveStartTime, String leaveEndTime, String startSel, String endSel,
        String holidayStr, boolean checkHoliday) throws Exception {
        String currentDate = leaveStartTime;
        int fullDays = 0;
        double halfDays = 0.0;
        while (currentDate.compareTo(leaveEndTime) <= 0) {
            // 如果需要检查节假日且当前日期是节假日，则跳过
            if (checkHoliday && holidayStr != null && holidayStr.contains(currentDate)) {
                currentDate = getNextDay(currentDate);
                continue;
            }
            if (currentDate.equals(leaveStartTime) && StringUtils.isNotBlank(startSel) && "下午".equals(startSel)) {
                halfDays += 0.5;
            } else if (currentDate.equals(leaveEndTime) && StringUtils.isNotBlank(endSel) && "上午".equals(endSel)) {
                halfDays += 0.5;
            } else {
                fullDays++;
            }
            currentDate = getNextDay(currentDate);
        }
        return String.valueOf(fullDays + halfDays);
    }

    /**
     * 计算小时数
     */
    private String calculateHours(String leaveStartTime, String leaveEndTime, String selStartTime, String selEndTime,
        String leaveType, String holidayStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (leaveStartTime.equals(leaveEndTime)) {
            return calculateSameDayHours(leaveStartTime, selStartTime, selEndTime, holidayStr, sdf);
        }
        if ("哺乳假".equals(leaveType)) {
            return calculateNursingLeaveHours(leaveStartTime, leaveEndTime, selStartTime, selEndTime, holidayStr, sdf);
        }
        return calculateMultiDayHours(leaveStartTime, leaveEndTime, selStartTime, selEndTime, holidayStr, sdf);
    }

    /**
     * 计算同一天的小时数
     */
    private String calculateSameDayHours(String leaveStartTime, String selStartTime, String selEndTime,
        String holidayStr, SimpleDateFormat sdf) throws Exception {
        if (holidayStr.contains(leaveStartTime)) {
            return "0";
        }
        if (StringUtils.isBlank(selStartTime)) {
            selStartTime = STARTTIME_KEY;
        }
        if (StringUtils.isBlank(selEndTime)) {
            selEndTime = ENDTIME_KEY;
        }
        double hours = calculateWorkHours(selStartTime, selEndTime, sdf);
        return String.valueOf(hours);
    }

    /**
     * 计算哺乳假小时数
     */
    private String calculateNursingLeaveHours(String leaveStartTime, String leaveEndTime, String selStartTime,
        String selEndTime, String holidayStr, SimpleDateFormat sdf) throws Exception {
        // 计算天数
        int days = 0;
        String currentDate = leaveStartTime;
        while (currentDate.compareTo(leaveEndTime) <= 0) {
            if (!holidayStr.contains(currentDate)) {
                days++;
            }
            currentDate = getNextDay(currentDate);
        }
        // 计算每天小时数
        if (StringUtils.isBlank(selStartTime)) {
            selStartTime = STARTTIME_KEY;
        }
        if (StringUtils.isBlank(selEndTime)) {
            selEndTime = ENDTIME_KEY;
        }
        double hoursPerDay = calculateWorkHours(selStartTime, selEndTime, sdf);
        return String.valueOf(days * hoursPerDay);
    }

    /**
     * 计算多天的小时数
     */
    private String calculateMultiDayHours(String leaveStartTime, String leaveEndTime, String selStartTime,
        String selEndTime, String holidayStr, SimpleDateFormat sdf) throws Exception {
        String currentDate = leaveStartTime;
        double totalHours = 0.0;
        while (currentDate.compareTo(leaveEndTime) <= 0) {
            if (!holidayStr.contains(currentDate)) {
                double dayHours =
                    calculateDayHours(currentDate, leaveStartTime, leaveEndTime, selStartTime, selEndTime, sdf);
                totalHours += dayHours;
            }
            currentDate = getNextDay(currentDate);
        }
        return String.valueOf(totalHours);
    }

    /**
     * 计算单天小时数
     */
    private double calculateDayHours(String currentDate, String leaveStartTime, String leaveEndTime,
        String selStartTime, String selEndTime, SimpleDateFormat sdf) throws ParseException {
        if (currentDate.equals(leaveStartTime) && StringUtils.isNotBlank(selStartTime)) {
            // 开始日期选择时间
            double waitTime = calculateHoursBetween(selStartTime, selEndTime, sdf);
            if (Integer.parseInt(selStartTime.split(":")[0]) < 12) {
                waitTime = waitTime - 1.5;
            }
            return waitTime;
        } else if (currentDate.equals(leaveEndTime) && StringUtils.isNotBlank(selEndTime)) {
            // 结束日期选择时间
            double waitTime = calculateHoursBetween(selStartTime, selEndTime, sdf);
            if (Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                waitTime = waitTime - 1.5;
            }
            return waitTime;
        } else {
            // 其余时间每天加7小时
            return 7.0;
        }
    }

    /**
     * 计算工作小时数（减去午休时间）
     */
    private double calculateWorkHours(String startTime, String endTime, SimpleDateFormat sdf) throws ParseException {
        double waitTime = calculateHoursBetween(startTime, endTime, sdf);
        // 减去中间包含的1.5个小时午休时间
        if (Integer.parseInt(startTime.split(":")[0]) < 12 && Integer.parseInt(endTime.split(":")[0]) > 12) {
            waitTime = waitTime - 1.5;
        }
        return waitTime;
    }

    /**
     * 获取下一天日期
     */
    private String getNextDay(String currentDate) throws Exception {
        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(currentDate)).getTime() + 3600 * 24 * 1000;
        return Y9DateTimeUtils.formatDate(new Date(time));
    }

    /**
     * 计算通用请假时长（不考虑节假日）
     */
    private String calculateCommonLeaveDuration(String type, String leaveStartTime, String leaveEndTime,
        String startSel, String endSel, String selStartTime, String selEndTime) throws Exception {
        switch (type) {
            case "天":
                return calculateCommonDays(leaveStartTime, leaveEndTime);
            case "半天":
                return calculateCommonHalfDays(leaveStartTime, leaveEndTime, startSel, endSel);
            case "小时":
                return calculateCommonHours(leaveStartTime, leaveEndTime, selStartTime, selEndTime);
            default:
                LOGGER.error("无效:{}", type);
                return "";
        }
    }

    /**
     * 计算通用天数（不考虑节假日）
     */
    private String calculateCommonDays(String leaveStartTime, String leaveEndTime) throws Exception {
        if (leaveStartTime.equals(leaveEndTime)) {
            return "0";
        }
        int count = 0;
        String currentDate = leaveStartTime;
        while (currentDate.compareTo(leaveEndTime) <= 0) {
            count++;
            currentDate = getNextDay(currentDate);
        }
        return String.valueOf(count);
    }

    /**
     * 计算通用小时数（不考虑节假日）
     */
    private String calculateCommonHours(String leaveStartTime, String leaveEndTime, String selStartTime,
        String selEndTime) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        if (leaveStartTime.equals(leaveEndTime)) {
            // 同一天，计算时间
            if (StringUtils.isBlank(selStartTime)) {
                selStartTime = STARTTIME_KEY;
            }
            if (StringUtils.isBlank(selEndTime)) {
                selEndTime = ENDTIME_KEY;
            }
            double waitTime = calculateWorkHours(selStartTime, selEndTime, sdf);
            return Y9Result.success(String.valueOf(waitTime), "获取成功").getData();
        }

        return String
            .valueOf(calculateCommonMultiDayHours(leaveStartTime, leaveEndTime, selStartTime, selEndTime, sdf));
    }

    /**
     * 计算通用多天小时数（不考虑节假日）
     */
    private double calculateCommonMultiDayHours(String leaveStartTime, String leaveEndTime, String selStartTime,
        String selEndTime, SimpleDateFormat sdf) throws Exception {

        String currentDate = leaveStartTime;
        double totalHours = 0.0;

        while (currentDate.compareTo(leaveEndTime) <= 0) {
            double dayHours =
                calculateDayHours(currentDate, leaveStartTime, leaveEndTime, selStartTime, selEndTime, sdf);
            totalHours += dayHours;
            currentDate = getNextDay(currentDate);
        }

        return totalHours;
    }

    private double calculateHoursBetween(String startTime, String endTime, SimpleDateFormat sdf) throws ParseException {
        long time = sdf.parse(endTime).getTime() - sdf.parse(startTime).getTime();
        double hours = (double)time / (60 * 60 * 1000);
        BigDecimal a = BigDecimal.valueOf(hours);
        return a.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
