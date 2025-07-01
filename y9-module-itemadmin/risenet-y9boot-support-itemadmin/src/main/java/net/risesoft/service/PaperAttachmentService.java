package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.PaperAttachment;

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
