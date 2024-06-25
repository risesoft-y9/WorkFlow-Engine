package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.TransactionFile;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
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
@RequestMapping(value = "/services/rest/attachment4Position")
public class AttachmentApiImpl implements Attachment4PositionApi {

    private final TransactionFileService transactionFileService;

    private final TransactionFileRepository transactionFileRepository;

    private final PositionApi positionManager;

    private final PersonApi personManager;

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/delByProcessSerialNumbers", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> delBatchByProcessSerialNumbers(String tenantId,
        @RequestBody List<String> processSerialNumbers) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionFileService.delBatchByProcessSerialNumbers(processSerialNumbers);
        return Y9Result.success();
    }

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param ids 附件ids
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/delFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> delFile(String tenantId, String ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        transactionFileService.delFile(ids);
        return Y9Result.success();
    }

    /**
     * 获取附件数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/fileCounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> fileCounts(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = transactionFileService.fileCounts(processSerialNumber);
        return Y9Result.success(num);
    }

    /**
     * 获取附件信息
     *
     * @param tenantId 租户id
     * @param id 附件id
     * @return Y9Result<AttachmentModel>
     */
    @Override
    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<AttachmentModel> findById(String tenantId, String id) {
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
     * 获取附件数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/getAttachmentCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> getAttachmentCount(String tenantId, String processSerialNumber, String fileSource,
        String fileType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        fileType = fileType.toLowerCase();
        int num = transactionFileService.getTransactionFileCount(processSerialNumber, fileSource, fileType);
        return Y9Result.success(num);
    }

    /**
     * 获取附件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<AttachmentModel>
     */
    @Override
    @GetMapping(value = "/getAttachmentList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<AttachmentModel> getAttachmentList(String tenantId, String processSerialNumber, String fileSource,
        int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return transactionFileService.getAttachmentList(processSerialNumber, fileSource, page, rows);
    }

    /**
     * 获取附件列表(model)
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @return Y9Result<List < AttachmentModel>>
     */
    @Override
    @GetMapping(value = "/getAttachmentModelList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<AttachmentModel>> getAttachmentModelList(String tenantId, String processSerialNumber,
        String fileSource) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<TransactionFile> transactionFileList =
            transactionFileService.getAttachmentModelList(processSerialNumber, fileSource);
        List<AttachmentModel> list = ItemAdminModelConvertUtil.attachmentList2ModelList(transactionFileList);
        return Y9Result.success(list);
    }

    /**
     * 获取附件信息
     *
     * @param tenantId 租户id
     * @param fileId 附件id
     * @return Y9Result<AttachmentModel>
     */
    @Override
    @GetMapping(value = "/getFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<AttachmentModel> getFile(String tenantId, String fileId) {
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
     * @param positionId 岗位id
     * @param attachjson 附件信息
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @SuppressWarnings("unchecked")
    @Override
    @PostMapping(value = "/saveAttachment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveAttachment(String tenantId, String positionId, String attachjson,
        String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
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
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param fileName 文件名称
     * @param fileType 文件类型
     * @param fileSizeString 文件大小
     * @param fileSource 附件来源
     * @param processInstanceId 流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/saveOrUpdateUploadInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> saveOrUpdateUploadInfo(String tenantId, String positionId, String fileName, String fileType,
        String fileSizeString, String fileSource, String processInstanceId, String processSerialNumber, String taskId,
        String y9FileStoreId) {
        String msg;
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TransactionFile attachment = transactionFileService.getFileInfoByFileName(fileName, processSerialNumber);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setName(fileName);
                attachment.setFileSize(fileSizeString);
                attachment.setTaskId(taskId);
                attachment.setPersonId(positionId);
                attachment.setPersonName(position.getName());
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
                fileAttachment.setPersonId(positionId);
                fileAttachment.setPersonName(position.getName());
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
     * @param userId 人员id
     * @param positionId 岗位id
     * @param fileId 文件id
     * @param fileSizeString 文件大小
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/updateFile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> updateFile(String tenantId, String userId, String positionId, String fileId,
        String fileSizeString, String taskId, String y9FileStoreId) {
        String msg;
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            TransactionFile attachment = transactionFileRepository.findById(fileId).orElse(null);
            if (null != attachment) {
                attachment.setFileStoreId(y9FileStoreId);
                attachment.setFileSize(fileSizeString);
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
     * @param positionId 岗位id
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param describes 描述
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param y9FileStoreId 附件上传id
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> upload(String tenantId, String userId, String positionId, String fileName, String fileSize,
        String processInstanceId, String taskId, String describes, String processSerialNumber, String fileSource,
        String y9FileStoreId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        transactionFileService.uploadRest(fileName, fileSize, processInstanceId, taskId, processSerialNumber, describes,
            fileSource, y9FileStoreId);
        return Y9Result.successMsg("上传成功");
    }

    /**
     * 上传附件(model)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param attachmentModel 附件实体信息
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/uploadModel", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> uploadModel(String tenantId, String positionId,
        @RequestBody AttachmentModel attachmentModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        TransactionFile transactionFile = ItemAdminModelConvertUtil.attachmentModel2TransactionFile(attachmentModel);
        transactionFileService.uploadRestModel(transactionFile);
        return Y9Result.success();
    }
}
