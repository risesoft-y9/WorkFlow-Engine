package net.risesoft.repository.jpa;

import net.risesoft.entity.ItemButtonBind;
import net.risesoft.entity.OrganWordUseHistory;
import net.risesoft.entity.PaperAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface PaperAttachmentRepository extends JpaRepository<PaperAttachment, String>, JpaSpecificationExecutor<PaperAttachment> {

    List<PaperAttachment> findByProcessSerialNumberOrderByTabIndexAsc(String processSerialNumber);

    @Query("select max(t.tabIndex) from PaperAttachment t where t.processSerialNumber=?1")
    Integer getMaxTabIndex(String processSerialNumber);
}
