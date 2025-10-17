package net.risesoft.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            Date startTime1 = Y9DateTimeUtils.parseDate(startDate);
            Date endTime1 = Y9DateTimeUtils.parseDate(endDate);
            long difference = (endTime1.getTime() - startTime1.getTime()) / 86400000;
            day = String.valueOf(Math.abs(difference) + 1);
        } catch (Exception e) {
            LOGGER.error("日期格式错误", e);
        }
        return day;
    }

    // 获取两个日期之间相隔天数，去除节假日
    public String daysBetween(String startDate, String endDate, String everyYearHoliday) {
        int days = 0;
        try {
            Calendar cal = Calendar.getInstance();
            Date startDate1 = Y9DateTimeUtils.parseDate(startDate);
            Date endDate1 = Y9DateTimeUtils.parseDate(endDate);
            // startTime1不大于endTime1则进入
            while (startDate1.compareTo(endDate1) <= 0) {
                cal.setTime(startDate1);
                String time1 = Y9DateTimeUtils.formatDate(startDate1);
                // 开始时间加1天继续判断
                if (!everyYearHoliday.contains(time1)) {
                    days += 1;
                }
                cal.add(Calendar.DAY_OF_MONTH, +1);// 开始时间加1天继续判断
                startDate1 = Y9DateTimeUtils.parseDate(Y9DateTimeUtils.formatDate(cal.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(days);
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
            String dayStr;
            CalendarConfigModel calendarConfig =
                calendarConfigApi.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]).getData();
            dayStr = calendarConfig.getEveryYearHoliday();
            switch (type) {
                case "天": {
                    boolean isDel = !StringUtils.isNotBlank(leaveType) || (!leaveType.equals("离京报备")
                        && !leaveType.equals("产假") && !leaveType.equals("婚假") && !leaveType.equals("陪产假"));
                    // 产假不排除节假日，直接算天数
                    if (leaveStartTime.equals(leaveEndTime)) {
                        if (isDel && dayStr.contains(leaveStartTime)) {
                            return Y9Result.success("0", "获取成功");
                        }
                    }
                    String tmp = leaveStartTime;
                    int num = 0;
                    while (tmp.compareTo(leaveEndTime) <= 0) {
                        LOGGER.debug("tmp1={}", tmp);
                        if (isDel) {
                            if (!dayStr.contains(tmp)) {
                                num++;
                            }
                        } else {
                            num++;
                        }
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
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
                        LOGGER.debug("tmp2={}", tmp);
                        if (!dayStr.contains(tmp)) {
                            if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(startSel)
                                && startSel.equals("下午")) {// 开始日期选择下午，算半天
                                start += 0.5;
                                long time =
                                    Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                                tmp = Y9DateTimeUtils.formatDate(new Date(time));
                                continue;
                            }
                            if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(endSel) && endSel.equals("上午")) {// 结束日期选择上午，算半天
                                start += 0.5;
                                long time =
                                    Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                                tmp = Y9DateTimeUtils.formatDate(new Date(time));
                                continue;
                            }
                            num++;
                        }
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
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
                                selStartTime = STARTTIME_KEY;
                            }
                            if (StringUtils.isBlank(selEndTime)) {
                                selEndTime = ENDTIME_KEY;
                            }
                            long time = sdf.parse(selEndTime).getTime() - sdf.parse(selStartTime).getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            // 减去中间包含的1.5个小时
                            if (Integer.parseInt(selStartTime.split(":")[0]) < 12
                                && Integer.parseInt(selEndTime.split(":")[0]) > 12) {
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
                            LOGGER.debug("tmp3={}", tmp);
                            if (!dayStr.contains(tmp)) {
                                num++;
                            }
                            long time =
                                Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                            tmp = Y9DateTimeUtils.formatDate(new Date(time));
                        }
                        // 计算小时
                        if (StringUtils.isBlank(selStartTime)) {
                            selStartTime = STARTTIME_KEY;
                        }
                        if (StringUtils.isBlank(selEndTime)) {
                            selEndTime = ENDTIME_KEY;
                        }
                        long time = sdf.parse(selEndTime).getTime() - sdf.parse(selStartTime).getTime();
                        double hours = (double)time / (60 * 60 * 1000);
                        BigDecimal a = BigDecimal.valueOf(hours);
                        double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                        // 减去中间包含的1.5个小时
                        if (Integer.parseInt(selStartTime.split(":")[0]) < 12
                            && Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                            waitTime = waitTime - 1.5;
                        }
                        return Y9Result.success(String.valueOf(num * waitTime), "获取成功");
                    }

                    String tmp = leaveStartTime;
                    double timeCount = 0.0;
                    while (tmp.compareTo(leaveEndTime) <= 0) {
                        LOGGER.debug("tmp4={}", tmp);
                        if (!dayStr.contains(tmp)) {
                            if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(selStartTime)) {// 开始日期选择时间
                                long time = sdf.parse(ENDTIME_KEY).getTime() - sdf.parse(selStartTime).getTime();
                                double hours = (double)time / (60 * 60 * 1000);
                                BigDecimal a = BigDecimal.valueOf(hours);
                                double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                                if (Integer.parseInt(selStartTime.split(":")[0]) < 12) {
                                    waitTime = waitTime - 1.5;
                                }
                                timeCount = timeCount + waitTime;
                                long timeTemp =
                                    Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                                tmp = Y9DateTimeUtils.formatDate(new Date(timeTemp));
                                continue;
                            }
                            if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(selEndTime)) {// 结束日期选择时间
                                long time = sdf.parse(selEndTime).getTime() - sdf.parse(STARTTIME_KEY).getTime();
                                double hours = (double)time / (60 * 60 * 1000);
                                BigDecimal a = BigDecimal.valueOf(hours);
                                double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                                if (Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                                    waitTime = waitTime - 1.5;
                                }
                                timeCount = timeCount + waitTime;
                                long timeTemp =
                                    Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                                tmp = Y9DateTimeUtils.formatDate(new Date(timeTemp));
                                continue;
                            }
                            timeCount = timeCount + 7;// 其余时间每天加7小时
                        }
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
                    }
                    return Y9Result.success(String.valueOf(timeCount), "获取成功");
                }
                default: {
                    LOGGER.error("无效:{}", type);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取请假时长失败", e);
            return Y9Result.failure("获取失败");
        }
        return Y9Result.success("", "获取成功");
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
            switch (type) {
                case "天": {
                    if (leaveStartTime.equals(leaveEndTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                    String tmp = leaveStartTime;
                    int num = 0;
                    while (tmp.compareTo(leaveEndTime) <= 0) {
                        LOGGER.debug("tmp5={}", tmp);
                        num++;
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
                    }
                    return Y9Result.success(String.valueOf(num), "获取成功");
                }
                case "半天": {
                    if (leaveStartTime.equals(leaveEndTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                    String tmp = leaveStartTime;
                    int num = 0;
                    double start = 0;
                    while (tmp.compareTo(leaveEndTime) <= 0) {
                        LOGGER.debug("tmp6={}", tmp);
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(startSel) && startSel.equals("下午")) {// 开始日期选择下午，算半天
                            start += 0.5;
                            long time =
                                Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                            tmp = Y9DateTimeUtils.formatDate(new Date(time));
                            continue;
                        }
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(endSel) && endSel.equals("上午")) {// 结束日期选择上午，算半天
                            start += 0.5;
                            long time =
                                Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                            tmp = Y9DateTimeUtils.formatDate(new Date(time));
                            continue;
                        }
                        num++;
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
                    }
                    if (start > 0) {
                        return Y9Result.success(String.valueOf(num + start), "获取成功");
                    }
                    return Y9Result.success(String.valueOf(num), "获取成功");
                }
                case "小时": {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    if (leaveStartTime.equals(leaveEndTime)) {
                        // 同一天，计算时间
                        if (StringUtils.isBlank(selStartTime)) {
                            selStartTime = STARTTIME_KEY;
                        }
                        if (StringUtils.isBlank(selEndTime)) {
                            selEndTime = ENDTIME_KEY;
                        }
                        long time = sdf.parse(selEndTime).getTime() - sdf.parse(selStartTime).getTime();
                        double hours = (double)time / (60 * 60 * 1000);
                        BigDecimal a = BigDecimal.valueOf(hours);
                        double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                        // 减去中间包含的1.5个小时
                        if (Integer.parseInt(selStartTime.split(":")[0]) < 12
                            && Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                            waitTime = waitTime - 1.5;
                        }
                        return Y9Result.success(String.valueOf(waitTime), "获取成功");
                    }
                    String tmp = leaveStartTime;
                    double timeCount = 0.0;
                    while (tmp.compareTo(leaveEndTime) <= 0) {
                        LOGGER.debug("tmp8={}", tmp);
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(selStartTime)) {// 开始日期选择时间
                            long time = sdf.parse(ENDTIME_KEY).getTime() - sdf.parse(selStartTime).getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            if (Integer.parseInt(selStartTime.split(":")[0]) < 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            long timeTemp =
                                Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                            tmp = Y9DateTimeUtils.formatDate(new Date(timeTemp));
                            continue;
                        }
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(selEndTime)) {// 结束日期选择时间
                            long time = sdf.parse(selEndTime).getTime() - sdf.parse(STARTTIME_KEY).getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            if (Integer.parseInt(selEndTime.split(":")[0]) > 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            long timeTemp =
                                Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                            tmp = Y9DateTimeUtils.formatDate(new Date(timeTemp));
                            continue;
                        }
                        timeCount = timeCount + 7;// 其余时间每天加7小时
                        long time = Objects.requireNonNull(Y9DateTimeUtils.parseDate(tmp)).getTime() + 3600 * 24 * 1000;
                        tmp = Y9DateTimeUtils.formatDate(new Date(time));
                    }
                    return Y9Result.success(String.valueOf(timeCount), "获取成功");
                }
                default: {
                    LOGGER.error("无效:{}", type);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取时间日期时长失败", e);
            return Y9Result.failure("获取失败");
        }
        return Y9Result.success("", "获取成功");
    }

}
