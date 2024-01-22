package net.risesoft.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

import jodd.util.Base64;

@Controller
@RequestMapping(value = "/print")
@Slf4j
public class PrintFormController {

    @Autowired
    private Opinion4PositionApi opinion4PositionApi;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 表单转img
     *
     * @param itembox
     * @param activitiUser
     * @param taskDefKey
     * @param temp_Id
     * @param taskId
     * @param guid
     * @param itemId
     * @param request
     */
    @SuppressWarnings({"deprecation", "resource"})
    @RequestMapping(value = "/formToImg")
    @ResponseBody
    public Map<String, Object> formToImg(@RequestParam(required = false) String activitiUser, @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String temp_Id, @RequestParam(required = false) String taskId, @RequestParam(required = false) String guid,
        @RequestParam(required = false) String itemId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String BLANK = "  ";
            String str1 = "phantomjs";
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf.format(date);
            String str2 = request.getRealPath("/") + "static" + File.separator + "screenshot.js";
            String str3 = request.getRealPath("/") + "static" + File.separator + "formToImg" + File.separator + "img" + temp_Id + "-" + time + ".png";
            String url = y9Config.getCommon().getFlowableBaseUrl() + "/eform/engine/getTemplate?temp_Id=" + temp_Id + "&edittype=1" + "&guid=" + guid + "&taskDefKey=" + taskDefKey + "&activitiUser=" + activitiUser + "&itemId=" + itemId + "&formToPDF=formToPDF" + "&ctx="
                + y9Config.getCommon().getFlowableBaseUrl() + "&tenantId=" + Y9LoginUserHolder.getTenantId() + "&userId=" + person.getPersonId();
            Process process = Runtime.getRuntime().exec(str1 + BLANK // 你的phantomjs.exe路径
                + str2 + BLANK // 就是上文中那段javascript脚本的存放路径
                + url + BLANK // 你的目标url地址
                + str3);// 你的图片输出路径
            // process.wait(1000);
            process.getInputStream();
            InputStream input = new FileInputStream(str3);
            byte[] byt = new byte[input.available()];
            input.read(byt);
            String encoded = "data:image/png;base64," + Base64.encodeToString(byt);
            map.put("newImgUrl", encoded);
            map.put(UtilConsts.SUCCESS, true);
            if (process != null) {
                process.destroy();
                process = null;
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 表单转pdf
     *
     * @param formId
     * @param url
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("deprecation")
    @ResponseBody
    @RequestMapping(value = "/formToPDF")
    public Map<String, Object> formToPDF(@RequestParam(required = false) String formId, @RequestParam(required = false) String printUrl, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Process process = null;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(date);
        String str3 = request.getRealPath("/") + "static" + File.separator + "formToPDF" + File.separator + "pdf_" + formId + "_" + time + ".pdf";
        try {
            String[] json = printUrl.split("jsonData=");
            Map<String, Object> json_map = Y9JsonUtil.readHashMap(json[1]);
            printUrl = json[0];
            String processSerialNumber = (String)json_map.get("processSerialNumber");
            String processDefinitionId = (String)json_map.get("processDefinitionId");
            String taskDefKey = (String)json_map.get("taskDefKey");
            String itemId = (String)json_map.get("itemId");
            String activitiUser = (String)json_map.get("activitiUser");
            String processInstanceId = (String)json_map.get("processInstanceId");
            printUrl += "formId=" + formId + "&processSerialNumber=" + processSerialNumber + "&processDefinitionId=" + processDefinitionId + "&taskDefKey=" + taskDefKey + "&itemId=" + itemId + "&activitiUser=" + activitiUser + "&processInstanceId=" + processInstanceId;
            LOGGER.info("printUrl:{}", printUrl);
            String BLANK = "  ";
            String str1 = request.getRealPath("/") + "static" + File.separator + "phantomjs" + File.separator + "bin" + File.separator + "phantomjs.exe";
            String os = System.getProperty("os.name");
            if ((os != null && os.toLowerCase().indexOf("win") > -1)) {// window
                // 根据phantomjs.exe的路径而定，如“C:\Users\Think\Desktop\phantomjs-2.1.1-windows\bin\phantomjs.exe”
                str1 = request.getRealPath("/") + "static" + File.separator + "phantomjs" + File.separator + "bin" + File.separator + "phantomjs.exe";
            } else if (os != null && os.toLowerCase().indexOf("linux") > -1) {// linux
                str1 = "phantomjs";// 根据phantomjs的安装路径而定，当前是安装在“/root”目录下
            }
            String str2 = request.getRealPath("/") + "static" + File.separator + "screenshot.js";
            process = Runtime.getRuntime().exec(str1 + BLANK // 你的phantomjs.exe路径
                + str2 + BLANK // 就是上文中那段javascript脚本的存放路径
                + printUrl + BLANK // 你的目标url地址
                + str3);// 你的图片输出路径
            map.put("fileName", "");
        } catch (Exception e) {
            map.put("fileName", "");
            e.printStackTrace();
        } finally {
            LOGGER.info("渲染中...");
            if (process != null) {
                try {
                    int times = 0;
                    while (times < 20) {
                        times++;
                        LOGGER.info("渲染时间{}s", times * 0.5);
                        try {
                            Thread.sleep(500);
                            boolean b = getPDF(str3);
                            if (b) {
                                times = 20;
                                map.put("fileName", "pdf_" + formId + "_" + time + ".pdf");
                                LOGGER.info("渲染成功");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // process.waitFor(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                process.destroy();
            }
        }
        return map;
    }

    public boolean getPDF(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @ResponseBody
    @RequestMapping(value = "/mobile/personCommentList")
    public List<Map<String, Object>> personCommentList(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processSerialNumber, @RequestParam String opinionFrameMark) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            listMap = opinion4PositionApi.personCommentList(tenantId, userId, processSerialNumber, "", ItemBoxTypeEnum.DONE.getValue(), opinionFrameMark, "", "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    /**
     * 表单转pdf并打印
     *
     * @param activitiUser
     * @param taskDefKey
     * @param temp_Id
     * @param taskId
     * @param guid
     * @param itemId
     * @param model
     * @return
     */
    @RequestMapping(value = "/show")
    public String show(@RequestParam(required = false) String formId, @RequestParam(required = false) String printUrl, Model model) {
        model.addAttribute("printUrl", printUrl);
        model.addAttribute("formId", formId);
        return "intranet/printPDF";
    }

}
