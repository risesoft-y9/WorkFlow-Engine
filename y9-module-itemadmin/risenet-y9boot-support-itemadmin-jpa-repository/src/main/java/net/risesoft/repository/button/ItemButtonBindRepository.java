package net.risesoft.repository.button;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.button.ItemButtonBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemButtonBindRepository
    extends JpaRepository<ItemButtonBind, String>, JpaSpecificationExecutor<ItemButtonBind> {

    List<ItemButtonBind> findByButtonIdOrderByItemIdDescUpdateTimeDesc(String buttonId);

    List<ItemButtonBind> findByItemId(String itemId);

    @Query("from ItemButtonBind t where t.itemId=?1 and t.buttonType=?2 and t.processDefinitionId=?3 and (t.taskDefKey is null or t.taskDefKey='') order by t.tabIndex ASC")
    List<ItemButtonBind> findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyIsNullOrderByTabIndexAsc(
        String itemId, Integer buttonType, String processDefinitionId);

    List<ItemButtonBind> findByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(String itemId,
        Integer buttonType, String processDefinitionId, String taskDefKey);

    List<ItemButtonBind> findByItemIdAndButtonTypeAndProcessDefinitionIdOrderByTabIndexAsc(String itemId,
        Integer buttonType, String processDefinitionId);

    List<ItemButtonBind> findByItemIdAndButtonTypeAndTaskDefKeyOrderByTabIndexAsc(String itemId, int i, String string);

    ItemButtonBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndButtonIdOrderByTabIndexAsc(String itemId,
        String processDefinitionId, String taskDefKey, String buttonId);

    @Query("from ItemButtonBind t where t.itemId=?1 and t.processDefinitionId=?2 and (t.taskDefKey is null or t.taskDefKey='') and t.buttonId=?3 order by t.tabIndex ASC")
    ItemButtonBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullAndButtonIdOrderByTabIndexAsc(String itemId,
        String processDefinitionId, String buttonId);

    @Query("from ItemButtonBind t where t.itemId=?1 and t.processDefinitionId=?2 and (t.taskDefKey is null or t.taskDefKey='') order by t.tabIndex ASC")
    List<ItemButtonBind> findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNullOrderByTabIndexAsc(String itemId,
        String processDefinitionId);

    List<ItemButtonBind> findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(String itemId,
        String processDefinitionId, String taskDefKey);

    List<ItemButtonBind> findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(String itemId,
        String processDefinitionId);

    @Query("select max(t.tabIndex) from ItemButtonBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 and t.buttonType=?4")
    Integer getMaxTabIndex(String itemId, String processDefinitionId, String taskDefKey, Integer buttonType);
}