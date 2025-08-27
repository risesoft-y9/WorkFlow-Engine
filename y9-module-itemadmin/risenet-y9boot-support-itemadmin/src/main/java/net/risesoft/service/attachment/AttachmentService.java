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
     * @param processSerialNumbers
     */
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 删除附件
     *
     * @param ids
     * @return
     */
    void delFile(String ids);

    /**
     * (软航附件控件) 根据流程编号查询附件数量
     *
     * @param processSerialNumber
     * @return
     */
    Integer fileCounts(String processSerialNumber);

    /**
     * 获取附件
     *
     * @param id
     * @return
     */
    Attachment findById(String id);

    /**
     * (软航附件控件) 根据附件名称查询附件信息
     *
     * @param fileName
     * @param processSerialNumber
     * @return
     */
    Attachment getFileInfoByFileName(String fileName, String processSerialNumber);

    /**
     * 根据流程实例id、附件来源和文件类型获取附件数
     *
     * @param processSerialNumber
     * @param fileSource
     * @param fileType
     * @return
     */
    Integer getAttachmentCount(String processSerialNumber, String fileSource, String fileType);

    /**
     * 根据processSerialNumber获取
     *
     * @param processSerialNumber
     * @return
     */
    List<Attachment> listByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取附件列表
     *
     * @param processSerialNumber
     * @param fileSource
     * @param page
     * @param rows
     * @return
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
