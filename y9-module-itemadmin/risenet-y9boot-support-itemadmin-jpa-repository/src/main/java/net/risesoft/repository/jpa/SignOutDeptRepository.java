package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

    List<SignOutDept> findByDeptTypeId(String deptTypeId);

    @Query("FROM SignOutDept where deptTypeId =?1 and isForbidden = ?2 order by deptOrder asc")
    List<SignOutDept> findByDeptTypeIdAndIsForbiddenOrderByDeptOrderAsc(String deptTypeId, Integer isForbidden);

    @Query("select max(s.deptOrder) from SignOutDept s where s.deptTypeId =?1")
    Integer getMaxDeptOrder(String deptTypeId);

    /**
     * 根据id修改tabIndex
     *
     * @param deptOrder
     * @param id
     */
    @Modifying
    @Transactional
    @Query("update SignOutDept t set t.deptOrder=?1 where t.deptId=?2")
    void updateOrder(int deptOrder, String id);
}
