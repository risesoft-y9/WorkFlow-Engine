package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.dto.itemadmin.IdsDTO;
import net.risesoft.entity.attachment.Attachment;
import net.risesoft.entity.attachment.AttachmentConf;
import net.risesoft.enums.ItemAdminAuditLogEnum;
import net.risesoft.model.itemadmin.AttachmentConfModel;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.attachment.AttachmentConfRepository;
import net.risesoft.repository.attachment.AttachmentRepository;
import net.risesoft.service.attachment.AttachmentService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * 附件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/attachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttachmentApiImpl implements AttachmentApi {

    private final AttachmentService attachmentService;

    private final AttachmentRepository attachmentRepository;

    private final AttachmentConfRepository attachmentConfRepository;

    /**
     * 根据流程编号删除附件
     *
     * 
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delBatchByProcessSerialNumbers(@RequestBody List<String> processSerialNumbers) {
        attachmentService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    /**
     * 删除附件（物理删除，包含具体文件）
     *
     * @param deleteEntityDTO 删除附件信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delFile(@RequestBody IdsDTO deleteEntityDTO) {
        attachmentService.delFile(deleteEntityDTO);
        return Y9Result.success();
    }

    /**
     * 根据流程编号获取附件数量
     *
     * 
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> fileCounts(@RequestParam String processSerialNumber) {
        int num = attachmentService.fileCounts(processSerialNumber);
        return Y9Result.success(num);
    }

    /**
     * 获取附件配置信息
     *
     *
     * @param attachmentType 附件类型
     * @return Y9Result<List<AttachmentConfModel>>
     */
    @Override
    public Y9Result<List<AttachmentConfModel>> findByAttachmentType(@RequestParam String attachmentType) {
        List<AttachmentConfModel> attachmentConfModelList = new ArrayList<>();
        List<AttachmentConf> attachmentConfList =
            attachmentConfRepository.findByAttachmentTypeOrderByTabIndexAsc(attachmentType);
        AttachmentConfModel attachmentConfModel;
        for (AttachmentConf attachmentConf : attachmentConfList) {
            attachmentConfModel = new AttachmentConfModel();
            Y9BeanUtil.copyProperties(attachmentConf, attachmentConfModel);
            attachmentConfModelList.add(attachmentConfModel);
        }
        return Y9Result.success(attachmentConfModelList);
    }

    /**
     * 根据附件id获取附件信息
     *
     *
     * @param id 附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<AttachmentModel> findById(@RequestParam String id) {
        Attachment file = attachmentService.findById(id);
        AttachmentModel model = null;
        if (file != null) {
            model = new AttachmentModel();
            Y9BeanUtil.copyProperties(file, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 根据流程编号、附件来源、文件类型获取附件数量
     *
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getAttachmentCount(@RequestParam String processSerialNumber, String fileSource,
        String fileType) {
        fileType = fileType.toLowerCase();
        int num = attachmentService.getAttachmentCount(processSerialNumber, fileSource, fileType);
        return Y9Result.success(num);
    }

    /**
     * 获取附件分页列表
     *
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<AttachmentModel>} 通用分页请求返回对象 - rows是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Page<AttachmentModel> getAttachmentList(@RequestParam String processSerialNumber, String fileSource,
        @RequestParam int page, @RequestParam int rows) {
        return attachmentService.pageByProcessSerialNumber(processSerialNumber, fileSource, page, rows);
    }

    /**
     * 获取附件文件信息
     *
     *
     * @param fileId 附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<AttachmentModel> getFile(@RequestParam String fileId) {
        Attachment file = attachmentRepository.findById(fileId).orElse(null);
        AttachmentModel model = null;
        if (file != null) {
            model = new AttachmentModel();
            Y9BeanUtil.copyProperties(file, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 保存附件排序
     *
     * @param idsDTO idsDTO
     * @return Y9Result<Object>
     */
    @Override
    public Y9Result<Object> saveOrder(@RequestBody IdsDTO idsDTO) {
        List<String> ids = idsDTO.getIds();
        IntStream.range(0, ids.size()).forEach(i -> attachmentRepository.updateAttachmentOrder(i + 1, ids.get(i)));
        return Y9Result.success();
    }

    /**
     * 更新附件信息
     *
     *
     * @param fileId 文件id
     * @param fileSize 文件大小
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> updateFile(@RequestParam String fileId, String fileSize, String taskId,
        @RequestParam String y9FileStoreId) {
        String msg;
        try {
            Attachment attachment = attachmentRepository.findById(fileId).orElse(null);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setFileSize(fileSize);
                attachment.setUploadTime(Y9DateTimeUtils.formatCurrentDateTime());
                attachmentRepository.save(attachment);
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.ATTACHMENT_UPLOAD_INFO_UPDATE.getAction())
                    .description(Y9StringUtil.format(
                        ItemAdminAuditLogEnum.ATTACHMENT_UPLOAD_INFO_UPDATE.getDescription(), attachment.getName()))
                    .objectId(attachment.getId())
                    .oldObject(attachment)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
            msg = "success:true";
        } catch (Exception e) {
            LOGGER.error("updateFile error", e);
            msg = "success:false";
        }
        return Y9Result.success(msg);
    }

    /**
     * 上传附件
     *
     *
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param y9FileStoreId 附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> upload(@RequestParam String fileName, String fileSize, String processInstanceId,
        String taskId, String describes, String processSerialNumber, String fileSource,
        @RequestParam String y9FileStoreId) {
        attachmentService.uploadRest(fileName, fileSize, processInstanceId, taskId, processSerialNumber, describes,
            fileSource, y9FileStoreId);
        return Y9Result.successMsg("上传成功");
    }

    /**
     * 更新附件信息(model)
     *
     *
     * @param attachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> uploadModel(@RequestBody AttachmentModel attachmentModel) {
        Attachment file = new Attachment();
        Y9BeanUtil.copyProperties(attachmentModel, file);
        attachmentService.uploadRestModel(file);
        return Y9Result.success();
    }
}
