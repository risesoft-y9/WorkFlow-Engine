package net.risesoft.repository.template;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.template.TaoHongTemplateType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TaoHongTemplateTypeRepository
    extends JpaRepository<TaoHongTemplateType, String>, JpaSpecificationExecutor<TaoHongTemplateType> {

    List<TaoHongTemplateType> findByBureauId(String bureauId, Sort sort);

    @Query("select max(t.tabIndex) from TaoHongTemplateType t where t.bureauId=?1")
    Integer getMaxTabIndex(String bureauId);

    @Modifying
    @Transactional
    @Query("update TaoHongTemplateType t set t.tabIndex=?1 where t.id=?2")
    void update4Order(Integer tabIndex, String id);
}
