package net.risesoft.controller.word;

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
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
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
import net.risesoft.consts.UtilConsts;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.TaoHongTemplateModel;
import net.risesoft.model.itemadmin.Y9WordHistoryModel;
import net.risesoft.model.itemadmin.Y9WordModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.Y9DownloadUtil;
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

    private static final String CONTENT_DIS_KEY = "Content-Disposition";
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
    @GetMapping(value = "/deleteWordByIsTaoHong")
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
    @GetMapping(value = "/downLoadHistoryDoc")
    public void downLoadHistoryDoc(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String fileType,
        HttpServletResponse response, HttpServletRequest request) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9WordHistoryModel map = y9WordApi.findHistoryVersionDoc(tenantId, userId, taskId).getData();
        String fileStoreId = map.getFileStoreId();
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9DownloadUtil.setDownloadResponseHeaders(response, request,
                getDocumentTitle(tenantId, processSerialNumber) + fileType);
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载历史版本正文文件异常，异常：", e);
        }
    }

    /**
     * 下载正文
     *
     * @param id 正文id
     */
    @FlowableLog(operationName = "下载正文", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, @RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, HttpServletResponse response,
        HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            Y9DownloadUtil.setDownloadResponseHeaders(response, request,
                getDocumentTitle(Y9LoginUserHolder.getTenantId(), processSerialNumber) + fileType);
            y9FileStoreService.downloadFileToOutputStream(id, out);
        } catch (Exception e) {
            LOGGER.error("下载正文文件异常，异常：", e);
        }
    }

    /**
     * 下载正文（抄送件）
     *
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     */
    @FlowableLog(operationName = "下载正文（抄送件）", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/downloadCS")
    public void downloadCS(@RequestParam(required = false) String fileType,
        @RequestParam(required = false) String processSerialNumber, HttpServletResponse response,
        HttpServletRequest request) {
        try (OutputStream out = response.getOutputStream()) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Y9WordModel map = y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String fileStoreId = map.getFileStoreId();
            Y9DownloadUtil.setDownloadResponseHeaders(response, request,
                getDocumentTitle(tenantId, processSerialNumber) + fileType);
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
        } catch (Exception e) {
            LOGGER.error("下载正文（抄送件）异常，异常：", e);
        }
    }

    /**
     * 获取正文信息
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getUpdateWord")
    public Y9WordModel getUpdateWord(@RequestParam String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return y9WordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
    }

    /**
     * 新建正文空白模板
     */
    @FlowableLog(operationName = "新建正文空白模板", operationType = FlowableOperationTypeEnum.ADD)
    @GetMapping("/openBlankWordTemplate")
    public void openBlankWordTemplate(HttpServletRequest request, HttpServletResponse response) {
        String filePath = request.getSession().getServletContext().getRealPath("/") + "static" + File.separator + "tags"
            + File.separator + "template" + File.separator + "blankTemplate.doc";
        File file = new File(filePath);
        try (ServletOutputStream out = response.getOutputStream(); FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bi = IOUtils.buffer(fs)) {
            String fileName = file.getName();
            String agent = request.getHeader("USER-AGENT");
            fileName = Y9DownloadUtil.encodeFileName(fileName, agent);
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=" + fileName);
            int b;
            byte[] by = new byte[1024];
            while ((b = bi.read(by)) != -1) {
                out.write(by, 0, b);
            }
        } catch (Exception e) {
            LOGGER.error("打开新建正文空白模板异常，异常：", e);
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
    @GetMapping(value = "/openDocument")
    public void openDocument(@RequestParam String processSerialNumber, @RequestParam String itemId,
        @RequestParam(required = false) String bindValue, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String y9FileStoreId =
            y9WordApi.openDocument(tenantId, userId, processSerialNumber, itemId, bindValue).getData();
        writeBytesToOutputStream(response, y9FileStoreId);
    }

    /**
     * 获取套红模板
     *
     * @param templateGUID 模板GUID
     */
    @GetMapping(value = "/openTaohongTemplate")
    public void openDocumentTemplate(@RequestParam String templateGUID, @RequestParam String processSerialNumber,
        HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "0");// 删除未套红的正文
        String content = y9WordApi.openDocumentTemplate(tenantId, userId, templateGUID).getData();
        try {
            writeBase64ToResponse(response, content);
        } catch (IOException e) {
            LOGGER.error("获取套红模板文件异常，异常：", e);
        }
    }

    /**
     * 将Base64编码的内容写入HTTP响应流
     *
     * @param response HTTP响应对象
     * @param base64Content Base64编码的内容
     * @throws IOException IO异常
     */
    private void writeBase64ToResponse(HttpServletResponse response, String base64Content) throws IOException {
        byte[] result = java.util.Base64.getDecoder().decode(base64Content);
        try (ServletOutputStream out = response.getOutputStream();
            ByteArrayInputStream bin = new ByteArrayInputStream(result)) {
            response.reset();
            response.setHeader("Content-Type", "application/msword");
            response.setHeader("Content-Length", String.valueOf(result.length));
            int b;
            byte[] by = new byte[1024];
            while ((b = bin.read(by)) != -1) {
                out.write(by, 0, b);
            }
        }
    }

    /**
     * 打开历史版本
     *
     * @param taskId 任务id
     */
    @GetMapping(value = "/openHistoryVersionDoc")
    public void openHistoryVersionDoc(@RequestParam String taskId, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        Y9WordHistoryModel history =
            y9WordApi.findHistoryVersionDoc(Y9LoginUserHolder.getTenantId(), userId, taskId).getData();
        writeBytesToOutputStream(response, history.getFileStoreId());
    }

    /**
     * 将输入的字节数组写入输出流
     *
     * @param response 响应
     * @param y9FileStoreId 文件存储id
     */
    private void writeBytesToOutputStream(HttpServletResponse response, String y9FileStoreId) {
        try (ServletOutputStream out = response.getOutputStream()) {
            Y9FileStore y9FileStore = y9FileStoreService.getById(y9FileStoreId);
            response.reset();
            response.setHeader(CONTENT_DIS_KEY, "attachment; filename=zhengwen." + y9FileStore.getFileExt());
            byte[] buf = y9FileStoreService.downloadFileToBytes(y9FileStoreId);
            if (buf != null) {
                ByteArrayInputStream bin = new ByteArrayInputStream(buf);
                int b;
                byte[] by = new byte[1024];
                while ((b = bin.read(by)) != -1) {
                    out.write(by, 0, b);
                }
            }
        } catch (Exception e) {
            LOGGER.error("打开正文异常y9FileStoreId={}，异常：", y9FileStoreId, e);
        }
    }

    /**
     * 打开PDF或者TIF文件
     *
     * @param processSerialNumber 流程实编号
     */
    @GetMapping(value = "/openPdf")
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
                LOGGER.error("下载PDF文件byte数据异常，异常：", e);
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
            LOGGER.error("打开PDF或TIF文件异常，异常：", e);
        }
    }

    /**
     * 打开撤销PDF后的正文
     *
     * @param processSerialNumber 流程实编号
     * @param istaohong 是否套红
     */
    @GetMapping(value = "/openRevokePDFAfterDocument")
    public void openRevokePDFAfterDocument(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String istaohong, HttpServletResponse response) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 删除转PDF的文件
        y9WordApi.deleteByIsTaoHong(tenantId, userId, processSerialNumber, "3");
        String y9FileStoreId =
            y9WordApi.openRevokePdfAfterDocument(tenantId, userId, processSerialNumber, istaohong).getData();
        writeBytesToOutputStream(response, y9FileStoreId);
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
    @PostMapping(value = "/saveAsPDFFile")
    public String saveAsPDFFile(@RequestParam(required = false) String fileType,
        @RequestParam String processSerialNumber, @RequestParam(required = false) String isTaoHong,
        @RequestParam(required = false) String taskId, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
        String result = "success:false";
        try {
            String title = getDocumentTitle(tenantId, processSerialNumber);
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
    @GetMapping(value = "/list")
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
     * 上传正文
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param file 文件
     * @return Map<String, Object>
     */
    @FlowableLog(operationName = "上传正文", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Map<String, Object> upload(@RequestParam String processSerialNumber,
        @RequestParam(required = false) String taskId, @RequestParam MultipartFile file) {
        Map<String, Object> result = new HashMap<>(16);
        result.put(UtilConsts.SUCCESS, false);
        result.put("msg", "上传失败");
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 处理文件名
            String fileName = Y9DownloadUtil.extractFileName(file.getOriginalFilename());
            // 验证文件类型
            String fileType = getFileExtension(fileName);
            if (Y9DownloadUtil.isValidFileType(fileType)) {
                result.put("msg", "请上传后缀名为.doc,.docx,.pdf,.tif文件");
                return result;
            }
            // 获取文档标题
            String title = getDocumentTitle(tenantId, processSerialNumber);
            // 确定是否套红
            String isTaoHong = determineTaoHongStatus(fileType);
            // 上传文件
            Y9FileStore y9FileStore = uploadFile(file, tenantId, processSerialNumber, title, fileType);
            // 保存正文信息
            boolean uploadSuccess =
                saveWordInfo(tenantId, userId, title, fileType, processSerialNumber, isTaoHong, taskId, y9FileStore);
            if (uploadSuccess) {
                result.put(UtilConsts.SUCCESS, true);
                result.put("msg", "上传成功");
                if (fileType.equals(".pdf") || fileType.equals(".tif")) {
                    result.put("isPdf", true);
                }
            } else {
                result.put("msg", "保存正文信息失败");
            }
        } catch (Exception e) {
            result.put("msg", "上传正文失败: " + e.getMessage());
        }
        return result;
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

    /**
     * 获取文档标题
     */
    private String getDocumentTitle(String tenantId, String processSerialNumber) {
        try {
            ProcessParamModel processModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            return processModel != null ? processModel.getTitle() : "正文";
        } catch (Exception e) {
            LOGGER.warn("获取文档标题失败，使用默认标题", e);
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
        return y9FileStoreService.uploadFile(file, fullPath, title + fileType);
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
            LOGGER.error("保存正文信息失败", e);
            return false;
        }
    }
}