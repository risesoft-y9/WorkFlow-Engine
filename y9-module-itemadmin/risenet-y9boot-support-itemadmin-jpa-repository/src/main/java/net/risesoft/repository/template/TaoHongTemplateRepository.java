package net.risesoft.repository.template;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.template.TaoHongTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TaoHongTemplateRepository
    extends JpaRepository<TaoHongTemplate, String>, JpaSpecificationExecutor<TaoHongTemplate> {
    @Query("from TaoHongTemplate t where t.bureauGuid=?1 order by t.tabIndex asc")
    List<TaoHongTemplate> findByBureauGuid(String bureauGuid);

    @Query("from TaoHongTemplate t where t.tenantId=?1 and t.bureauName like ?2 order by t.tabIndex asc")
    List<TaoHongTemplate> findByTenantId(String tenantId, String name);

    /**
     * 获取最大的tabIndex
     * 
     * @return
     */
    @Query("select max(tabIndex) from TaoHongTemplate")
    Integer getMaxTabIndex();
}
