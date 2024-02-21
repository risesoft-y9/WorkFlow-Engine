package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.EformItemBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface EformItemBindRepository
    extends JpaRepository<EformItemBind, String>, JpaSpecificationExecutor<EformItemBind> {

    @Query("From EformItemBind t where t.formId in ?1")
    public List<EformItemBind> findByFormIdList(List<String> formIdList);

    @Query("From EformItemBind t where t.itemId=?1 and t.processDefinitionId=?2 order by t.tabIndex asc")
    public List<EformItemBind> findByItemIdAndProcDefId(String itemId, String procDefId);

    @Query("From EformItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.formId=?3 and (t.taskDefKey is null or length(trim(t.taskDefKey))=0) order by t.tabIndex asc")
    public EformItemBind findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(String itemId, String procDefId,
        String formId);

    @Query("From EformItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 order by t.tabIndex asc")
    public List<EformItemBind> findByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId,
        String taskDefKey);

    @Query("From EformItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 and t.formId=?4 order by t.tabIndex asc")
    public EformItemBind findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(String itemId, String procDefId,
        String taskDefKey, String formId);

    @Query("From EformItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and (t.taskDefKey is null or length(trim(t.taskDefKey))=0) order by t.tabIndex asc")
    public List<EformItemBind> findByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId);

    @Query("select count(*) from EformItemBind t where t.processDefinitionId=?1")
    public int getCountByProcessDefinitionId(String procDefId);

    @Query("select count(*) from EformItemBind t where t.processDefinitionId like ?1")
    public int getCountByProcessDefinitionIdLike(String procDefId);
}
