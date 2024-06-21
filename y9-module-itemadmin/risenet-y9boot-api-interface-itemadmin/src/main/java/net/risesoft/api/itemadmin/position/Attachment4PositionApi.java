package net.risesoft.api.itemadmin.position;

import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;


import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Attachment4PositionApi {

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId             租户id
     * @param processSerialNumbers 流程编号
     * @return Y9Result<Object>
     */
    Y9Result<Object> delBatchByProcessSerialNumbers(String tenantId, List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param ids      附件ids
     * @return Y9Result<Object>
     */
    Y9Result<Object> delFile(String tenantId, String ids);

    /**
     * 附件数
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> fileCounts(String tenantId, String processSerialNumber);

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param id       附件id
     * @return Y9Result<AttachmentModel>
     */
    Y9Result<AttachmentModel> findById(String tenantId, String id);

    /**
     * 获取附件数
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param fileType            文件类型
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> getAttachmentCount(String tenantId, String processSerialNumber, String fileSource, String fileType);

    /**
     * 获取附件列表
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param page                页码
     * @param rows                行数
     * @return Y9Page<AttachmentModel>
     */
    Y9Page<AttachmentModel> getAttachmentList(String tenantId, String processSerialNumber, String fileSource, int page, int rows);

    /**
     * 获取附件列表(model)
     *
     * @param tenantId            租户id
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @return Y9Result<List < AttachmentModel>>
     */
    Y9Result<List<AttachmentModel>> getAttachmentModelList(String tenantId, String processSerialNumber, String fileSource);

    /**
     * 获取附件
     *
     * @param tenantId 租户id
     * @param fileId   附件id
     * @return Y9Result<AttachmentModel>
     */
    Y9Result<AttachmentModel> getFile(String tenantId, String fileId);

    /**
     * 保存附件信息
     *
     * @param tenantId            租户id
     * @param positionId          岗位id
     * @param attachjson          附件信息
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    Y9Result<Object> saveAttachment(String tenantId, String positionId, String attachjson, String processSerialNumber);

    /**
     * 保存附件信息
     *
     * @param tenantId            租户id
     * @param userId              用户id
     * @param fileName            文件名称
     * @param fileType            文件类型
     * @param fileSizeString      文件大小
     * @param fileSource          附件来源
     * @param processInstanceId   流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId              任务id
     * @param y9FileStoreId       附件上传id
     * @return Y9Result<String>
     */
    Y9Result<String> saveOrUpdateUploadInfo(String tenantId, String userId, String fileName, String fileType, String fileSizeString, String fileSource, String processInstanceId, String processSerialNumber, String taskId, String y9FileStoreId);

    /**
     * 更新附件
     *
     * @param tenantId      租户id
     * @param userId        人员id
     * @param positionId    岗位id
     * @param fileId        文件id
     * @param fileSize      文件大小
     * @param taskId        任务id
     * @param y9FileStoreId 附件上传id
     * @return Y9Result<String>
     */
    Y9Result<String> updateFile(String tenantId, String userId, String positionId, String fileId, String fileSize, String taskId, String y9FileStoreId);

    /**
     * 上传附件
     *
     * @param tenantId            租户id
     * @param userId              人员id
     * @param positionId          岗位id
     * @param fileName            文件名
     * @param fileSize            文件大小
     * @param processInstanceId   流程实例id
     * @param taskId              任务id
     * @param describes           描述
     * @param processSerialNumber 流程编号
     * @param fileSource          附件来源
     * @param y9FileStoreId       附件上传id
     * @return Y9Result<String>
     */
    Y9Result<String> upload(String tenantId, String userId, String positionId, String fileName, String fileSize, String processInstanceId, String taskId, String describes, String processSerialNumber, String fileSource, String y9FileStoreId);

    /**
     * 上传附件(model)
     *
     * @param tenantId        租户id
     * @param positionId      岗位id
     * @param attachmentModel 附件实体信息
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    Y9Result<Object> uploadModel(String tenantId, String positionId, AttachmentModel attachmentModel) throws Exception;
}
