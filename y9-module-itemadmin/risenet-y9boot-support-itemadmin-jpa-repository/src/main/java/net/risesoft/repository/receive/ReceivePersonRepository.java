package net.risesoft.repository.receive;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.receive.ReceivePerson;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ReceivePersonRepository
    extends JpaRepository<ReceivePerson, String>, JpaSpecificationExecutor<ReceivePerson> {

    @Query("select count(t.id) from ReceivePerson t where t.deptId = ?1")
    Integer countByDeptId(String deptId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ReceivePerson t where t.deptId=?1")
    void deleteByDeptId(String deptId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ReceivePerson t where t.deptId = ?1 and t.personId = ?2")
    void deleteByDeptIdAndPersonId(String deptId, String userId);

    List<ReceivePerson> findByDeptId(String deptId);

    List<ReceivePerson> findByPersonId(String userId);

    ReceivePerson findByPersonIdAndDeptId(String userId, String deptId);
}
