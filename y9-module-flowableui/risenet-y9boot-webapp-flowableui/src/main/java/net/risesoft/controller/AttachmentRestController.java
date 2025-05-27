package net.risesoft.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.BrowserTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.AttachmentConfModel;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 附件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/attachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentRestController {

    private final Y9FileStoreService y9FileStoreService;

    private final AttachmentApi attachmentApi;

    private final OrgUnitApi orgUnitApi;

    /**
     * 附件下载
     *
     * @param id 附件id
     */
    @GetMapping(value = "/attachmentDownload")
    public void attachmentDownload(@RequestParam @NotBlank String id, HttpServletResponse response,
        HttpServletRequest request) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            AttachmentModel model = attachmentApi.findById(tenantId, id).getData();
            String filename = model.getName();
            String filePath = model.getFileStoreId();
            if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), "ISO8859-1");// 火狐浏览器
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
            }
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            y9FileStoreService.downloadFileToOutputStream(filePath, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LOGGER.error("附件下载失败", e);
        }
    }

    /**
     * 删除附件
     *
     * @param ids 附件ids，逗号隔开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delFile")
    public Y9Result<String> delFile(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        attachmentApi.delFile(tenantId, ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取附件列表
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 文件来源
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<AttachmentModel>
     */
    @GetMapping(value = "/getAttachmentList")
    public Y9Page<AttachmentModel> getAttachmentList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String fileSource, @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return attachmentApi.getAttachmentList(tenantId, processSerialNumber, fileSource, page, rows);
    }

    /**
     * 附加打包zip下载
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     */
    @GetMapping(value = "/packDownload")
    public void packDownload(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String fileSource, HttpServletResponse response, HttpServletRequest request) {
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
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
            String zipPath = packDownloadPath + File.separator + fileZip;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipPath));
            ZipOutputStream zos = new ZipOutputStream(bos);
            ZipEntry ze;
            for (AttachmentModel file : list) {
                String filename = file.getName();
                String fileStoreId = file.getFileStoreId();
                byte[] filebyte = y9FileStoreService.downloadFileToBytes(fileStoreId);
                InputStream bis = new ByteArrayInputStream(filebyte);
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
            boolean b = request.getHeader("User-Agent").toLowerCase().indexOf(BrowserTypeEnum.FIREFOX.getValue()) > 0;
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
            } catch (Exception e) {
                LOGGER.error("下载失败", e);
            } finally {
                if (out != null) {
                    out.close();
                }
                in.close();
                // 删除服务器本地产生的临时压缩文件
                reportZip.delete();
            }
        } catch (Exception e) {
            LOGGER.error("下载失败", e);
        }
    }

    /**
     * 上传附件
     *
     * @param file 文件
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 文件来源
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "上传附件", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/upload")
    public Y9Result<String> upload(MultipartFile file, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String describes,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String fileSource) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            String storeId = y9FileStore.getId();
            String fileSize =
                Y9FileUtil.getDisplayFileSize(y9FileStore.getFileSize() != null ? y9FileStore.getFileSize() : 0);
            return attachmentApi.upload(tenantId, userId, Y9LoginUserHolder.getPositionId(), fileName, fileSize,
                processInstanceId, taskId, describes, processSerialNumber, fileSource, storeId);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

    /**
     * 获取附件配置
     *
     * @param attachmentType
     * @return
     */
    @GetMapping(value = "/getAttachmentConfig")
    public Y9Result<List<AttachmentConfModel>> getAttachmentConfig(@RequestParam @NotBlank String attachmentType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return attachmentApi.findByAttachmentType(tenantId, attachmentType);
    }

    /**
     * 上传附件带表单信息
     *
     * @param file 文件
     * @return Y9Result<String>
     */
    @PostMapping(value = "/uploadForm")
    public Y9Result<Object> uploadForm(@RequestParam("file") MultipartFile file,
        @ModelAttribute AttachmentModel attachmentModel) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName(), tenantId = Y9LoginUserHolder.getTenantId(),
            positionId = Y9LoginUserHolder.getPositionId();
        try {
            String processSerialNumber = attachmentModel.getProcessSerialNumber();
            if (StringUtils.isBlank(processSerialNumber)) {
                return Y9Result.failure("流程序列号不能为空？");
            }
            String describes = attachmentModel.getDescribes();
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
                attachmentModel.setDescribes(describes);
            }
            String originalFilename = file.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fullPath =
                "/" + Y9Context.getSystemName() + "/" + tenantId + "/attachmentFile" + "/" + processSerialNumber;
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
            String storeId = y9FileStore.getId();
            String fileSize =
                Y9FileUtil.getDisplayFileSize(y9FileStore.getFileSize() != null ? y9FileStore.getFileSize() : 0);
            attachmentModel.setName(fileName);
            attachmentModel.setFileSize(fileSize);
            attachmentModel.setFileStoreId(storeId);
            attachmentModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            String[] types = fileName.split("\\.");
            String type = types[types.length - 1].toLowerCase();
            attachmentModel.setFileType(type);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
            OrgUnit department =
                orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), orgUnit.getParentId()).getData();
            attachmentModel.setDeptId(department != null ? department.getId() : "");
            attachmentModel.setDeptName(department != null ? department.getName() : "");
            attachmentModel.setPersonId(userId);
            attachmentModel.setPersonName(userName);
            attachmentModel.setPositionId(positionId);
            return attachmentApi.uploadModel(tenantId, userId, attachmentModel);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

}
