package net.risesoft.controller.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/ntkoFile")
@Slf4j
public class FileNTKOController {

    private final Y9FileStoreService y9FileStoreService;

    private final PersonApi personApi;

    private final Attachment4PositionApi attachment4PositionApi;

    private final Y9Properties y9Config;

    /**
     * 打开正文
     *
     * @param fileId 正文id
     */
    @RequestMapping(value = "/openDoc")
    public void openDoc(@RequestParam String fileId, @RequestParam String tenantId, HttpServletResponse response, HttpServletRequest request) {
        Y9LoginUserHolder.setTenantId(tenantId);
        AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId).getData();
        ServletOutputStream out = null;
        try {
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
            out = response.getOutputStream();
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
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("关闭流失败", e);
            }
        }
    }

    /**
     * 获取文件
     *
     * @param processSerialNumber 流程编号
     * @param itembox 状态
     * @param taskId 任务id
     * @param browser 浏览器类型
     * @param fileId 文件id
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return String
     */
    @RequestMapping("/showWord")
    public String showWord(@RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String itembox, @RequestParam(required = false) String taskId, @RequestParam(required = false) String browser, @RequestParam(required = false) String fileId,
        @RequestParam String tenantId, @RequestParam(required = false) String userId, @RequestParam(required = false) String positionId, Model model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId).getData();
            String downloadUrl = y9Config.getCommon().getItemAdminBaseUrl() + "/s/" + file.getFileStoreId() + "." + file.getFileType();
            model.addAttribute("fileName", file.getName());
            model.addAttribute("browser", browser);
            model.addAttribute("fileUrl", downloadUrl);
            model.addAttribute("tenantId", tenantId);
            model.addAttribute("userId", userId);
            model.addAttribute("taskId", taskId);
            model.addAttribute("positionId", positionId);
            model.addAttribute("itembox", itembox);
            model.addAttribute("fileId", fileId);
            model.addAttribute("userName", person.getName());
            model.addAttribute("processSerialNumber", processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取文件失败", e);
        }
        return "file/webOfficeNTKO";
    }

    /**
     * 更新附件
     *
     * @param fileId 文件id
     * @param processSerialNumber 流程编号
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return String
     */
    @RequestMapping(value = "/uploadWord", method = RequestMethod.POST)
    public String uploadWord(@RequestParam(required = false) String fileId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String positionId, @RequestParam(required = false) String taskId, @RequestParam String tenantId, @RequestParam String userId,
        HttpServletRequest request) {
        String result = "success:false";
        try {
            LOGGER.debug("*****************fileId={}", fileId);
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            AttachmentModel file = attachment4PositionApi.getFile(tenantId, fileId).getData();
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile multipartFile = multipartRequest.getFile("currentDoc");
            String fullPath = "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, file.getName());
            result = attachment4PositionApi.updateFile(tenantId, userId, positionId, fileId, y9FileStore.getDisplayFileSize(), taskId, y9FileStore.getId()).getData();
        } catch (Exception e) {
            LOGGER.error("更新附件失败", e);
        }
        return result;
    }

}
