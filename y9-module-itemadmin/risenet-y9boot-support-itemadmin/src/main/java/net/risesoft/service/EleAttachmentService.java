package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.EleAttachment;

/**
 * @author qinman
 * @date 2024/11/11
 */
public interface EleAttachmentService {

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
     * 获取附件
     *
     * @param id
     * @return
     */
    EleAttachment findById(String id);

    /**
     * 根据流程编号和附件类型获取附件
     *
     * @param processSerialNumber
     * @param attachmentType
     * @return
     */
    List<EleAttachment> findByProcessSerialNumberAndAttachmentType(String processSerialNumber, String attachmentType);

    /**
     * 根据流程编号和附件类型获取附件
     *
     * @param processSerialNumber
     * @param attachmentType
     * @return
     */
    List<EleAttachment> findByProcessSerialNumberAndTypeOrderByTime(String processSerialNumber, String attachmentType);

    /**
     * Description: 保存附件
     *
     * @param eleAttachment
     */
    void saveOrUpdate(EleAttachment eleAttachment);

    /**
     * 附件排序
     *
     * @param id1
     * @param id2
     */
    void saveOrder(String id1, String id2);
}
