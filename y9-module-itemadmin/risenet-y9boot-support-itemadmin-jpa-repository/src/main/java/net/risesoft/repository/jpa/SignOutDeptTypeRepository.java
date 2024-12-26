package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SignOutDeptType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface SignOutDeptTypeRepository
    extends JpaRepository<SignOutDeptType, String>, JpaSpecificationExecutor<SignOutDeptType> {

    @Query("FROM SignOutDeptType order by tabIndex asc")
    List<SignOutDeptType> findAllOrderByTabIndexAsc();

    @Query("FROM SignOutDeptType where isForbidden = ?1 order by tabIndex asc")
    List<SignOutDeptType> findByIsForbiddenOrderByTabIndexAsc(Integer isForbidden);

    @Query("select max(s.tabIndex) from SignOutDeptType s")
    Integer getMaxTabIndex();

    @Modifying
    @Transactional
    @Query("update SignOutDeptType t set t.tabIndex=?1 where t.deptTypeId=?2")
    void updateOrder(int tabIndex, String id);
}
