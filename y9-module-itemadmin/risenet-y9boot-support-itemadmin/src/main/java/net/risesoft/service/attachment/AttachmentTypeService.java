package net.risesoft.service.attachment;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.attachment.AttachmentType;

public interface AttachmentTypeService {

    /**
     * 获取附件类型信息
     *
     * @param id
     * @return
     */
    AttachmentType getById(String id);

    /**
     * 获取所有附件类型信息
     *
     * @return
     */
    List<AttachmentType> listAll();

    /**
     * 获取分页的附件类型信息
     *
     * @param page
     * @param rows
     * @return
     */
    Page<AttachmentType> pageAll(int page, int rows);

    /**
     * 移除附件类型信息
     *
     * @param id
     */
    void remove(String id);

    /**
     * 保存附件类型信息
     *
     * @param AttachmentType
     * @return
     */
    AttachmentType save(AttachmentType AttachmentType);

    /**
     * 保存或更新附件类型信息
     *
     * @param AttachmentType
     * @return
     */
    AttachmentType saveOrUpdate(AttachmentType AttachmentType);

}
