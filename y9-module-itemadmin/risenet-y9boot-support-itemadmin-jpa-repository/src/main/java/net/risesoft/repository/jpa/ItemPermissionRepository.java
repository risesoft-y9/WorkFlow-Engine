package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemPermission;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemPermissionRepository
    extends JpaRepository<ItemPermission, String>, JpaSpecificationExecutor<ItemPermission> {

    List<ItemPermission> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    ItemPermission findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(String itemId, String processDefinitionId,
        String taskdefKey, String roleId);

    @Query("from ItemPermission t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=''")
    List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(String itemId,
        String processDefinitionId);

    List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(String itemId,
        String processDefinitionId, String taskdefKey);

    @Query("select max(t.tabIndex) from ItemPermission t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3")
    Integer getMaxTabIndex(String itemId, String processDefinitionId, String taskDefKey);

    @Transactional
    void deleteByItemId(String itemId);
}
