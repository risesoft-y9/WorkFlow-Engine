package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.TransactionFile;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.TransactionFileRepository;
import net.risesoft.service.TransactionFileService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

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

    private final TransactionFileService transactionFileService;

    private final TransactionFileRepository transactionFileRepository;

    private final OrgUnitApi orgUnitApi;

    private final PersonApi personApi;

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delBatchByProcessSerialNumbers(@RequestParam String tenantId,
        @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionFileService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    /**
     * 删除附件（物理删除，包含具体文件）
     *
     * @param tenantId 租户id
     * @param ids 附件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delFile(@RequestParam String tenantId, @RequestParam String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionFileService.delFile(ids);
        return Y9Result.success();
    }

    /**
     * 根据流程编号获取附件数量
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> fileCounts(@RequestParam String tenantId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = transactionFileService.fileCounts(processSerialNumber);
        return Y9Result.success(num);
    }

    /**
     * 根据附件id获取附件信息
     *
     * @param tenantId 租户id
     * @param id 附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<AttachmentModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        TransactionFile file = transactionFileService.findById(id);
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
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是附件数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getAttachmentCount(@RequestParam String tenantId, @RequestParam String processSerialNumber,
        String fileSource, String fileType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        fileType = fileType.toLowerCase();
        int num = transactionFileService.getTransactionFileCount(processSerialNumber, fileSource, fileType);
        return Y9Result.success(num);
    }

    /**
     * 获取附件分页列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<AttachmentModel>} 通用分页请求返回对象 - rows是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Page<AttachmentModel> getAttachmentList(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, String fileSource, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return transactionFileService.pageByProcessSerialNumber(processSerialNumber, fileSource, page, rows);
    }

    /**
     * 获取附件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @return {@code Y9Result<List<AttachmentModel>>} 通用请求返回对象 - data是附件列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<AttachmentModel>> getAttachmentModelList(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, String fileSource) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<TransactionFile> transactionFileList =
            transactionFileService.listSearchByProcessSerialNumber(processSerialNumber, fileSource);
        List<AttachmentModel> list = ItemAdminModelConvertUtil.attachmentList2ModelList(transactionFileList);
        return Y9Result.success(list);
    }

    /**
     * 获取附件文件信息
     *
     * @param tenantId 租户id
     * @param fileId 附件id
     * @return {@code Y9Result<AttachmentModel>} 通用请求返回对象 - data是附件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<AttachmentModel> getFile(@RequestParam String tenantId, @RequestParam String fileId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        TransactionFile file = transactionFileRepository.findById(fileId).orElse(null);
        AttachmentModel model = null;
        if (file != null) {
            model = new AttachmentModel();
            Y9BeanUtil.copyProperties(file, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param attachjson 附件信息
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @SuppressWarnings("unchecked")
    @Override
    public Y9Result<Object> saveAttachment(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String attachjson, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> attachmentJson = Y9JsonUtil.readValue(attachjson, Map.class);
        assert attachmentJson != null;
        List<Map<String, Object>> attachmentList = (List<Map<String, Object>>)attachmentJson.get("attachment");
        for (Map<String, Object> map : attachmentList) {
            TransactionFile file = new TransactionFile();
            file.setDescribes(map.get("describes") == null ? "" : map.get("describes").toString());
            file.setFileStoreId(map.get("filePath").toString());
            file.setFileSize(map.get("fileSize") == null ? "" : map.get("fileSize").toString());
            file.setFileSource(map.get("fileSource") == null ? "" : map.get("fileSource").toString());
            file.setFileType(map.get("fileType") == null ? "" : map.get("fileType").toString());
            file.setId(map.get("id").toString());
            file.setName(map.get("fileName").toString());
            file.setPersonId(map.get("personId") == null ? "" : map.get("personId").toString());
            file.setPersonName(map.get("personName") == null ? "" : map.get("personName").toString());
            file.setProcessSerialNumber(processSerialNumber);
            file.setUploadTime(sdf.format(new Date()));
            transactionFileService.save(file);
        }
        return Y9Result.success();
    }

    /**
     * 保存或更新附件上传信息
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param fileName 文件名称
     * @param fileType 文件类型
     * @param fileSizeString 文件大小
     * @param fileSource 附件来源
     * @param processInstanceId 流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveOrUpdateUploadInfo(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String fileName, String fileType, String fileSizeString, String fileSource,
        String processInstanceId, String processSerialNumber, String taskId, @RequestParam String y9FileStoreId) {
        String msg;
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TransactionFile attachment = transactionFileService.getFileInfoByFileName(fileName, processSerialNumber);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setName(fileName);
                attachment.setFileSize(fileSizeString);
                attachment.setTaskId(taskId);
                attachment.setPersonId(orgUnitId);
                attachment.setPersonName(orgUnit.getName());
                attachment.setUploadTime(sdf.format(new Date()));
                transactionFileRepository.save(attachment);
            } else {
                TransactionFile fileAttachment = new TransactionFile();
                fileAttachment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                fileAttachment.setFileStoreId(y9FileStoreId);
                fileAttachment.setName(fileName);
                fileAttachment.setFileSize(fileSizeString);
                fileAttachment.setFileType(fileType);
                fileAttachment.setUploadTime(sdf.format(new Date()));
                fileAttachment.setPersonId(orgUnitId);
                fileAttachment.setPersonName(orgUnit.getName());
                fileAttachment.setProcessInstanceId(processInstanceId);
                fileAttachment.setProcessSerialNumber(processSerialNumber);
                fileAttachment.setTaskId(taskId);
                fileAttachment.setFileSource(fileSource);
                fileAttachment.setTabIndex(transactionFileService.fileCounts(processSerialNumber) + 1);
                transactionFileRepository.save(fileAttachment);
            }
            msg = "success:true";
        } catch (Exception e) {
            LOGGER.error("saveOrUpdateUploadInfo error", e);
            msg = "success:false";
        }
        return Y9Result.success(msg);
    }

    /**
     * 更新附件信息
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param fileId 文件id
     * @param fileSize 文件大小
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> updateFile(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String fileId, String fileSize, String taskId, @RequestParam String y9FileStoreId) {
        String msg;
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TransactionFile attachment = transactionFileRepository.findById(fileId).orElse(null);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setFileSize(fileSize);
                attachment.setUploadTime(sdf.format(new Date()));
                transactionFileRepository.save(attachment);
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
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
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
    public Y9Result<String> upload(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String orgUnitId, @RequestParam String fileName, String fileSize, String processInstanceId,
        String taskId, String describes, String processSerialNumber, String fileSource,
        @RequestParam String y9FileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        transactionFileService.uploadRest(fileName, fileSize, processInstanceId, taskId, processSerialNumber, describes,
            fileSource, y9FileStoreId);
        return Y9Result.successMsg("上传成功");
    }

    /**
     * 更新附件信息(model)
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param attachmentModel 附件实体信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> uploadModel(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestBody AttachmentModel attachmentModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        TransactionFile transactionFile = ItemAdminModelConvertUtil.attachmentModel2TransactionFile(attachmentModel);
        transactionFileService.uploadRestModel(transactionFile);
        return Y9Result.success();
    }
}
