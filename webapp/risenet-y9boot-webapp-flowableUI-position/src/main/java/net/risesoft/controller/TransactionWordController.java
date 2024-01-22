package net.risesoft.controller;

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

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@Controller
@RequestMapping("/transactionWord")
public class TransactionWordController {

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private TransactionWordApi transactionWordApi;

    @Autowired
    private Draft4PositionApi draft4PositionApi;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessParamApi processParamApi;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong
     * @param processSerialNumber
     * @param response
     */
    @RequestMapping(value = "/deleteWordByIsTaoHong")
    @ResponseBody
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载正文
     *
     * @param id
     * @param response
     * @param request
     */
    @RequestMapping(value = "/download")
    public void download(@RequestParam(required = false) String id, @RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, HttpServletResponse response, HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            // Y9FileStore y9FileStore = y9FileStoreService.getById(id);
            // String fileName = y9FileStore.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf("MSIE 8.0") || -1 < userAgent.indexOf("MSIE 6.0") || -1 < userAgent.indexOf("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
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
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, HttpServletResponse response, HttpServletRequest request) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> map = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            String fileStoreId = map.get("fileStoreId").toString();
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            // Y9FileStore y9FileStore = y9FileStoreService.getById(id);
            // String fileName = y9FileStore.getFileName();
            title = ToolUtil.replaceSpecialStr(title);
            String userAgent = request.getHeader("User-Agent");
            if (-1 < userAgent.indexOf("MSIE 8.0") || -1 < userAgent.indexOf("MSIE 6.0") || -1 < userAgent.indexOf("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
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
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String fileType, HttpServletResponse response,
        HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            // Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            // String fileName = y9FileStore.getFileName();
            String userAgent = request.getHeader("User-Agent");
            String title = "";
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            title = ToolUtil.replaceSpecialStr(title);
            if (-1 < userAgent.indexOf("MSIE 8.0") || -1 < userAgent.indexOf("MSIE 6.0") || -1 < userAgent.indexOf("MSIE 7.0")) {
                title = new String(title.getBytes("gb2312"), "ISO8859-1");
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
                response.setHeader("Content-type", "text/html;charset=GBK");
                response.setContentType("application/octet-stream");
            } else {
                if (-1 != userAgent.indexOf("Firefox")) {
                    title = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(title.getBytes("UTF-8")))) + "?=";
                } else {
                    title = java.net.URLEncoder.encode(title, "UTF-8");
                    title = StringUtils.replace(title, "+", "%20");// 替换空格
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

    @RequestMapping(value = "/getUpdateWord")
    @ResponseBody
    public Map<String, Object> getUpdateWord(@RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            map = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
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
            if (-1 != agent.indexOf("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
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
    @RequestMapping(value = "/openDocument")
    @ResponseBody
    public void openDocument(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String itemId, HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        String y9FileStoreId = transactionWordApi.openDocument(tenantId, userId, processSerialNumber, itemId);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
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
    public void openDocumentTemplate(@RequestParam(required = false) String templateGUID, @RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");// 删除未套红的正文
        String content = transactionWordApi.openDocumentTemplate(tenantId, userId, templateGUID);
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
    public void openHistoryVersionDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String itemId, HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
        String fileStoreId = map.get("fileStoreId").toString();
        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
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
    public void openPdf(@RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        String y9FileStoreId = transactionWordApi.openPdf(tenantId, userId, processSerialNumber);

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
            // response.setHeader("Content-Type", "application/msword");
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
    public void openRevokePDFAfterDocument(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String istaohong, HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        // 删除转PDF的文件
        transactionWordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId = transactionWordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong);

        ServletOutputStream out = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            if (-1 != agent.indexOf("Firefox")) {
                fileName = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName = StringUtils.replace(fileName, "+", "%20");// 替换空格
            }
            response.reset();
            // response.setHeader("Content-Type", "application/msword");
            // response.setHeader("Content-Length", String.valueOf(buf.length));
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
    public String openTaoHong(@RequestParam String activitiUser, Model model) {
        OrgUnit currentBureau = personApi.getBureau(Y9LoginUserHolder.getTenantId(), activitiUser).getData();
        model.addAttribute("currentBureauGuid", currentBureau.getId());
        model.addAttribute("tenantId", Y9LoginUserHolder.getTenantId());
        model.addAttribute("userId", Y9LoginUserHolder.getPersonId());
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
    public void saveAsPDFFile(@RequestParam(required = false) String fileType, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title = "";
        String result = "success:false";
        try {
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/showDownPdfTool")
    public String showDownPdfTool() {
        return "intranet/downPdfTool";
    }

    @RequestMapping(value = "/showHistoryDoc")
    public String showHistoryDoc(@RequestParam(required = false) String taskId, @RequestParam(required = false) String historyFileType, ModelMap model) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = transactionWordApi.findHistoryVersionDoc(tenantId, userId, taskId);
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
        @RequestParam(required = false) String browser, Model model) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = transactionWordApi.showWord(tenantId, userId, processSerialNumber, itemId, itembox, taskId);
        model.addAllAttributes(map);
        Object documentTitle = null;
        if (StringUtils.isBlank(processInstanceId)) {
            Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
            documentTitle = retMap.get("title");
        } else {
            String[] pInstanceId = processInstanceId.split(",");
            ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, pInstanceId[0]);
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
    public List<Map<String, Object>> taoHongTemplateList(@RequestParam String currentBureauGuid) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), choiceDeptId = Y9LoginUserHolder.getDeptId(), tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(currentBureauGuid)) {
            if (StringUtils.isNotBlank(currentBureauGuid)) {
                currentBureauGuid = person.getParentId();
            } else {
                currentBureauGuid = choiceDeptId;
            }
        }
        return transactionWordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid);
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
    public Map<String, Object> upload(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId, @RequestParam(required = false) MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            String fileName = file.getOriginalFilename();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            if (fileName.contains("\\")) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();// 文件类型
            if (!(fileType.equals(".doc") || fileType.equals(".docx") || fileType.equals(".pdf") || fileType.equals(".tif"))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong = "";
            if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                isTaoHong = "2";
            } else {
                isTaoHong = "0";
            }
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            String result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
            if (result.contains("true")) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
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
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/uploadWord", method = RequestMethod.POST)
    @ResponseBody
    public String uploadWord(@RequestParam(required = false) String fileType, @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        HttpServletRequest request, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title = "";
        String result = "success:false";
        try {
            Object documentTitle = null;
            if (StringUtils.isBlank(processInstanceId)) {
                Map<String, Object> retMap = draft4PositionApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber);
                documentTitle = retMap.get("title");
            } else {
                ProcessParamModel processModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                documentTitle = processModel.getTitle();
            }

            title = documentTitle != null ? (String)documentTitle : "正文";
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            result = transactionWordApi.uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
