package net.risesoft.controller.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.enums.FileTypeEnum;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping(value = "/services/ntkoForm")
public class FormNtkoController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private DraftApi draftManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong
     * @param processSerialNumber
     * @param response
     */
    @RequestMapping(value = "/deleteWordByIsTaoHong")
    @ResponseBody
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        transactionWordManager.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载正文
     *
     * @param id
     * @param response
     * @param request
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(required = false) String id, @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
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

    /**
     * 下载正文（抄送）
     *
     * @param fileType
     * @param processSerialNumber
     * @param processInstanceId
     * @param tenantId
     * @param userId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downloadCS")
    public void downloadCs(@RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            Map<String, Object> map = transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            String fileStoreId = map.get("fileStoreId").toString();
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
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
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/downLoadHistoryDoc")
    @ResponseBody
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String fileType, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordManager.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            String userAgent = request.getHeader("User-Agent");
            String title = "";
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");
                }
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
            }
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载正文
     *
     * @param id
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam(required = false) String id, @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String tenantId,
        @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            Object documentTitle = null;
            String[] pId = processInstanceId.split(",");
            processInstanceId = pId[0];
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title").toString();
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf(BrowserTypeEnum.IE8.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE6.getValue()) || -1 < userAgent.indexOf(BrowserTypeEnum.IE7.getValue())) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
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

    @RequestMapping(value = "/getUpdateWord")
    @ResponseBody
    public Map<String, Object> getUpdateWord(@RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, @RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        try {
            map = transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 新建正文空白模板
     */
    @RequestMapping("/openBlankWordTemplate")
    @ResponseBody
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "tags" + File.separator + "template" + File.separator + "blankTemplate.doc";
        ServletOutputStream out = null;
        BufferedInputStream bi = null;
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            String agent = request.getHeader("USER-AGENT");
            if (-1 != agent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            FileInputStream fs = new FileInputStream(file);
            bi = IOUtils.buffer(fs);
            out = response.getOutputStream();
            int b = 0;
            byte[] by = new byte[1024];
            while ((b = bi.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bi.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开正文
     *
     * @param processSerialNumber
     * @param itemId
     */
    @RequestMapping(value = "/openDoc")
    @ResponseBody
    public void openDoc(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String itemId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
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
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
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

    /**
     * 套红模板
     *
     * @param templateGUID
     */
    @RequestMapping(value = "/openTaohongTemplate")
    @ResponseBody
    public void openDocumentTemplate(@RequestParam(required = false, name = "templateGUID") String templateGuid, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        transactionWordManager.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");
        String content = transactionWordManager.openDocumentTemplate(tenantId, userId, templateGuid);
        ServletOutputStream out = null;
        try {
            byte[] result = null;
            out = response.getOutputStream();
            result = jodd.util.Base64.decode(content);
            ByteArrayInputStream bin = new ByteArrayInputStream(result);
            int b = 0;
            byte[] by = new byte[1024];
            response.reset();
            response.setHeader("Content-Type", "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
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

    @RequestMapping(value = "/openHistoryVersionDoc")
    @ResponseBody
    public void openHistoryVersionDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordManager.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());

            out = response.getOutputStream();
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
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
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber
     */
    @RequestMapping(value = "/openPdf")
    @ResponseBody
    public void openPdf(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        String y9FileStoreId = transactionWordManager.openPdf(tenantId, userId, processSerialNumber);

        ServletOutputStream out = null;
        try {
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
            response.reset();
            response.setHeader("Content-Length", String.valueOf(buf.length));
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
                out.flush();
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
     * *打开撤销PDF后的正文
     *
     * @param processSerialNumber
     * @param istaohong
     */
    @RequestMapping(value = "/openRevokePDFAfterDocument")
    @ResponseBody
    public void openRevokePdfAfterDocument(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String istaohong, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        // 删除转PDF的文件
        transactionWordManager.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId = transactionWordManager.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf(BrowserTypeEnum.FIREFOX.getValue())) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
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

    /**
     * 选择套红
     *
     * @param activitiUser
     * @param model
     * @return
     */
    @RequestMapping(value = "/openTaoHong")
    public String openTaoHong(@RequestParam String activitiUser, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, Model model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        OrgUnit currentBureau = personApi.getBureau(tenantId, activitiUser);
        model.addAttribute("currentBureauGuid", currentBureau.getId());
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        return "intranet/taohongNTKO";
    }

    /**
     * 保存word转PDF的正文
     *
     * @param fileType
     * @param processSerialNumber
     * @param taskId
     * @return
     */
    @SuppressWarnings("unused")
    @ResponseBody
    @RequestMapping(value = "/saveAsPDFFile")
    public void saveAsPdfFile(@RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title = "";
        String result = "success:false";
        try {
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordManager.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/showHistoryDoc")
    public String showHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, @RequestParam(required = false) String historyFileType, ModelMap model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = transactionWordManager.findHistoryVersionDoc(tenantId, userId, taskId);
        model.putAll(map);
        model.addAttribute("taskId", taskId);
        model.addAttribute("historyFileType", historyFileType);
        return "history/openHistoryDoc";
    }

    /**
     * 获取正文
     *
     * @return
     */
    @RequestMapping("/showWord")
    public String showWord(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String browser, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, Model model) {
        Map<String, Object> map = transactionWordManager.showWord(tenantId, userId, processSerialNumber, itemId, itembox, taskId);
        model.addAllAttributes(map);
        Object documentTitle = null;
        if (StringUtils.isBlank(processInstanceId)) {
            Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
            documentTitle = retMap.get("title");
        } else {
            String[] pInstanceId = processInstanceId.split(",");
            ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, pInstanceId[0]);
            documentTitle = processModel.getTitle();
            processInstanceId = pInstanceId[0];
        }
        model.addAttribute("documentTitle", documentTitle != null ? documentTitle : "正文");
        model.addAttribute("browser", browser);
        model.addAttribute("processInstanceId", processInstanceId);
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("userId", userId);
        return "intranet/webOfficeNTKO";
    }

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid
     * @return
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public List<Map<String, Object>> taoHongTemplateList(@RequestParam String currentBureauGuid, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        if (StringUtils.isBlank(currentBureauGuid)) {
            currentBureauGuid = person.getParentId();
        }
        return transactionWordManager.taoHongTemplateList(tenantId, userId, currentBureauGuid);
    }

    /**
     ** 上传正文
     *
     * @param processSerialNumber
     * @param taskId
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> upload(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId, @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            String fileName = file.getOriginalFilename();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            boolean b = fileName.contains("\\");
            if (b) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            if (!(FileTypeEnum.DOC.getName().equals(fileType) || FileTypeEnum.DOCX.getName().equals(fileType) || FileTypeEnum.PDF.getName().equals(fileType) || FileTypeEnum.TIF.getName().equals(fileType))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong = "";
            if (FileTypeEnum.PDF.getName().equals(fileType) || FileTypeEnum.TIF.getName().equals(fileType)) {
                isTaoHong = "2";
            } else {
                isTaoHong = "0";
            }
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            String result = transactionWordManager.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains(UtilConsts.TRUE)) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType.equals(FileTypeEnum.PDF.getName()) || fileType.equals(FileTypeEnum.TIF.getName())) {
                    map.put("isPdf", true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 草稿箱保存正文
     *
     * @param fileType
     * @param isTaoHong
     * @param processSerialNumber
     * @param processInstanceId
     * @param taskId
     * @param tenantId
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadWord", method = RequestMethod.POST)
    @ResponseBody
    public String uploadWord(@RequestParam(required = false) String fileType, @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String tenantId, @RequestParam(required = false) String userId, HttpServletRequest request, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title = "";
        String result = "success:false";
        try {
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draftManager.getDraftByProcessSerialNumber(tenantId, userId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }

            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordManager.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
