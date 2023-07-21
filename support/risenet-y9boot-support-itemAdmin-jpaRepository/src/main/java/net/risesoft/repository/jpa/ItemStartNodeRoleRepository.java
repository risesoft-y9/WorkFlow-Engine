package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemStartNodeRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemStartNodeRoleRepository extends JpaRepository<ItemStartNodeRole, String>, JpaSpecificationExecutor<ItemStartNodeRole> {

    List<ItemStartNodeRole> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    ItemStartNodeRole findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId, String taskDefKey);

    List<ItemStartNodeRole> findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(String itemId, String processDefinitionId);

    List<ItemStartNodeRole> findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(String itemId, String processDefinitionId);

    @Query("select max(tabIndex) from ItemStartNodeRole t where t.itemId=?1 and t.processDefinitionId=?2")
    Integer getMaxTabIndex(String itemId, String processDefinitionId);
}
