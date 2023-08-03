package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemOpinionFrameBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemOpinionFrameBindRepository
    extends JpaRepository<ItemOpinionFrameBind, String>, JpaSpecificationExecutor<ItemOpinionFrameBind> {

    List<ItemOpinionFrameBind> findByItemId(String itemId);

    @Query("from ItemOpinionFrameBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.opinionFrameMark=?3 and t.taskDefKey=''")
    ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndOpinionFrameMarkAndTaskDefKeyIsNull(String itemId,
        String processDefinitionId, String opinionFrameMark);

    ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(String itemId,
        String processDefinitionId, String taskDefKey, String opinionFrameMark);

    List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(String itemId,
        String processDefinitionId, String taskDefKey);

    List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdOrderByCreateDateAsc(String itemId,
        String processDefinitionId);

    List<ItemOpinionFrameBind> findByOpinionFrameMarkOrderByItemIdDescModifyDateDesc(String mark);

    @Query("SELECT t.opinionFrameMark from ItemOpinionFrameBind t where t.itemId=?1 and t.processDefinitionId=?2 GROUP BY t.opinionFrameMark")
    List<String> getBindOpinionFrame(String itemId, String processDefinitionId);

}
