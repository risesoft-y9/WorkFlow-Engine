package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.AttachmentConf;

public interface AttachmentConfService {

    /**
     * 根据唯一标示查找附件配置
     *
     * @param id
     * @return
     */
    AttachmentConf findById(String id);

    /**
     * Description:根据附件类型查找附件配置
     *
     * @param attachmentType
     * @param configType
     * @return
     */
    List<AttachmentConf> listByAttachmentType(String attachmentType, Integer configType);

    /**
     * Description:移除指定附件类型的所有附件配置
     *
     * @param attachmentType
     */
    void removeByAttachmentType(String attachmentType);

    /**
     * 根据附件配置唯一标示数据删除附件配置
     *
     * @param attachmentConfIds
     */
    void removeAttachmentConfs(String[] attachmentConfIds);

    /**
     * 保存或更新附件配置
     *
     * @param attachmentConf
     */
    void saveOrUpdate(AttachmentConf attachmentConf);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void updateOrder(String[] idAndTabIndexs);
}
