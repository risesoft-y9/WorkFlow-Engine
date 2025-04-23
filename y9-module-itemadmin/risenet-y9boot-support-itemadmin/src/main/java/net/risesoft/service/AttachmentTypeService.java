package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.AttachmentType;

public interface AttachmentTypeService {

    /**
     * Description:获取附件类型信息
     *
     * @param id
     * @return
     */
    AttachmentType getById(String id);

    /**
     * Description:根据标识获取附件类型信息
     *
     * @param mark
     * @return
     */
    AttachmentType getByMark(String mark);

    /**
     * Description:获取所有附件类型信息
     *
     * @return
     */
    List<AttachmentType> listAll();

    /**
     * Description:获取分页的附件类型信息
     *
     * @param page
     * @param rows
     * @return
     */
    Page<AttachmentType> pageAll(int page, int rows);

    /**
     * Description:移除附件类型信息
     *
     * @param id
     */
    void remove(String id);

    /**
     * Description:保存附件类型信息
     *
     * @param AttachmentType
     * @return
     */
    AttachmentType save(AttachmentType AttachmentType);

    /**
     * Description:保存或更新附件类型信息
     *
     * @param AttachmentType
     * @return
     */
    AttachmentType saveOrUpdate(AttachmentType AttachmentType);

}
