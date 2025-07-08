package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.receive.ReceiveDepartment;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ReceiveDepartmentRepository
    extends JpaRepository<ReceiveDepartment, String>, JpaSpecificationExecutor<ReceiveDepartment> {

    @Query("select count(t.id) from ReceiveDepartment t where t.parentId = ?1")
    Integer countByParentId(String deptId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ReceiveDepartment t where t.deptId=?1")
    void deleteByDeptId(String id);

    @Query(" from ReceiveDepartment t order by t.tabIndex asc")
    List<ReceiveDepartment> findAllOrderByTabIndex();

    ReceiveDepartment findByDeptId(String deptId);

    List<ReceiveDepartment> findByDeptNameContainingOrderByTabIndex(String name);

    List<ReceiveDepartment> findByDeptNameLikeOrderByTabIndex(String name);

    List<ReceiveDepartment> findByParentIdOrderByTabIndex(String orgUnitId);

    @Query("select max(tabIndex) from ReceiveDepartment")
    Integer getMaxTabIndex();

}
