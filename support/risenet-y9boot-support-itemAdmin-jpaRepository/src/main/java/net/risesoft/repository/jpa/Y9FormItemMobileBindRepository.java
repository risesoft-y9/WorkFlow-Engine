package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.Y9FormItemMobileBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9FormItemMobileBindRepository
    extends JpaRepository<Y9FormItemMobileBind, String>, JpaSpecificationExecutor<Y9FormItemMobileBind> {

    @Query("From Y9FormItemMobileBind t where t.formId in ?1")
    public List<Y9FormItemMobileBind> findByFormIdList(List<String> formIdList);

    @Query("From Y9FormItemMobileBind t where t.itemId=?1 and t.processDefinitionId=?2")
    public List<Y9FormItemMobileBind> findByItemIdAndProcDefId(String itemId, String procDefId);

    @Query("From Y9FormItemMobileBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.formId=?3 and (t.taskDefKey is null or length(trim(t.taskDefKey))=0)")
    public Y9FormItemMobileBind findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(String itemId, String procDefId,
        String formId);

    @Query("From Y9FormItemMobileBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3")
    public List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId,
        String taskDefKey);

    @Query("From Y9FormItemMobileBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 and t.formId=?4")
    public Y9FormItemMobileBind findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(String itemId, String procDefId,
        String taskDefKey, String formId);

    @Query("From Y9FormItemMobileBind t where t.itemId=?1 and t.processDefinitionId=?2 and (t.taskDefKey is null or length(trim(t.taskDefKey))=0)")
    public List<Y9FormItemMobileBind> findByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId);

    public List<Y9FormItemMobileBind> findByItemIdAndProcessDefinitionId(String itemId, String procDefId);

    @Query("select count(*) from Y9FormItemMobileBind t where t.processDefinitionId=?1")
    public int getCountByProcessDefinitionId(String procDefId);

    @Query("select count(*) from Y9FormItemMobileBind t where t.processDefinitionId like ?1")
    public int getCountByProcessDefinitionIdLike(String procDefId);
}
