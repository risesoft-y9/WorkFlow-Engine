package net.risesoft.service;

import net.risesoft.entity.PaperAttachment;

import java.util.List;

public interface PaperAttachmentService {

    PaperAttachment findById(String id);

    List<PaperAttachment> findbyProcessSerialNumber(String processSerialNumber);

    void saveOrUpdate(PaperAttachment paperAttachment);

    /**
     * 删除附件
     *
     * @param ids
     * @return
     */
    void delFile(String ids);
}
