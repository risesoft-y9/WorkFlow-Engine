package net.risesoft.controller.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.user.UserApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 附件在线编辑
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoFile")
@Slf4j
public class FileNTKOController {

    private final Y9FileStoreService y9FileStoreService;

    private final PersonApi personApi;

    private final AttachmentApi attachmentApi;

    private final UserApi userApi;

    /**
     * 附件中打开正文文件
     *
     * @param fileId 正文id
     */
    @GetMapping(value = "/openDoc")
    public void openDoc(@RequestParam String fileId, @RequestParam String tenantId, HttpServletResponse response,
        HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        AttachmentModel file = attachmentApi.getFile(tenantId, fileId).getData();
        try (ServletOutputStream out = response.getOutputStream()) {
            String agent = request.getHeader("USER-AGENT");
            String fileName = file.getName();
            if (agent.contains("Firefox")) {
                org.apache.commons.codec.binary.Base64.encodeBase64(fileName.getBytes(StandardCharsets.UTF_8));
            } else {
                fileName = java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8);
                StringUtils.replace(fileName, "+", "%20");
            }
            response.reset();
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            byte[] buf = null;
            try {
                buf = y9FileStoreService.downloadFileToBytes(file.getFileStoreId());
            } catch (Exception e) {
                LOGGER.error("下载文件失败", e);
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
            LOGGER.error("下载文件失败", e);
        }
    }

    /**
     * 更新附件列表文件内容
     *
     * @param fileId 文件id
     * @param processSerialNumber 流程编号
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @FlowableLog(operationName = "更新附件列表文件内容", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/uploadWord")
    public String uploadWord(@RequestParam(required = false) String fileId,
        @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String positionId,
        @RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        String result = "success:false";
        try {
            LOGGER.debug("*****************fileId={}", fileId);
            Y9LoginUserHolder.setTenantId(tenantId);
            UserInfo userInfo = userApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setUserInfo(userInfo);
            AttachmentModel file = attachmentApi.getFile(tenantId, fileId).getData();
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
            String fullPath =
                Y9FileStore.buildPath(Y9Context.getSystemName(), tenantId, "attachmentFile", processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, file.getName());
            result = attachmentApi
                .updateFile(tenantId, positionId, fileId, y9FileStore.getDisplayFileSize(), taskId, y9FileStore.getId())
                .getData();
        } catch (Exception e) {
            LOGGER.error("更新附件失败", e);
        }
        return result;
    }
}
