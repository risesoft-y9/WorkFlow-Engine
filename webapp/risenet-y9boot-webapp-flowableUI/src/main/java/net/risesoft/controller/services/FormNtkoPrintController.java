package net.risesoft.controller.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.api.itemadmin.PrintApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.SysVariables;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping(value = "/services/print")
@Slf4j
public class FormNtkoPrintController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private PrintApi printManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private DraftApi draftManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private OpinionApi opinionManager;

    @Autowired
    private ItemOpinionFrameBindApi itemOpinionFrameBindManager;

    /**
     * 下载正文
     *
     * @param id
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam(required = false) String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            Object documentTitle = null;
            String[] pId = processInstanceId.split(",");
            processInstanceId = pId[0];
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap =
                    draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title").toString();
            } else {
                ProcessParamModel processModel =
                    processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue())
                || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?"
                        + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8"))))
                        + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getPrintLayuiTempalteData")
    public Map<String, Object> getPrintLayuiTempalteData(@RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String itembox, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, String itemId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = formDataManager.getData(tenantId, itemId, processSerialNumber);

        // 意见框
        List<Map<String, Object>> opinioListMap = new ArrayList<Map<String, Object>>();
        List<ItemOpinionFrameBindModel> opinionFrameList = itemOpinionFrameBindManager.findByItemId(tenantId, itemId);
        for (ItemOpinionFrameBindModel opinionFrame : opinionFrameList) {
            Map<String, Object> opinionMap = new HashMap<String, Object>(16);
            String opinionFrameMark = opinionFrame.getOpinionFrameMark();
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            listMap = opinionManager.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox,
                opinionFrameMark, itemId, taskDefKey, activitiUser);
            opinionMap.put("opinionFrameMark", opinionFrameMark);
            opinionMap.put("opinionFrameName", opinionFrame.getOpinionFrameName());
            opinionMap.put("opinionList", listMap);
            if (!opinioListMap.contains(opinionMap)) {
                opinioListMap.add(opinionMap);
            }
        }
        map.put("opinionListMap", opinioListMap);
        String str = Y9JsonUtil.writeValueAsString(map);
        LOGGER.debug("打印数据：{}", str);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/getPrintTempalteData")
    public Map<String, Object> getPrintTempalteData(@RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String formIds,
        @RequestParam(required = false) String formNames, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String itembox,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        // 表单数据
        List<String> formIdList = Y9Util.stringToList(formIds, SysVariables.COMMA);
        List<String> formNameList = Y9Util.stringToList(formNames, SysVariables.COMMA);
        List<Map<String, Object>> formListMap = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < formIdList.size(); i++) {
            Map<String, Object> formMap = new HashMap<String, Object>(16);
            formMap.put("formId", formIdList.get(i));
            formMap.put("formName", formNameList.get(i));
            // dataMap = templateService.getFromData(formIdList.get(i),
            // processSerialNumber);
            formMap.put("fromData", dataMap);
            formListMap.add(formMap);
        }
        map.put("formDataListMap", dataMap);

        if (taskId.indexOf(PunctuationConsts.COMMA) != -1) {
            String[] id = taskId.split(",");
            taskId = id[0];
        }

        // 意见框
        List<Map<String, Object>> opinioListMap = new ArrayList<Map<String, Object>>();
        List<ItemOpinionFrameBindModel> opinionFrameList = itemOpinionFrameBindManager.findByItemId(tenantId, itemId);
        for (ItemOpinionFrameBindModel opinionFrame : opinionFrameList) {
            Map<String, Object> opinionMap = new HashMap<String, Object>(16);
            String opinionFrameMark = opinionFrame.getOpinionFrameMark();
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            listMap = opinionManager.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox,
                opinionFrameMark, itemId, taskDefKey, activitiUser);
            opinionMap.put("opinionFrameMark", opinionFrameMark);
            opinionMap.put("opinionFrameName", opinionFrame.getOpinionFrameName());
            opinionMap.put("opinionList", listMap);
            if (!opinioListMap.contains(opinionMap)) {
                opinioListMap.add(opinionMap);
            }
        }
        map.put("opinionListMap", opinioListMap);
        return map;
    }

    /**
     * 打开正文
     *
     * @param processSerialNumber
     * @param itemId
     */
    @RequestMapping(value = "/openDoc")
    @ResponseBody
    public void openDoc(@RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = transactionWordManager.openDocument(tenantId, userId, processSerialNumber, itemId);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                fileName = "=?UTF-8?B?"
                    + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayInputStream bin = new ByteArrayInputStream(buf);
            int b = 0;
            byte[] by = new byte[1024];
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @ResponseBody
    @RequestMapping(value = "/openDocument")
    public void openDocument(String itemId, String tenantId, String userId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = printManager.openDocument(tenantId, userId, itemId);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                fileName = "=?UTF-8?B?"
                    + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=printForm.doc");
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ByteArrayInputStream bin = new ByteArrayInputStream(buf);
            int b = 0;
            byte[] by = new byte[1024];
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开工单打印的模板
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
    @RequestMapping(value = "/showPrintTemplate")
    public String showPrintTemplate(@RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String itembox,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String formIds,
        @RequestParam(required = false) String formNames, @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        model.addAttribute("userName", userInfo != null ? userInfo.getName() : "");
        model.addAttribute("activitiUser", activitiUser);
        model.addAttribute("taskDefKey", taskDefKey);
        model.addAttribute("itembox", itembox);
        model.addAttribute("taskId", taskId);
        model.addAttribute("processSerialNumber", processSerialNumber);
        String[] items = itemId.split(",");
        model.addAttribute("itemId", items.length == 0 && items == null ? "" : items[0]);
        model.addAttribute("formIds", formIds);
        model.addAttribute("formNames", formNames);
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        return "print/printWorkOrderTeplate";
    }

}
