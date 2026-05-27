package net.risesoft.controller.attachment;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.dto.DeleteEntityDTO;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.AttachmentConfModel;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AttachmentService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9.util.mime.MediaTypeUtil;
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

    private final AttachmentService attachmentService;

    private final ServletContext servletContext;

    /**
     * 附件下载
     *
     * @param id 附件id
     */
    @FlowableLog(operationName = "附件下载", operationType = FlowableOperationTypeEnum.DOWNLOAD)
    @GetMapping(value = "/download")
    public void download(@RequestParam @NotBlank String id, HttpServletResponse response) {
        String tenantId = Y9LoginUserHolder.getTenantId();

        try (ServletOutputStream out = response.getOutputStream()) {
            AttachmentModel model = attachmentApi.findById(tenantId, id).getData();
            String filename = model.getName();
            String fileStoreId = model.getFileStoreId();
            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(filename));
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, filename).toString());
            attachmentService.download(fileStoreId, filename, out);
            out.flush();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 删除附件
     *
     * @param dto 删除DTO
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delFile")
    @FlowableLog(operationName = "删除附件", operationType = FlowableOperationTypeEnum.DELETE)
    public Y9Result<String> delFile(@RequestBody @Valid DeleteEntityDTO dto) {
        return attachmentService.delFile(dto.getIds());
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
    @FlowableLog(operationName = "附加打包zip下载")
    @GetMapping(value = "/packDownload")
    public void packDownload(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String fileSource, HttpServletResponse response, HttpServletRequest request) {
        attachmentService.packDownload(processSerialNumber, fileSource, response, request);
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
        return attachmentService.upload(file, processInstanceId, taskId, describes, processSerialNumber, fileSource);
    }

    /**
     * 获取附件配置
     *
     * @param attachmentType 附件类型
     * @return Y9Result<List<AttachmentConfModel>>
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
    @FlowableLog(operationName = "上传附件带表单信息", operationType = FlowableOperationTypeEnum.UPLOAD)
    @PostMapping(value = "/uploadForm")
    public Y9Result<Object> uploadForm(@RequestParam("file") MultipartFile file,
        @ModelAttribute AttachmentModel attachmentModel) {
        String userId = Y9LoginUserHolder.getUserInfo().getPersonId();
        String userName = Y9LoginUserHolder.getUserInfo().getName();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
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
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file.getInputStream(), fullPath, fileName);
            String storeId = y9FileStore.getId();
            String fileSize =
                Y9FileUtil.getDisplayFileSize(y9FileStore.getFileSize() != null ? y9FileStore.getFileSize() : 0);
            attachmentModel.setName(fileName);
            attachmentModel.setFileSize(fileSize);
            attachmentModel.setFileStoreId(storeId);
            attachmentModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            assert fileName != null;
            String[] types = fileName.split("\\.");
            String type = types[types.length - 1].toLowerCase();
            attachmentModel.setFileType(type);
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, positionId).getData();
            OrgUnit department =
                orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), orgUnit.getParentId()).getData();
            attachmentModel.setDeptId(department != null ? department.getId() : "");
            attachmentModel.setDeptName(department != null ? department.getName() : "");
            attachmentModel.setPersonId(userId);
            attachmentModel.setPersonName(userName);
            return attachmentApi.uploadModel(tenantId, userId, attachmentModel);
        } catch (Exception e) {
            LOGGER.error("上传失败", e);
        }
        return Y9Result.failure("上传失败");
    }

    /**
     * 保存附件排序
     * 
     * @param idAndTabIndexs idAndTabIndexs
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "保存附件排序", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveOrder")
    public Y9Result<Object> saveOrder(@RequestParam String[] idAndTabIndexs) {
        String userId = Y9LoginUserHolder.getUserInfo().getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        attachmentApi.saveOrder(tenantId, userId, idAndTabIndexs);
        return Y9Result.successMsg("保存附件排序成功！");
    }

}
