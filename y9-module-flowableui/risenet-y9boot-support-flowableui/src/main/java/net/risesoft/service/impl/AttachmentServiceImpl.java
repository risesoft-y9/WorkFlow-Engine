package net.risesoft.service.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.dto.itemadmin.IdsDTO;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.enums.FlowableUiAuditLogEnum;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AttachmentService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 附件服务实现类
 *
 * @author yihong
 * @date 2026/04/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final Y9FileStoreService y9FileStoreService;

    private final AttachmentApi attachmentApi;

    private final ProcessParamApi processParamApi;

    @Override
    @Transactional
    public void download(String id, String fileName, OutputStream out) {
        try {
            y9FileStoreService.downloadFileToOutputStream(id, out);
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.ATTACHMENT_DOWNLOAD.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.ATTACHMENT_DOWNLOAD.getDescription(), fileName))
                .objectId(id)
                .oldObject(fileName)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("附件下载失败", e);
        }
    }

    @Override
    @Transactional
    public Y9Result<String> delFile(IdsDTO deleteEntityDTO) {
        attachmentApi.delFile(deleteEntityDTO);
        return Y9Result.successMsg("删除成功");
    }

    @Override
    @Transactional
    public void packDownload(String processSerialNumber, String fileSource, HttpServletResponse response,
        HttpServletRequest request) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Y9Page<AttachmentModel> y9Page =
                attachmentApi.getAttachmentList(tenantId, processSerialNumber, fileSource, 1, 100);
            List<AttachmentModel> list = y9Page.getRows();
            // 拼接zip文件,之后下载下来的压缩文件的名字
            String base_name = "附件" + new Date().getTime();
            String fileZip = base_name + ".zip";
            String packDownloadPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "packDownload";
            File folder = new File(packDownloadPath);
            if (!folder.exists() && !folder.isDirectory() && !folder.mkdirs()) {
                LOGGER.error("创建目录失败：{}", packDownloadPath);
            }
            String zipPath = packDownloadPath + File.separator + fileZip;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipPath));
            ZipOutputStream zos = new ZipOutputStream(bos);
            ZipEntry ze;
            for (AttachmentModel file : list) {
                String filename = file.getName();
                String fileStoreId = file.getFileStoreId();
                byte[] fileByte = y9FileStoreService.downloadFileToBytes(fileStoreId);
                InputStream bis = new ByteArrayInputStream(fileByte);
                ze = new ZipEntry(filename);
                zos.putNextEntry(ze);
                int s;
                while ((s = bis.read()) != -1) {
                    zos.write(s);
                }
                bis.close();
            }
            zos.flush();
            zos.close();
            boolean b = request.getHeader("User-Agent").toLowerCase().contains(BrowserTypeEnum.FIREFOX.getValue());
            if (b) {
                fileZip = new String(fileZip.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            } else {
                fileZip = URLEncoder.encode(fileZip, StandardCharsets.UTF_8);
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileZip + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            // 浏览器下载临时文件的路径
            DataInputStream in = new DataInputStream(new FileInputStream(zipPath));
            byte[] byte1 = new byte[2048];
            // 之后用来删除临时压缩文件
            File reportZip = new File(zipPath);
            try {
                while ((in.read(byte1)) != -1) {
                    out.write(byte1);
                }
                out.flush();
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(FlowableUiAuditLogEnum.ATTACHMENT_PACK_DOWNLOAD.getAction())
                    .description(Y9StringUtil.format(FlowableUiAuditLogEnum.ATTACHMENT_PACK_DOWNLOAD.getDescription(),
                        processParamModel.getTitle()))
                    .objectId(processSerialNumber)
                    .oldObject(list)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            } catch (Exception e) {
                LOGGER.error("下载失败", e);
            } finally {
                if (out != null) {
                    out.close();
                }
                in.close();
                // 删除服务器本地产生的临时压缩文件
                try {
                    Files.delete(reportZip.toPath());
                } catch (IOException e) {
                    LOGGER.warn("删除服务器本地产生的临时压缩文件失败！{}", reportZip.getAbsolutePath(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("下载失败", e);
        }
    }

    @Override
    public Y9Result<String> upload(MultipartFile file, String processInstanceId, String taskId, String describes,
        String processSerialNumber, String fileSource) {
        String userId = Y9LoginUserHolder.getUserInfo().getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isNotBlank(describes)) {
                describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                String.format("/%s/%s/attachmentFile/%s", Y9Context.getSystemName(), tenantId, processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file.getInputStream(), fullPath, fileName);
            String storeId = y9FileStore.getId();
            String fileSize =
                Y9FileUtil.getDisplayFileSize(y9FileStore.getFileSize() != null ? y9FileStore.getFileSize() : 0);
            return attachmentApi.upload(tenantId, userId, Y9FlowableHolder.getPositionId(), fileName, fileSize,
                processInstanceId, taskId, describes, processSerialNumber, fileSource, storeId);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }
}
