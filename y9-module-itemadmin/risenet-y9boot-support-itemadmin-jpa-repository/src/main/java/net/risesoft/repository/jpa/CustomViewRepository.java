package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.CustomView;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface CustomViewRepository extends JpaRepository<CustomView, String>, JpaSpecificationExecutor<CustomView> {

    @Transactional
    void deleteByUserIdAndViewType(String orgUnitId, String viewType);

    @Transactional
    void deleteByUserIdAndViewTypeAndIdNotIn(String orgUnitId, String viewType, List<String> ids);

    List<CustomView> findByUserIdAndViewTypeOrderByTabIndex(String userId, String viewType);

    @Query("select max(t.tabIndex) from CustomView t where t.userId=?1 and t.viewType=?2")
    Integer getMaxTabIndex(String userId, String viewType);

}