package net.risesoft.controller.mobile;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 附件，正文接口
 *
 * @author 10858
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/attachment")
public class MobileAttachmentController {

    private final Y9FileStoreService y9FileStoreService;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TransactionWordApi transactionWordApi;

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param id 附件id
     */
    @RequestMapping(value = "/download")
    public void attachmentDownload(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String id,
        HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            AttachmentModel model = attachment4PositionApi.findById(tenantId, id).getData();
            String filename = model.getName();
            String fileStoreId = model.getFileStoreId();
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
            }
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            OutputStream out = response.getOutputStream();
            y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("附件下载失败", e);
        }
    }

    /**
     * 附件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/list")
    public void attachmentList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String fileSource, int page,
        int rows, HttpServletResponse response) {
        Y9Page<AttachmentModel> y9Page =
            attachment4PositionApi.getAttachmentList(tenantId, processSerialNumber, fileSource, page, rows);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Page));
    }

    /**
     * 附件上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     */
    @RequestMapping(value = "/upload")
    public void attachmentUpload(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String describes,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String fileSource,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isNotEmpty(describes)) {
            describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
        }
        String originalFilename = file.getOriginalFilename();
        String fileName = FilenameUtils.getName(originalFilename);
        if (fileName != null) {
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
        }
        String fullPath =
            Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "attachmentFile", processSerialNumber);
        try {
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            Y9Result<String> y9Result =
                attachment4PositionApi.upload(tenantId, userId, positionId, fileName, y9FileStore.getDisplayFileSize(),
                    processInstanceId, taskId, describes, processSerialNumber, fileSource, y9FileStore.getId());
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
        } catch (Exception e) {
            e.printStackTrace();
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(Y9Result.failure("上传失败")));
        }
    }

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param ids 附件ids
     */
    @RequestMapping(value = "/delFile")
    public void delFile(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        attachment4PositionApi.delFile(tenantId, ids);
    }

    /**
     * 正文下载
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     */
    @RequestMapping(value = "/downloadWord")
    public void downloadWord(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String itemId, HttpServletResponse response, HttpServletRequest request) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            TransactionWordModel fileDocument =
                transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String filename = fileDocument.getFileName() != null ? fileDocument.getFileName() : "正文.doc";
            String fileStoreId =
                transactionWordApi.openDocument(tenantId, userId, processSerialNumber, itemId).getData();
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);// IE浏览器
            }
            if (StringUtils.isNotBlank(fileStoreId)) {
                response.reset();
                response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                response.setContentType("application/octet-stream");
                OutputStream out = response.getOutputStream();
                y9FileStoreService.downloadFileToOutputStream(fileStoreId, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            LOGGER.error("正文下载失败", e);
        }
    }

    /**
     * 正文上传
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param documentTitle 文件标题
     * @param file 文件
     * @param fileType 文件类型
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return String
     */
    @RequestMapping(value = "/uploadWord")
    public String uploadWord(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam(required = false) String documentTitle,
        @RequestParam(required = false) MultipartFile file, @RequestParam(required = false) String fileType,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String taskId) {
        try {
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "word", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, "正文.doc");
            Boolean result = transactionWordApi.uploadWord(tenantId, userId, documentTitle, fileType,
                processSerialNumber, "0", taskId, y9FileStore.getDisplayFileSize(), y9FileStore.getId()).getData();
            if (Boolean.TRUE.equals(result)) {
                return "上传成功";
            }
        } catch (Exception e) {
            LOGGER.error("正文上传失败", e);
        }
        return "正文上传失败";
    }
}
