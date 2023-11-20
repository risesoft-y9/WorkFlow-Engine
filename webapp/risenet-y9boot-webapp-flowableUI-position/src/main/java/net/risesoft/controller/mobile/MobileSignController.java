package net.risesoft.controller.mobile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 请休假接口
 */
@RestController
@RequestMapping("/mobile/sign")
@Slf4j
public class MobileSignController {

    @Autowired
    private CalendarConfigApi calendarConfigManager;

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
        String s = String.valueOf(days);
        return s;
    }

    /**
     * 获取两个日期之间的天数，除去节假日
     *
     * @param tenantId 租户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param request
     * @param response
     * @param session
     */
    @RequestMapping(value = "/getDay")
    @ResponseBody
    public void getDay(@RequestHeader("auth-tenantId") String tenantId, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map.put(UtilConsts.SUCCESS, false);
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String year = startDate.substring(0, 4);
                CalendarConfigModel calendarConfigModel = calendarConfigManager.findByYear(tenantId, year);
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(everyYearHoliday)) {
                    String day = daysBetween(startDate, endDate, everyYearHoliday);
                    map.put("day", day);
                    map.put(UtilConsts.SUCCESS, true);
                } else {
                    String day = daysBetween(startDate, endDate);
                    map.put("day", day);
                    map.put(UtilConsts.SUCCESS, true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 有生云请假办件，计算请假天数和小时
     *
     * @param tenantId 租户id
     * @param type 计算类型，小时，天，半天
     * @param leaveType 请假类型
     * @param leaveStartTime 请假开始时间
     * @param leaveEndTime 请假结束时间
     * @param startSel 上午下午选择
     * @param endSel 上午下午选择
     * @param selStartTime 开始时间点选择
     * @param selEndTime 结束时间点选择
     * @return
     */
    @SuppressWarnings("deprecation")
    @ResponseBody
    @RequestMapping("/getDayOrHour")
    public void getDayOrHour(@RequestHeader("auth-tenantId") String tenantId, @RequestParam(required = false) String type, @RequestParam(required = false) String leaveStartTime, @RequestParam(required = false) String leaveEndTime, @RequestParam(required = false) String startSel,
        @RequestParam(required = false) String endSel, @RequestParam(required = false) String selStartTime, @RequestParam(required = false) String selEndTime, @RequestParam(required = false) String leaveType, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", "");
        map.put("msg", "获取成功");
        map.put("success", true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr = "";
            CalendarConfigModel calendarConfig = calendarConfigManager.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]);
            dayStr = calendarConfig.getEveryYearHoliday();
            if (type.equals("天")) {
                boolean isdel = true;
                if (StringUtils.isNotBlank(leaveType) && (leaveType.equals("离京报备") || leaveType.equals("产假") || leaveType.equals("婚假") || leaveType.equals("陪产假"))) {// 产假不排除节假日，直接算天数
                    isdel = false;
                }
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (isdel && dayStr.contains(leaveStartTime)) {
                        map.put("data", "0");
                        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                        return;
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
                map.put("data", String.valueOf(num));
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            } else if (type.equals("半天")) {
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        map.put("data", "");
                        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                        return;
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
                    map.put("data", String.valueOf(num + start));
                    Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                    return;
                }
                map.put("data", String.valueOf(num));
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            } else if (type.equals("小时")) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                if (leaveStartTime.equals(leaveEndTime)) {
                    if (dayStr.contains(leaveStartTime)) {
                        map.put("data", "0");
                        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                        return;
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
                        if (Integer.valueOf(selStartTime.split(":")[0]) < 12 && Integer.valueOf(selEndTime.split(":")[0]) > 12) {
                            waitTime = waitTime - 1.5;
                        }
                        map.put("data", String.valueOf(waitTime));
                        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                        return;
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
                map.put("data", String.valueOf(timeCount));
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "获取失败");
            map.put("success", false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param tenantId 租户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dateType 是否排除节假日和周末
     * @param request
     * @param response
     * @param session
     */
    @RequestMapping(value = "/getDays")
    @ResponseBody
    public void getDays(@RequestHeader("auth-tenantId") String tenantId, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String dateType, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map.put(UtilConsts.SUCCESS, false);
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String year = startDate.substring(0, 4);
                CalendarConfigModel calendarConfigModel = calendarConfigManager.findByYear(tenantId, year);
                String everyYearHoliday = calendarConfigModel.getEveryYearHoliday();
                if (StringUtils.isNotBlank(dateType) && dateType.equals("1")) {// 排除节假日，周末
                    if (StringUtils.isNotBlank(everyYearHoliday)) {
                        String day = daysBetween(startDate, endDate, everyYearHoliday);
                        map.put("day", day);
                        map.put(UtilConsts.SUCCESS, true);
                    } else {
                        String day = daysBetween(startDate, endDate);
                        map.put("day", day);
                        map.put(UtilConsts.SUCCESS, true);
                    }
                } else {// 不排除节假日和周末
                    String day = daysBetween(startDate, endDate);
                    map.put("day", day);
                    map.put(UtilConsts.SUCCESS, true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
