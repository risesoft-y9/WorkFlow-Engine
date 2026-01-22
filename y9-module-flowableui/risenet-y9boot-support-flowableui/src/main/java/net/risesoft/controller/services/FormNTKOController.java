package net.risesoft.controller.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.user.UserApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.Y9DownloadUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 正文相关接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoForm")
@Slf4j
public class FormNTKOController {

    private static final String FIREFOX_KEY = "Firefox";
    private static final String UTF8LINK_KEY = "=?UTF-8?B?";
    private static final String CONTENT_DIS_KEY = "Content-Disposition";
    private static final String USER_AGENT_KEY = "USER-AGENT";
    private static final String PARAM_NULL_KEY = "必要参数为空";
    private static final String FILE_NOT_EXIST_KEY = "文件不存在";
    private static final String OCTET_STREAM = "application/octet-stream";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String SUCCESS_FALSE = "success:false";
    private static final String SAVE_ERROR = "保存正文信息失败";
    private final Y9FileStoreService y9FileStoreService;
    private final PersonApi personApi;
    private final OrgUnitApi orgUnitApi;
    private final ProcessParamApi processParamApi;
    private final DraftApi draftApi;
    private final Y9WordApi y9WordApi;
    private final UserApi userApi;

    /**
     * 删除指定类型的正文
     *
     * @param isTaoHong 是否套红
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @PostMapping(value = "/deleteWordByIsTaoHong")
    public void deleteWordByIsTaoHong(@RequestParam(required = false) String isTaoHong,
        @RequestParam String processSerialNumber, @RequestParam String tenantId, @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, isTaoHong);
    }

    /**
     * 下载历史正文
     *
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param fileType 文件类型
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载历史正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downLoadHistoryDoc")
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String fileType,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        Y9WordHistoryModel model = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
        String fileStoreId = model.getFileStoreId();
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber) + fileType);
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载历史正文失败", e);
        }
    }

    /**
     * 下载正文
     *
     * @param id 文件id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/download")
    public void download(@RequestParam String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            initializeUserContext(tenantId, userId);
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber) + fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败，异常为：", e);
        }
    }

    /**
     * 设置文件下载响应头
     */
    private String getTitle(String processSerialNumber) {
        // 获取流程参数模型
        ProcessParamModel processModel =
            processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        return StringUtils.isNotBlank(processModel.getTitle()) ? processModel.getTitle() : "正文";
    }

    /**
     * 下载正文（抄送）
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文（抄送）", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType, @RequestParam String processSerialNumber,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response,
        HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);
            Y9WordModel word = y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String fileStoreId = word.getFileStoreId();
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber) + fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文失败（抄送），异常为：", e);
        }
    }

    /**
     * 下载正文文件
     *
     * @param id 正文id
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @FlowableLog(operationName = "下载正文文件", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downloadWord")
    public void downloadWord(@RequestParam String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);
            Y9DownloadUtil.setDownloadResponseHeaders(response, request, getTitle(processSerialNumber) + fileType);
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(id, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("下载正文文件失败，异常为：", e);
        }
    }

    /**
     * 获取正文信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @return Map
     */
    @GetMapping(value = "/getUpdateWord")
    public Y9WordModel getUpdateWord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
        return y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
    }

    /**
     * 新建正文空白模板
     */
    @FlowableLog(operationName = "新建正文空白模板", operationType = FlowableOperationTypeEnum.ADD)
    @GetMapping("/openBlankWordTemplate")
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator
                + "tags" + File.separator + "template" + File.separator + "blankTemplate.doc";
            File file = new File(filePath);

            // 验证文件是否存在
            if (!file.exists() || !file.isFile()) {
                LOGGER.warn("空白模板文件不存在: {}", filePath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板文件不存在");
                return;
            }

            try (ServletOutputStream out = response.getOutputStream(); FileInputStream fs = new FileInputStream(file);
                BufferedInputStream bi = IOUtils.buffer(fs)) {

                String fileName = file.getName();
                // 使用统一的文件名编码方法
                setFileDownloadResponse(response, request, fileName);

                int b;
                byte[] by = new byte[1024];
                while ((b = bi.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "新建正文空白模板失败", e);
        }
    }

    /**
     * 打开正文文件
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param bindValue 绑定值
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openDoc")
    public void openDoc(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String bindValue, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            // 验证必要参数
            if (validateRequiredParams(processSerialNumber, itemId, tenantId, userId)) {
                handleFileDownloadError(response, PARAM_NULL_KEY, new IllegalArgumentException(PARAM_NULL_KEY));
                return;
            }
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 获取文件存储ID
            String y9FileStoreId =
                y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, bindValue).getData();
            if (StringUtils.isBlank(y9FileStoreId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 获取文件存储信息
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            if (y9FileStore == null) {
                logFileStoreNotFound(y9FileStoreId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 设置响应头
            setFileDownloadResponse(response, request, "zhengWen." + y9FileStore.getFileExt());
            // 输出文件内容
            try (ServletOutputStream out = response.getOutputStream()) {
                y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
                out.flush();
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "打开正文文件失败", e);
        }
    }

    /**
     * 打开套红模板文件
     *
     * @param templateGUID 模板id
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openTaohongTemplate")
    public void openDocumentTemplate(@RequestParam String templateGUID, @RequestParam String processSerialNumber,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletResponse response) {
        try {
            // 验证必要参数
            if (validateRequiredParams(templateGUID, processSerialNumber, tenantId, userId)) {
                handleFileDownloadError(response, PARAM_NULL_KEY, new IllegalArgumentException(PARAM_NULL_KEY));
                return;
            }
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 删除未套红的正文
            y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");
            // 获取套红模板内容
            String content = y9WordApi.openDocumentTemplate(tenantId, userId, templateGUID).getData();
            if (StringUtils.isBlank(content)) {
                LOGGER.warn("套红模板内容为空: templateGUID={}", templateGUID);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "模板内容不存在");
                return;
            }
            byte[] result = java.util.Base64.getDecoder().decode(content);
            response.reset();
            response.setHeader(CONTENT_TYPE, "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
            // 使用统一的文件下载响应头设置
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=taoHong_template.doc");
            try (ServletOutputStream out = response.getOutputStream();
                ByteArrayInputStream bin = new ByteArrayInputStream(result)) {
                int b;
                byte[] by = new byte[1024];
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
                out.flush();
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "套红正文失败", e);
        }
    }

    /**
     * 打开历史版本正文文件
     *
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam(required = false) String taskId, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response, HttpServletRequest request) {
        try {
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 获取历史版本文档信息
            Y9WordHistoryModel historyModel = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
            if (historyModel == null || StringUtils.isBlank(historyModel.getFileStoreId())) {
                LOGGER.warn("历史版本文档不存在: taskId={}", taskId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "文档不存在");
                return;
            }
            String y9FileStoreId = historyModel.getFileStoreId();
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            if (y9FileStore == null) {
                logFileStoreNotFound(y9FileStoreId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 设置响应头
            setFileDownloadResponse(response, request, "zhengWen." + y9FileStore.getFileExt());
            // 下载并输出文件
            try (ServletOutputStream out = response.getOutputStream()) {
                byte[] fileBytes = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
                if (fileBytes != null && fileBytes.length > 0) {
                    out.write(fileBytes);
                    out.flush();
                }
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "打开历史版本正文文件失败", e);
        }
    }

    /**
     * 设置文件下载响应头
     */
    private void setFileDownloadResponse(HttpServletResponse response, HttpServletRequest request, String fileName) {
        String userAgent = request.getHeader(USER_AGENT_KEY);
        String encodedFileName = encodeFileName(fileName, userAgent);
        response.reset();
        response.setHeader(CONTENT_DIS_KEY, "attachment; filename=" + encodedFileName);
        response.setContentType(OCTET_STREAM);
    }

    /**
     * 编码文件名以适配不同浏览器
     */
    private String encodeFileName(String fileName, String userAgent) {
        if (userAgent.contains(FIREFOX_KEY)) {
            return UTF8LINK_KEY + (new String(Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8)))) + "?=";
        } else {
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            return StringUtils.replace(encodedName, "+", "%20"); // 替换空格
        }
    }

    /**
     * 处理文件下载错误
     */
    private void handleFileDownloadError(HttpServletResponse response, String errorMsg, Exception e) {
        LOGGER.error(errorMsg, e);
        try {
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件下载失败");
            }
        } catch (IOException ioException) {
            LOGGER.error("发送错误响应失败", ioException);
        }
    }

    /**
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber 流程编号
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openPdf")
    public void openPdf(@RequestParam String processSerialNumber, @RequestParam String tenantId,
        @RequestParam String userId, HttpServletResponse response) {
        try {
            // 验证必要参数
            if (validateRequiredParams(processSerialNumber, tenantId, userId)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, PARAM_NULL_KEY);
                return;
            }
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 获取PDF文件存储ID
            String y9FileStoreId = y9WordApi.openPdf(tenantId, userId, processSerialNumber).getData();
            if (StringUtils.isBlank(y9FileStoreId)) {
                LOGGER.warn("PDF文件存储ID为空: processSerialNumber={}", processSerialNumber);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 获取文件存储信息
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            if (y9FileStore == null) {
                logFileStoreNotFound(y9FileStoreId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 设置响应头
            response.reset();
            response.setContentType(OCTET_STREAM);
            response.setHeader("Content-Length", String.valueOf(y9FileStore.getFileSize()));
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=" + y9FileStore.getFileName());
            // 输出文件内容
            try (ServletOutputStream out = response.getOutputStream()) {
                y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
                out.flush();
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "打开PDF或者TIF文件失败", e);
        }
    }

    /**
     * 打开撤销PDF后的正文
     *
     * @param processSerialNumber 流程编号
     * @param istaohong 是否套红
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @GetMapping(value = "/openRevokePDFAfterDocument")
    public void openRevokePDFAfterDocument(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String istaohong, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            // 验证必要参数
            if (validateRequiredParams(processSerialNumber, tenantId, userId)) {
                handleFileDownloadError(response, PARAM_NULL_KEY, new IllegalArgumentException(PARAM_NULL_KEY));
                return;
            }
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 删除转PDF的文件
            y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
            // 获取撤销PDF后的文件存储ID
            String y9FileStoreId =
                y9WordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong).getData();
            if (StringUtils.isBlank(y9FileStoreId)) {
                LOGGER.warn("撤销PDF后的文件存储ID为空: processSerialNumber={}", processSerialNumber);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 获取文件存储信息
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            if (y9FileStore == null) {
                logFileStoreNotFound(y9FileStoreId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, FILE_NOT_EXIST_KEY);
                return;
            }
            // 设置响应头
            setFileDownloadResponse(response, request, y9FileStore.getFileName());
            // 输出文件内容
            try (ServletOutputStream out = response.getOutputStream()) {
                y9FileStoreService.downloadFileToOutputStream(y9FileStoreId, out);
                out.flush();
            }
        } catch (Exception e) {
            handleFileDownloadError(response, "打开撤销PDF后的正文失败", e);
        }
    }

    /**
     * 保存word转PDF的正文
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param isTaoHong 是否套红
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @FlowableLog(operationName = "保存word转PDF的正文", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveAsPDFFile")
    public String saveAsPDFFile(@RequestParam(required = false) String fileType,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, HttpServletRequest request,
        HttpServletResponse response) {
        // 设置响应头
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        try {
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 获取上传文件
            MultipartFile multipartFile = getUploadedFile(request);
            if (multipartFile == null || multipartFile.isEmpty()) {
                logUploadFileEmpty();
                return SUCCESS_FALSE;
            }
            // 获取文档标题
            String title = getDocumentTitle(tenantId, processSerialNumber, processInstanceId);
            // 上传文件
            Y9FileStore y9FileStore = uploadPDFFile(multipartFile, tenantId, processSerialNumber, title, fileType);
            // 保存正文信息
            boolean saveSuccess =
                saveWordInfo(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore);
            return saveSuccess ? "success:true" : SUCCESS_FALSE;
        } catch (Exception e) {
            LOGGER.error("保存word转PDF的正文失败", e);
            return SUCCESS_FALSE;
        }
    }

    /**
     * 获取上传的文件
     */
    private MultipartFile getUploadedFile(HttpServletRequest request) {
        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            LOGGER.warn("请求不是MultipartHttpServletRequest类型");
            return null;
        }
        return multipartRequest.getFile("currentDoc");
    }

    /**
     * 上传PDF文件到文件存储服务
     */
    private Y9FileStore uploadPDFFile(MultipartFile file, String tenantId, String processSerialNumber, String title,
        String fileType) throws Exception {
        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "PDF", processSerialNumber);
        return y9FileStoreService.uploadFile(file.getInputStream(), fullPath, title + fileType);
    }

    /**
     * 获取套红模板列表
     *
     * @param currentBureauGuid 委办局id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return List<Map < String, Object>>
     */
    @GetMapping(value = "/list")
    public List<TaoHongTemplateModel> taoHongTemplateList(@RequestParam(required = false) String currentBureauGuid,
        @RequestParam String tenantId, @RequestParam(required = false) String userId,
        @RequestParam(required = false) String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(currentBureauGuid) && StringUtils.isNotBlank(positionId)) {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            currentBureauGuid = orgUnit.getParentId();
        }
        return y9WordApi.taoHongTemplateList(tenantId, userId, currentBureauGuid).getData();
    }

    /**
     * 上传正文文件
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param file 文件
     * @return Map<String, Object>
     */
    @FlowableLog(operationName = "上传正文文件", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String taskId,
        @RequestParam String tenantId, @RequestParam String userId, @RequestParam MultipartFile file) {
        Map<String, Object> result = createDefaultResult();
        try {
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 处理文件名
            String fileName = Y9DownloadUtil.extractFileName(file.getOriginalFilename());
            if (StringUtils.isBlank(fileName)) {
                return createErrorResult("文件名不能为空");
            }
            // 验证文件类型
            String fileType = getFileExtension(fileName);
            if (Y9DownloadUtil.isValidFileType(fileType)) {
                return createErrorResult("请上传后缀名为.doc,.docx,.pdf,.tif文件");
            }
            // 获取文档标题
            String title = getDocumentTitle(tenantId, processSerialNumber, processInstanceId);
            // 确定是否套红
            String isTaoHong = determineTaoHongStatus(fileType);
            // 上传文件
            Y9FileStore y9FileStore = uploadFile(file, tenantId, processSerialNumber, title, fileType);
            // 保存正文信息
            boolean uploadSuccess =
                saveWordInfo(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore);
            if (uploadSuccess) {
                updateSuccessResult(result, fileType);
            } else {
                return createErrorResult(SAVE_ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
            return createErrorResult("上传正文失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 创建默认返回结果
     */
    private Map<String, Object> createDefaultResult() {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "上传成功");
        return map;
    }

    /**
     * 创建错误返回结果
     */
    private Map<String, Object> createErrorResult(String errorMsg) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", errorMsg);
        return map;
    }

    /**
     * 初始化用户上下文
     */
    private void initializeUserContext(String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return null;
        }
        return fileName.substring(lastDotIndex).toLowerCase();
    }

    /**
     * 获取文档标题
     */
    private String getDocumentTitle(String tenantId, String processSerialNumber, String processInstanceId) {
        try {
            if (StringUtils.isBlank(processInstanceId)) {
                DraftModel draftModel = draftApi.getDraftByProcessSerialNumber(tenantId, processSerialNumber).getData();
                return draftModel != null ? draftModel.getTitle() : "正文";
            } else {
                ProcessParamModel processModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                return processModel != null ? processModel.getTitle() : "正文";
            }
        } catch (Exception e) {
            LOGGER.warn("获取文档标题失败", e);
            return "正文";
        }
    }

    /**
     * 确定套红状态
     */
    private String determineTaoHongStatus(String fileType) {
        if (fileType == null) {
            return "";
        }
        // PDF或TIF文件标记为类型2，其他文档标记为类型0
        return fileType.equals(".pdf") || fileType.equals(".tif") ? "2" : "0";
    }

    /**
     * 上传文件到文件存储服务
     */
    private Y9FileStore uploadFile(MultipartFile file, String tenantId, String processSerialNumber, String title,
        String fileType) throws Exception {
        String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
        return y9FileStoreService.uploadFile(file.getInputStream(), fullPath, title + fileType);
    }

    /**
     * 保存正文信息
     */
    private boolean saveWordInfo(String tenantId, String userId, String title, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, Y9FileStore y9FileStore) {
        try {
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, "", taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LOGGER.error(SAVE_ERROR, e);
            return false;
        }
    }

    /**
     * 更新成功结果
     */
    private void updateSuccessResult(Map<String, Object> result, String fileType) {
        result.put(UtilConsts.SUCCESS, true);
        if (fileType != null && (fileType.equals(".pdf") || fileType.equals(".tif"))) {
            result.put("isPdf", true);
        }
    }

    /**
     * 办件保存正文
     *
     * @param fileType 文件类型
     * @param isTaoHong 是否套红
     * @param docCategory 文档类别
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @FlowableLog(operationName = "保存正文", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/uploadWord")
    public String uploadWord(@RequestParam(required = false) String fileType,
        @RequestParam(required = false) String isTaoHong, @RequestParam(required = false) String docCategory,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        try {
            // 初始化用户上下文
            initializeUserContext(tenantId, userId);
            // 验证必要参数
            if (validateRequiredParams(tenantId, userId, processSerialNumber)) {
                return SUCCESS_FALSE;
            }
            // 获取上传文件
            MultipartFile multipartFile = getUploadedFile(request);
            if (multipartFile == null || multipartFile.isEmpty()) {
                logUploadFileEmpty();
                return SUCCESS_FALSE;
            }
            // 获取文档标题
            String title = getDocumentTitle(tenantId, processSerialNumber, processInstanceId);
            // 上传文件
            Y9FileStore y9FileStore = uploadFile(multipartFile, tenantId, processSerialNumber, title, fileType);
            // 保存正文信息
            boolean saveSuccess = saveWordInfo(tenantId, userId, title, fileType, processSerialNumber, isTaoHong,
                docCategory, taskId, y9FileStore);
            return saveSuccess ? "success:true" : SUCCESS_FALSE;
        } catch (Exception e) {
            LOGGER.error("上传正文失败", e);
            return SUCCESS_FALSE;
        }
    }

    /**
     * 保存正文信息（带文档类别）
     */
    private boolean saveWordInfo(String tenantId, String userId, String title, String fileType,
        String processSerialNumber, String isTaoHong, String docCategory, String taskId, Y9FileStore y9FileStore) {
        try {
            Boolean result =
                y9WordApi
                    .uploadWord(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, docCategory, taskId,
                        y9FileStore.getDisplayFileSize(), y9FileStore.getId())
                    .getData();
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            LOGGER.error(SAVE_ERROR, e);
            return false;
        }
    }

    /**
     * 记录文件存储记录不存在的日志
     */
    private void logFileStoreNotFound(String fileStoreId) {
        LOGGER.warn("文件存储记录不存在: fileStoreId={}", fileStoreId);
    }

    /**
     * 验证必要参数是否为空
     */
    private boolean validateRequiredParams(String... params) {
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 记录上传文件为空的日志
     */
    private void logUploadFileEmpty() {
        LOGGER.warn("上传文件为空");
    }
}
