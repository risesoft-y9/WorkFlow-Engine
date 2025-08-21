package net.risesoft.service.attachment;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.attachment.AttachmentType;

public interface AttachmentTypeService {

    /**
     * 获取附件类型信息
     *
     * @param id 唯一标识
     * @return AttachmentType
     */
    AttachmentType getById(String id);

    /**
     * 获取所有附件类型信息
     *
     * @return List<AttachmentType>
     */
    List<AttachmentType> listAll();

    /**
     * 获取分页的附件类型信息
     *
     * @param page 页码
     * @param rows 每页记录数
     * @return Page<AttachmentType>
     */
    Page<AttachmentType> pageAll(int page, int rows);

    /**
     * 移除附件类型信息
     *
     * @param id 唯一标识
     */
    void remove(String id);

    /**
     * 保存附件类型信息
     *
     * @param attachmentType 附件类型
     * @return AttachmentType
     */
    AttachmentType save(AttachmentType attachmentType);

    /**
     * 保存或更新附件类型信息
     *
     * @param attachmentType 附件类型
     * @return AttachmentType
     */
    AttachmentType saveOrUpdate(AttachmentType attachmentType);
}