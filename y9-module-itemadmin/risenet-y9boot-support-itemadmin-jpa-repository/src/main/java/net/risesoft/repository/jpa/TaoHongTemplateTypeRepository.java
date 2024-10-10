package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TaoHongTemplateType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TaoHongTemplateTypeRepository
    extends JpaRepository<TaoHongTemplateType, String>, JpaSpecificationExecutor<TaoHongTemplateType> {
    @Override
    @Query("from TaoHongTemplateType t order by t.tabIndex")
    List<TaoHongTemplateType> findAll();

    @Query("from TaoHongTemplateType t where t.bureauId=?1 order by t.tabIndex")
    List<TaoHongTemplateType> findByBureauId(String bureauId);

    @Query("select max(t.tabIndex) from TaoHongTemplateType t where t.bureauId=?1")
    Integer getMaxTabIndex(String bureauId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update TaoHongTemplateType t set t.tabIndex=?1 where t.id=?2")
    void update4Order(Integer tabIndex, String id);
}
