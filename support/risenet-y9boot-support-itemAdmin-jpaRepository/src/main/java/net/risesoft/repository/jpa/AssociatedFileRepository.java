package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.AssociatedFile;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AssociatedFileRepository extends JpaRepository<AssociatedFile, String>, JpaSpecificationExecutor<AssociatedFile> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete AssociatedFile t where t.processSerialNumber=?1")
    void deleteByProcessSerialNumber(String processSerialNumber);

    @Query("from AssociatedFile t where t.processSerialNumber=?1")
    AssociatedFile findByProcessSerialNumber(String processSerialNumber);

}
