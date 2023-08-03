package net.risesoft.controller.mobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.dzxh.AttendanceApi;
import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.Person;
import net.risesoft.model.attendance.Attendance;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

import y9.apisix.annotation.NoApiClass;

/**
 * 地灾考勤接口
 */
@NoApiClass
@RestController
@RequestMapping("/mobile/sign")
@Slf4j
public class MobileSignController {

    public static String getMD5(String psw) throws Exception {
        if (StringUtils.isEmpty(psw)) {
            return null;
        }
        return DigestUtils.sha1Hex(psw);
    }

    @Autowired
    private PersonApi personManager;

    @Autowired
    private AttendanceApi attendanceManager;

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
     * 获取个人年休假
     *
     * @param tenantId
     * @param userId
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getAnnualLeaveDay")
    @ResponseBody
    public void getAnnualLeaveDay(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, HttpServletRequest request, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("actualAnnualLeave", "0");// 应休年假
        map.put("alreadyAnnualLeave", "0");// 已休年假

        map.put("actualVisitLeave", "0");// 应休探亲假
        map.put("alreadyVisitLeave", "0");// 已休探亲假
        try {
            map.put(UtilConsts.SUCCESS, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sdf.format(new Date());
            Attendance attendance =
                attendanceManager.findAnnualLeaveDaysByPersonIdAndYear(tenantId, userId, nowDate.substring(0, 4));
            if (attendance != null && StringUtils.isNotBlank(attendance.getId())) {
                map.put("actualAnnualLeave", attendance.getActualAnnualLeave());// 应休年假
                map.put("alreadyAnnualLeave", attendance.getAlreadyAnnualLeave());// 已休年假

                map.put("actualVisitLeave", attendance.getActualVisitLeave());// 应休探亲假
                map.put("alreadyVisitLeave", attendance.getAlreadyVisitLeave());// 已休探亲假
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取两个日期之间的天数，除去节假日
     *
     * @param tenantId
     * @param startDate
     * @param endDate
     * @param request
     * @param response
     * @param session
     */
    @RequestMapping(value = "/getDay")
    @ResponseBody
    public void getDay(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
        HttpServletRequest request, HttpServletResponse response, HttpSession session) {
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
     * @param type
     * @param leaveStartTime
     * @param leaveEndTime
     * @param startSel
     * @param endSel
     * @param selStartTime
     * @param selEndTime
     * @return
     */
    @SuppressWarnings("deprecation")
    @ResponseBody
    @RequestMapping("/getDayOrHour")
    public void getDayOrHour(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam(required = false) String type, @RequestParam(required = false) String leaveStartTime,
        @RequestParam(required = false) String leaveEndTime, @RequestParam(required = false) String startSel,
        @RequestParam(required = false) String endSel, @RequestParam(required = false) String selStartTime,
        @RequestParam(required = false) String selEndTime, @RequestParam(required = false) String leaveType,
        HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("data", "");
        map.put("msg", "获取成功");
        map.put("success", true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dayStr = "";
            CalendarConfigModel calendarConfig =
                calendarConfigManager.findByYear(Y9LoginUserHolder.getTenantId(), leaveEndTime.split("-")[0]);
            dayStr = calendarConfig.getEveryYearHoliday();
            if (type.equals("天")) {
                boolean isdel = true;
                if (StringUtils.isNotBlank(leaveType) && (leaveType.equals("离京报备") || leaveType.equals("产假")
                    || leaveType.equals("婚假") || leaveType.equals("陪产假"))) {// 产假不排除节假日，直接算天数
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
                        if (Integer.valueOf(selStartTime.split(":")[0]) < 12
                            && Integer.valueOf(selEndTime.split(":")[0]) > 12) {
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
     * 留言审批办结回调接口
     *
     * @param tenantId
     * @param lysp_bianhao
     * @param lysp_shifouzhanshi
     * @param lysp_huifuneirong
     * @param request
     * @param response
     */
    @RequestMapping(value = "/lyToReturn")
    @ResponseBody
    public void lyToReturn(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam(required = false) String lysp_bianhao, @RequestParam(required = false) String lysp_shifouzhanshi,
        @RequestParam(required = false) String lysp_huifuneirong, HttpServletRequest request,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String methodUrl = "http://www.caghp.org.cn/index.php/Member/returnback.html";
        HttpURLConnection connection = null;
        OutputStream dataout = null;
        BufferedReader reader = null;
        String line = null;
        try {
            String code = lysp_bianhao + "H2S5DV91ST2QSBtG";
            code = getMD5(code);
            LOGGER.debug("code={}", code);
            Map<String, Object> mapFormJson = new HashMap<String, Object>(16);
            mapFormJson.put("code", code);
            mapFormJson.put("bianhao", lysp_bianhao);
            mapFormJson.put("shifouzhanshi", lysp_shifouzhanshi);
            mapFormJson.put("huifuneirong", lysp_huifuneirong);
            JSONObject jsonObj = new JSONObject(mapFormJson);
            String formJsonData = jsonObj.toString();
            String urlEnCode = URLEncoder.encode(formJsonData, "UTF-8");
            URL url = new URL(methodUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            dataout = new DataOutputStream(connection.getOutputStream());
            String body = urlEnCode;
            dataout.write(body.getBytes());
            dataout.flush();
            dataout.close();
            if (connection.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));
                }
                LOGGER.debug("result={}", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return;
    }

    /**
     * 地灾接口，针对请假、出差、外勤做考勤记录
     *
     * @param tenantId
     * @param userId
     * @param username
     * @param startDate
     * @param endDate
     * @param type
     * @param leaveYear
     * @param request
     * @param response
     */
    @RequestMapping(value = "/saveToSign")
    @ResponseBody
    public void saveToSign(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam(required = false) String username, @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate, @RequestParam(required = false) String type,
        @RequestParam(required = false) String leaveYear, HttpServletRequest request, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map = attendanceManager.saveLeaveList(Y9LoginUserHolder.getTenantId(), username, startDate, endDate, type,
                leaveYear);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
