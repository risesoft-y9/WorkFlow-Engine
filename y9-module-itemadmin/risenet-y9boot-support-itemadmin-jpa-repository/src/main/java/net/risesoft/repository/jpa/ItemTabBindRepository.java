package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemTabBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemTabBindRepository
    extends JpaRepository<ItemTabBind, String>, JpaSpecificationExecutor<ItemTabBind> {

    ItemTabBind findByItemIdAndProcessDefinitionIdAndTabId(String itemId, String processDefinitionId, String tabId);

    List<ItemTabBind> findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(String itemId, String processDefinitionId);

    @Query("select max(tabIndex) from ItemTabBind t where t.itemId=?1 and t.processDefinitionId=?2")
    Integer getMaxTabIndex(String itemId, String processDefinitionId);
}
