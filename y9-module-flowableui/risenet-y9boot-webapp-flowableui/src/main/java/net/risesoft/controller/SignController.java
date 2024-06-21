package net.risesoft.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/sign")
@Slf4j
public class SignController {

    public static String getMd5(String psw) throws Exception {
        if (StringUtils.isEmpty(psw)) {
            return null;
        }
        return DigestUtils.sha1Hex(psw);
    }

    @Autowired
    private CalendarConfigApi calendarConfigManager;

    /**
     *
     * Description: 两个日期时间相隔天数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public String daysBetween(String startTime, String endTime) {
        String day = "0";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startTime1 = sdf.parse(startTime);
            Date endTime1 = sdf.parse(endTime);
            long difference = (endTime1.getTime() - startTime1.getTime()) / 86400000;
            day = String.valueOf(Math.abs(difference) + 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    /**
     *
     * Description: 两个日期之间相隔天数，去除节假日
     *
     * @param startTime
     * @param endTime
     * @param everyYearHoliday
     * @return
     * @throws Exception
     */
    public String daysBetween(String startTime, String endTime, String everyYearHoliday) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int days = 0;
        Calendar cal = Calendar.getInstance();
        Date startTime1 = sdf.parse(startTime);
        Date endTime1 = sdf.parse(endTime);
        while (startTime1.compareTo(endTime1) != 1) {
            cal.setTime(startTime1);
            String time1 = sdf.format(startTime1);
            if (everyYearHoliday.contains(time1)) {
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startTime1 = sdf.parse(sdf.format(cal.getTime()));
            } else {
                days += 1;
                cal.add(Calendar.DAY_OF_MONTH, +1);
                startTime1 = sdf.parse(sdf.format(cal.getTime()));
            }
        }
        String s = String.valueOf(days);
        return s;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/getDay")
    public Y9Result<String> getDay(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String day = "";
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String year = startDate.substring(0, 4);
                CalendarConfigModel calendarConfigModel = calendarConfigManager.findByYear(tenantId, year).getData();
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
    public Y9Result<String> getDayOrHour(String type, String leaveStartTime, String leaveEndTime, String startSel, String endSel, String selStartTime, String selEndTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr = "";
            CalendarConfigModel calendarConfig = calendarConfigManager.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]).getData();
            dayStr = calendarConfig.getEveryYearHoliday();
            String day = "天", halfDay = "半天", hs = "小时";
            int hd = 12;
            if (day.equals(type)) {
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                }
                String tmp = leaveStartTime;
                int num = 0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp:{}", tmp);
                    if (!dayStr.contains(tmp)) {
                        num++;
                    }
                    tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                }
                return Y9Result.success(String.valueOf(num), "获取成功");
            } else if (halfDay.equals(type)) {
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    }
                }
                String tmp = leaveStartTime;
                int num = 0;
                double start = 0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp:{}", tmp);
                    if (!dayStr.contains(tmp)) {
                        // 开始日期选择下午，算半天
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(startSel) && "下午".equals(startSel)) {
                            start += 0.5;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        // 结束日期选择上午，算半天
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(endSel) && "上午".equals(endSel)) {
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
            } else if (type.equals(hs)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        return Y9Result.success("0", "获取成功");
                    } else {
                        // 同一天，计算时间
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
                        if (Integer.valueOf(selStartTime.split(PunctuationConsts.COLON)[0]) < hd && Integer.valueOf(selEndTime.split(PunctuationConsts.COLON)[0]) > hd) {
                            waitTime = waitTime - 1.5;
                        }
                        return Y9Result.success(String.valueOf(waitTime), "获取成功");
                    }
                }

                String tmp = leaveStartTime;
                double timeCount = 0.0;
                while (tmp.compareTo(leaveEndTime) <= 0) {
                    LOGGER.debug("tmp:{}", tmp);
                    if (!dayStr.contains(tmp)) {
                        // 开始日期选择时间
                        if (tmp.equals(leaveStartTime) && StringUtils.isNotBlank(selStartTime)) {
                            long time = sdf.parse("17:30").getTime() - sdf.parse(selStartTime).getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            if (Integer.valueOf(selStartTime.split(":")[0]) < 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        // 结束日期选择时间
                        if (tmp.equals(leaveEndTime) && StringUtils.isNotBlank(selEndTime)) {
                            long time = sdf.parse(selEndTime).getTime() - sdf.parse("09:00").getTime();
                            double hours = (double)time / (60 * 60 * 1000);
                            BigDecimal a = BigDecimal.valueOf(hours);
                            double waitTime = a.setScale(2, RoundingMode.HALF_UP).doubleValue();
                            if (Integer.valueOf(selEndTime.split(":")[0]) > 12) {
                                waitTime = waitTime - 1.5;
                            }
                            timeCount = timeCount + waitTime;
                            tmp = format.format(format.parse(tmp).getTime() + 3600 * 24 * 1000);
                            continue;
                        }
                        // 其余时间每天加7小时
                        timeCount = timeCount + 7;
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
