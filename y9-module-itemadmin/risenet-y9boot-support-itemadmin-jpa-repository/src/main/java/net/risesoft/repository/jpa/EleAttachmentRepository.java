package net.risesoft.repository.jpa;

import net.risesoft.entity.EleAttachment;
import net.risesoft.entity.PaperAttachment;
import net.risesoft.entity.TransactionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface EleAttachmentRepository extends JpaRepository<EleAttachment, String>, JpaSpecificationExecutor<EleAttachment> {

    List<EleAttachment> findByProcessSerialNumberIn(List<String> processSerialNumbers);

    List<EleAttachment> findByProcessSerialNumberAndAttachmentTypeOrderByTabIndexAsc(String processSerialNumber, String attachmentType);

    @Query("select max(t.tabIndex) from EleAttachment t where t.processSerialNumber=?1 and t.attachmentType=?2")
    Integer getMaxTabIndex(String processSerialNumber, String attachmentType);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from EleAttachment where processSerialNumber in (?1)")
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);
}
