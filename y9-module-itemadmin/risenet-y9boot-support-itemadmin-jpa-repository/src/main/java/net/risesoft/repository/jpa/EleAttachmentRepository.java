package net.risesoft.repository.jpa;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.EleAttachment;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface EleAttachmentRepository extends JpaRepository<EleAttachment, String>, JpaSpecificationExecutor<EleAttachment> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from EleAttachment where processSerialNumber in (?1)")
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    List<EleAttachment> findByProcessSerialNumberAndAttachmentTypeOrderByTabIndexAsc(String processSerialNumber, String attachmentType);

    List<EleAttachment> findByProcessSerialNumberAndAttachmentTypeOrderByUploadTimeDesc(String processSerialNumber, String attachmentType);

    List<EleAttachment> findByProcessSerialNumberIn(List<String> processSerialNumbers);

    @Query("select max(t.tabIndex) from EleAttachment t where t.processSerialNumber=?1 and t.attachmentType=?2")
    Integer getMaxTabIndex(String processSerialNumber, String attachmentType);
}
