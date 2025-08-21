package net.risesoft.service.attachment;

import java.util.List;

import net.risesoft.entity.attachment.AttachmentConf;

public interface AttachmentConfService {

    /**
     * 根据唯一标示查找附件配置
     *
     * @param id 唯一标示
     * @return AttachmentConf
     */
    AttachmentConf findById(String id);

    /**
     * 根据附件类型查找附件配置
     *
     * @param attachmentType 附件类型
     * @param configType 配置类型
     * @return List<AttachmentConf>
     */
    List<AttachmentConf> listByAttachmentType(String attachmentType, Integer configType);

    /**
     * 移除指定附件类型的所有附件配置
     *
     * @param attachmentType 附件类型
     */
    void removeByAttachmentType(String attachmentType);

    /**
     * 根据附件配置唯一标示数据删除附件配置
     *
     * @param attachmentConfIds 附件配置唯一标示数据
     */
    void removeAttachmentConfs(String[] attachmentConfIds);

    /**
     * 保存或更新附件配置
     *
     * @param attachmentConf 附件配置
     */
    void saveOrUpdate(AttachmentConf attachmentConf);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void updateOrder(String[] idAndTabIndexs);
}
