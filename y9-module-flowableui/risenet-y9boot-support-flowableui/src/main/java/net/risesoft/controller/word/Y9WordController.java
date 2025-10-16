package net.risesoft.controller.word;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.ToolUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文（后端调用实现）
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/y9Word", produces = MediaType.APPLICATION_JSON_VALUE)
public class Y9WordController {

    private final Y9FileStoreService y9FileStoreService;

    private final Y9WordApi y9WordApi;

    private final DraftApi draftApi;

    private final ProcessParamApi processParamApi;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong 是否套红
     * @param processSerialNumber 流程编号
     */
    @RequestMapping(value = "/deleteWordByIsTaoHong")
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong,
        @RequestParam @NotBlank String processSerialNumber) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载历史版本正文
     *
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param fileType 文件类型
     */
    @FlowableLog(operationName = "下载历史版本正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/downLoadHistoryDoc")
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String fileType,
        HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9WordHistoryModel map = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
        String fileStoreId = map.getFileStoreId();

        try (ServletOutputStream out = response.getOutputStream()) {
            setResponse(response, request, processSerialNumber, fileType);
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    private void setResponse(HttpServletResponse response, HttpServletRequest request, String processSerialNumber,
        String fileType) {
        String userAgent = request.getHeader("User-Agent");
        String title;
        ProcessParamModel processModel =
            processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        title = processModel.getTitle() != null ? processModel.getTitle() : "正文";
        title = ToolUtil.replaceSpecialStr(title);
        title = encodeFileName(title, userAgent);

        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=\"" + title + fileType + "\"");
        if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0") || userAgent.contains("MSIE 7.0")) {
            response.setHeader("Content-type", "text/html;charset=GBK");
        } else {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
        }
    }

    /**
     * 根据浏览器类型对文件名进行编码
     *
     * @param fileName 原始文件名
     * @param userAgent 用户代理字符串
     * @return 编码后的文件名
     */
    private String encodeFileName(String fileName, String userAgent) {
        try {
            if (userAgent.contains("Firefox")) {
                return "=?UTF-8?B?"
                    + new String(
                        org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))
                    + "?=";
            } else if (userAgent.contains("MSIE 8.0") || userAgent.contains("MSIE 6.0")
                || userAgent.contains("MSIE 7.0")) {
                return java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            } else {
                String encodedName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                return StringUtils.replace(encodedName, "+", "%20"); // 替换空格
            }
        } catch (Exception e) {
            LOGGER.error("文件名编码异常", e);
            return fileName;
        }
    }

    /**
     * 下载正文
     *
     * @param id 正文id
     */
    @FlowableLog(operationName = "下载正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, HttpServletResponse response,
        HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            setResponse(response, request, processSerialNumber, fileType);
            y9FileStoreService.downloadFileToOutputStream(id, out);
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 下载正文（抄送件）
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     */
    @FlowableLog(operationName = "下载正文（抄送件）", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, HttpServletResponse response,
        HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Y9WordModel map = y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String fileStoreId = map.getFileStoreId();
            setResponse(response, request, processSerialNumber, fileType);
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 获取正文信息
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getUpdateWord")
    public Y9WordModel getUpdateWord(@RequestParam String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
    }

    /**
     * 新建正文空白模板
     */
    @FlowableLog(operationName = "新建正文空白模板", operationType = FlowableOperationTypeEnum.ADD)
    @RequestMapping("/openBlankWordTemplate")
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "tags"
            + File.separator + "template" + File.separator + "blankTemplate.doc";
        File file = new File(filePath);
        try (ServletOutputStream out = response.getOutputStream(); FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bi = IOUtils.buffer(fs)) {
            String fileName = file.getName();
            String agent = request.getHeader("USER-AGENT");
            fileName = encodeFileName(fileName, agent);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            int b;
            byte[] by = new byte[1024];
            while ((b = bi.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (Exception e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 打开正文
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param bindValue 绑定值
     *
     */
    @RequestMapping(value = "/openDocument")
    public void openDocument(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String bindValue, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String y9FileStoreId =
            y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, bindValue).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文异常", e);
            }
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (IOException e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 获取套红模板
     *
     * @param templateGUID 模板GUID
     */
    @RequestMapping(value = "/openTaohongTemplate")
    public void openDocumentTemplate(@RequestParam String templateGUID, @RequestParam String processSerialNumber,
        HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");// 删除未套红的正文
        String content = y9WordApi.openDocumentTemplate(tenantId, userId, templateGUID).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            byte[] result;
            result = java.util.Base64.getDecoder().decode(content);
            ByteArrayInputStream bin = new ByteArrayInputStream(result);
            int b;
            byte[] by = new byte[1024];
            response.reset();
            response.setHeader("Content-Type", "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 打开历史版本
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam String taskId, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        Y9WordHistoryModel history =
            y9WordApi.findHistoryVersionDoc(Y9LoginUserHolder.getTenantId(), userId, taskId).getData();
        String fileStoreId = history.getFileStoreId();
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9FileStore y9FileStore = y9FileStoreService.getById(fileStoreId);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(fileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文异常", e);
            }
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (IOException e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber 流程实编号
     */
    @RequestMapping(value = "/openPdf")
    public void openPdf(@RequestParam String processSerialNumber, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String y9FileStoreId = y9WordApi.openPdf(tenantId, userId, processSerialNumber).getData();

        try (ServletOutputStream out = response.getOutputStream()) {
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文异常", e);
            }
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            response.reset();
            if (buf != null) {
                response.setHeader("Content-Length", String.valueOf(buf.length));
            }
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                    out.flush();
                }
            }
        } catch (IOException e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 打开撤销PDF后的正文
     *
     * @param processSerialNumber 流程实编号
     * @param istaohong 是否套红
     */
    @RequestMapping(value = "/openRevokePDFAfterDocument")
    public void openRevokePDFAfterDocument(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String istaohong, HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 删除转PDF的文件
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId =
            y9WordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong).getData();

        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader("USER-AGENT");
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            String fileName = y9FileStore.getFileName();
            fileName = encodeFileName(fileName, agent);
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            } catch (Exception e) {
                LOGGER.error("下载正文异常", e);
            }
            ByteArrayInputStream bin = null;
            if (buf != null) {
                bin = new ByteArrayInputStream(buf);
            }
            int b;
            byte[] by = new byte[1024];
            if (bin != null) {
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (IOException e) {
            LOGGER.error("下载正文异常", e);
        }
    }

    /**
     * 保存word转PDF的正文
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程实编号
     * @param taskId 任务id
     * @return String
     */
    @FlowableLog(operationName = "转PDF", operationType = FlowableOperationTypeEnum.SAVE)
    @RequestMapping(value = "/saveAsPDFFile")
    public String saveAsPDFFile(@RequestParam(required = false) String fileType,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title;
        String result = "success:false";
        try {
            String documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            Boolean result2 =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result2)) {
                result = "success:true";
            }
        } catch (Exception e) {
            LOGGER.error("保存正文异常", e);
        }
        return result;
    }

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid 当前办公局guid
     * @return List<Map < String, Object>>
     */
    @RequestMapping(value = "/list")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String choiceDeptId = Y9LoginUserHolder.getDeptId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(currentBureauGuid)) {
            if (StringUtils.isNotBlank(currentBureauGuid)) {
                currentBureauGuid = person.getParentId();
            } else {
                currentBureauGuid = choiceDeptId;
            }
        }
        return y9WordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param file 文件
     * @return Map<String, Object>
     */
    @FlowableLog(operationName = "上传正文", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            if (fileName != null && fileName.contains("\\")) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
            }
            String fileType = "";// 文件类型
            if (fileName != null) {
                fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            }
            if (!(fileType.equals(".doc") || fileType.equals(".docx") || fileType.equals(".pdf")
                || fileType.equals(".tif"))) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return map;
            }
            String isTaoHong;
            if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                isTaoHong = "2";
            } else {
                isTaoHong = "0";
            }
            String documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            String title = documentTitle != null ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, title + fileType);
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result)) {
                map.put(UtilConsts.SUCCESS, true);
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                    map.put("isPdf", true);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("上传正文失败", e);
        }
        return map;
    }

    /**
     * 办件保存正文
     *
     * @param fileType 文件类型
     * @param isTaoHong 套红
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param docCategory 文档类别
     * @param request 请求
     * @return String
     */
    @FlowableLog(operationName = "保存正文", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/uploadWord")
    public String uploadWord(@RequestParam(required = false) String fileType,
        @RequestParam(required = false) String isTaoHong, @RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String docCategory, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String title;
        String result = "success:false";
        try {
            String documentTitle;
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel model = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                documentTitle = model.getTitle();
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                documentTitle = processModel.getTitle();
            }
            title = documentTitle != null ? documentTitle : "正文";
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, title + fileType);
            Boolean result2 =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, docCategory, taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            if (Boolean.TRUE.equals(result2)) {
                result = "success:true";
            }
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
        }
        return result;
    }
}