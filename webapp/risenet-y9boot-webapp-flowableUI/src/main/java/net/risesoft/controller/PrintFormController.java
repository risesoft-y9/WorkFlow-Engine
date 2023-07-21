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

import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

import jodd.util.Base64;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping(value = "/print")
@Slf4j
public class PrintFormController {

    @Autowired
    private OpinionApi opinionManager;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 表单转img
     *
     * @param activitiUser
     * @param taskDefKey
     * @param tempId
     * @param taskId
     * @param guid
     * @param itemId
     * @param request
     */
    @SuppressWarnings({"resource"})
    @RequestMapping(value = "/formToImg")
    @ResponseBody
    public Map<String, Object> formToImg(@RequestParam(required = false) String activitiUser, @RequestParam(required = false) String taskDefKey, @RequestParam(required = false, name = "temp_Id") String tempId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String guid, @RequestParam(required = false) String itemId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String blank = "  ";
            String str1 = "phantomjs";
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = sdf.format(date);
            String str2 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "screenshot.js";
            String str3 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "formToImg" + File.separator + "img" + tempId + "-" + time + ".png";
            String url = y9Config.getCommon().getFlowableBaseUrl() + "/eform/engine/getTemplate?temp_Id=" + tempId + "&edittype=1" + "&guid=" + guid + "&taskDefKey=" + taskDefKey + "&activitiUser=" + activitiUser + "&itemId=" + itemId + "&formToPDF=formToPDF" + "&ctx="
                + y9Config.getCommon().getFlowableBaseUrl() + "&tenantId=" + Y9LoginUserHolder.getTenantId() + "&userId=" + userInfo.getPersonId();
            Process process = Runtime.getRuntime().exec(str1 + blank + str2 + blank + url + blank + str3);
            process.getInputStream();
            InputStream input = new FileInputStream(str3);
            byte[] byt = new byte[input.available()];
            input.read(byt);
            String encoded = "data:image/png;base64," + Base64.encodeToString(byt);
            LOGGER.info("渲染成功...");
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
     * @param printUrl
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/formToPDF")
    public Map<String, Object> formToPdf(@RequestParam(required = false) String formId, @RequestParam(required = false) String printUrl, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Process process = null;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(date);
        String str3 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "formToPDF" + File.separator + "pdf_" + formId + "_" + time + ".pdf";
        try {
            String[] json = printUrl.split("jsonData=");
            Map<String, Object> jsonMap = Y9JsonUtil.readHashMap(json[1]);
            printUrl = json[0];
            String processSerialNumber = (String)jsonMap.get("processSerialNumber");
            String processDefinitionId = (String)jsonMap.get("processDefinitionId");
            String taskDefKey = (String)jsonMap.get("taskDefKey");
            String itemId = (String)jsonMap.get("itemId");
            String activitiUser = (String)jsonMap.get("activitiUser");
            String processInstanceId = (String)jsonMap.get("processInstanceId");
            printUrl += "formId=" + formId + "&processSerialNumber=" + processSerialNumber + "&processDefinitionId=" + processDefinitionId + "&taskDefKey=" + taskDefKey + "&itemId=" + itemId + "&activitiUser=" + activitiUser + "&processInstanceId=" + processInstanceId;
            LOGGER.debug("printUrl:{}", printUrl);
            String blank = "  ";
            String str1 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "phantomjs" + File.separator + "bin" + File.separator + "phantomjs.exe";
            String os = System.getProperty("os.name");
            // window
            boolean win = os != null && os.toLowerCase().indexOf("win") > -1;
            boolean lin = os != null && os.toLowerCase().indexOf("linux") > -1;
            if (win) {
                // 根据phantomjs.exe的路径而定，如“C:\Users\Think\Desktop\phantomjs-2.1.1-windows\bin\phantomjs.exe”
                str1 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "phantomjs" + File.separator + "bin" + File.separator + "phantomjs.exe";
            } else if (lin) {
                // linux
                // 根据phantomjs的安装路径而定，当前是安装在“/root”目录下
                str1 = "phantomjs";
            }
            String str2 = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "screenshot.js";
            process = Runtime.getRuntime().exec(str1 + blank + str2 + blank + printUrl + blank + str3);
            map.put("fileName", "");
        } catch (Exception e) {
            map.put("fileName", "");
            e.printStackTrace();
        } finally {
            LOGGER.info("渲染中...");
            if (process != null) {
                try {
                    int times = 0, timesTemp = 20;
                    while (times < timesTemp) {
                        times++;
                        LOGGER.info("渲染时间 {} s", times * 0.5);
                        try {
                            Thread.sleep(500);
                            boolean b = getPdf(str3);
                            if (b) {
                                times = 20;
                                map.put("fileName", "pdf_" + formId + "_" + time + ".pdf");
                                LOGGER.info("渲染成功");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                process.destroy();
            }
        }
        return map;
    }

    public boolean getPdf(String path) {
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
            listMap = opinionManager.personCommentList(tenantId, userId, processSerialNumber, "", ItemBoxTypeEnum.DONE.getValue(), opinionFrameMark, "", "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    /**
     * 表单转pdf并打印
     *
     * @param formId 表单id
     * @param printUrl 打印url
     * @param model 模型
     * @return {@link String}
     */
    @RequestMapping(value = "/show")
    public String show(@RequestParam(required = false) String formId, @RequestParam(required = false) String printUrl, Model model) {
        model.addAttribute("printUrl", printUrl);
        model.addAttribute("formId", formId);
        return "intranet/printPDF";
    }

}
