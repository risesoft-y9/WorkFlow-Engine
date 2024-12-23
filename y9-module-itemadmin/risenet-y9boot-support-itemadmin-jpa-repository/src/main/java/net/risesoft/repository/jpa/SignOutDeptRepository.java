package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SignOutDept;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface SignOutDeptRepository
    extends JpaRepository<SignOutDept, String>, JpaSpecificationExecutor<SignOutDept> {

    @Query("FROM SignOutDept order by deptOrder asc")
    List<SignOutDept> findAllOrderByDeptOrderAsc();

    @Query("select max(s.deptOrder) from SignOutDept s")
    Integer getMaxDeptOrder();
}
