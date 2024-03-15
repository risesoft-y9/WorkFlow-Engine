package net.risesoft.controller.mobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

import jodd.http.HttpStatus;

/**
 * 地灾考勤接口
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/sign")
@Slf4j
public class MobileSignController {

    public static String getMd5(String psw) throws Exception {
        if (StringUtils.isEmpty(psw)) {
            return null;
        }
        return DigestUtils.sha1Hex(psw);
    }

    @Autowired
    private PersonApi personApi;

    @Autowired
    private CalendarConfigApi calendarConfigManager;

    /**
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
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
     * 留言审批办结回调接口
     *
     * @param tenantId
     * @param bianhao
     * @param shifouzhanshi
     * @param huifuneirong
     * @param request
     * @param response
     */
    @RequestMapping(value = "/lyToReturn")
    @ResponseBody
    public void lyToReturn(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam(required = false, name = "lysp_bianhao") String bianhao,
        @RequestParam(required = false, name = "lysp_shifouzhanshi") String shifouzhanshi,
        @RequestParam(required = false, name = "lysp_huifuneirong") String huifuneirong, HttpServletRequest request,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String methodUrl = "http://www.caghp.org.cn/index.php/Member/returnback.html";
        HttpURLConnection connection = null;
        OutputStream dataout = null;
        BufferedReader reader = null;
        String line = null;
        try {
            String code = bianhao + "H2S5DV91ST2QSBtG";
            code = getMd5(code);
            LOGGER.debug("code:{}", code);
            Map<String, Object> mapFormJson = new HashMap<String, Object>(16);
            mapFormJson.put("code", code);
            mapFormJson.put("bianhao", bianhao);
            mapFormJson.put("shifouzhanshi", shifouzhanshi);
            mapFormJson.put("huifuneirong", huifuneirong);
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
            if (connection.getResponseCode() == HttpStatus.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    result.append(line).append(System.getProperty("line.separator"));
                }
                LOGGER.debug("result:{}", result);
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
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
