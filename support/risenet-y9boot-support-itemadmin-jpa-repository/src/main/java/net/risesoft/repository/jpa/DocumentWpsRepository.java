package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DocumentWps;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface DocumentWpsRepository
    extends JpaRepository<DocumentWps, String>, JpaSpecificationExecutor<DocumentWps> {

    DocumentWps findByProcessSerialNumber(String processSerialNumber);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update DocumentWps t set t.hasContent=?2 where t.processSerialNumber=?1")
    void updateHasContent(String processSerialNumber, String hasContent);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update DocumentWps t set t.processInstanceId=?2 where t.processSerialNumber=?1")
    void updateProcessInstanceId(String processSerialNumber, String processInstanceId);
}
