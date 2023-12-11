package net.risesoft.api.itemadmin.position;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.AttachmentModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Attachment4PositionApi {

    /**
     * 附件下载
     *
     * @param tenantId 租户id
     * @param id 附件id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> attachmentDownload(String tenantId, String id);

    /**
     * 根据流程编号删除附件
     *
     * @param tenantId 租户id
     * @param processSerialNumbers 流程编号
     */
    void delBatchByProcessSerialNumbers(String tenantId, List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param tenantId 租户id
     * @param ids 附件ids
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> delFile(String tenantId, String ids);

    /**
     * 附件数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Integer Integer
     */
    Integer fileCounts(String tenantId, String processSerialNumber);

    /**
     * 获取附件数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return int
     */
    int getAttachmentCount(String tenantId, String processSerialNumber, String fileSource, String fileType);

    /**
     * 获取附件列表
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getAttachmentList(String tenantId, String processSerialNumber, String fileSource, int page, int rows);

    /**
     * 获取附件列表(model)
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @return List&lt;AttachmentModel&gt;
     */
    List<AttachmentModel> getAttachmentModelList(String tenantId, String processSerialNumber, String fileSource);

    /**
     * 获取附件
     *
     * @param tenantId 租户id
     * @param fileId 附件id
     * @return
     */
    AttachmentModel getFile(String tenantId, String fileId);

    /**
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param attachjson 附件信息
     * @param processSerialNumber 流程编号
     * @return Boolean 是否保存成功
     */
    Boolean saveAttachment(String tenantId, String positionId, String attachjson, String processSerialNumber);

    /**
     * 保存附件信息
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param fileName 文件名称
     * @param fileType 文件类型
     * @param fileSizeString 文件大小
     * @param fileSource 附件来源
     * @param processInstanceId 流程实例id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return String String
     */
    String saveOrUpdateUploadInfo(String tenantId, String userId, String fileName, String fileType, String fileSizeString, String fileSource, String processInstanceId, String processSerialNumber, String taskId, String y9FileStoreId);

    /**
     * 更新附件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param fileId 文件id
     * @param fileSize 文件大小
     * @param taskId 任务id
     * @param y9FileStoreId 附件上传id
     * @return
     */
    String updateFile(String tenantId, String userId, String positionId, String fileId, String fileSize, String taskId, String y9FileStoreId);

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
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> upload(String tenantId, String userId, String positionId, String fileName, String fileSize, String processInstanceId, String taskId, String describes, String processSerialNumber, String fileSource, String y9FileStoreId);

    /**
     * 上传附件(model)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param attachmentModel 附件实体信息
     * @return boolean
     * @throws Exception Exception
     */
    boolean uploadModel(String tenantId, String positionId, AttachmentModel attachmentModel) throws Exception;
}
