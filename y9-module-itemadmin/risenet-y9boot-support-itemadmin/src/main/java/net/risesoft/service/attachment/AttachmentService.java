package net.risesoft.service.attachment;

import java.util.List;

import net.risesoft.entity.attachment.Attachment;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AttachmentService {

    /**
     * 根据流程编号删除附件，同时删除文件系统的文件
     *
     * @param processSerialNumbers 流程编号
     */
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 删除附件
     * 
     * @param ids 附件唯一标识
     */
    void delFile(String ids);

    /**
     * (软航附件控件) 根据流程编号查询附件数量
     *
     * @param processSerialNumber 流程编号
     * @return Integer
     */
    Integer fileCounts(String processSerialNumber);

    /**
     * 获取附件
     *
     * @param id 唯一标识
     * @return Attachment
     */
    Attachment findById(String id);

    /**
     * (软航附件控件) 根据附件名称查询附件信息
     *
     * @param fileName 附件名称
     * @param processSerialNumber 流程编号
     * @return Attachment
     */
    Attachment getFileInfoByFileName(String fileName, String processSerialNumber);

    /**
     * 根据流程实例id、附件来源和文件类型获取附件数
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param fileType 文件类型
     * @return Integer
     */
    Integer getAttachmentCount(String processSerialNumber, String fileSource, String fileType);

    /**
     * 根据processSerialNumber获取
     *
     * @param processSerialNumber 流程编号
     * @return List<Attachment>
     */
    List<Attachment> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取附件列表
     *
     * @param processSerialNumber 流程编号
     * @param fileSource 附件来源
     * @param page 页数
     * @param rows 每页数量
     * @return Y9Page<AttachmentModel>
     */
    Y9Page<AttachmentModel> pageByProcessSerialNumber(String processSerialNumber, String fileSource, int page,
        int rows);

    /**
     * 上传附件
     *
     * @param fileName
     * @param fileSize
     * @param processInstanceId
     * @param taskId
     * @param processSerialNumber
     * @param describes
     * @param fileSource
     * @param y9FileStoreId
     * @return
     */
    void uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId);

    /**
     * 上传附件
     *
     * @param attachment
     */
    void uploadRestModel(Attachment attachment);
}
