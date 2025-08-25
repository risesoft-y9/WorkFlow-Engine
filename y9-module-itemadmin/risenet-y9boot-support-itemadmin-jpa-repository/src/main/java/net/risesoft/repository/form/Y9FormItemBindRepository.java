package net.risesoft.repository.form;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.form.Y9FormItemBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Y9FormItemBindRepository
    extends JpaRepository<Y9FormItemBind, String>, JpaSpecificationExecutor<Y9FormItemBind> {

    @Query("From Y9FormItemBind t where t.formId in ?1 order by t.processDefinitionId desc")
    List<Y9FormItemBind> findByFormIdList(List<String> formIdList);

    @Query("From Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 order by t.tabIndex asc")
    List<Y9FormItemBind> findByItemIdAndProcDefId(String itemId, String procDefId);

    @Query("From Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.formId=?3 and (t.taskDefKey is null or t.taskDefKey='') order by t.tabIndex asc")
    Y9FormItemBind findByItemIdAndProcDefIdAndAndFormIdAndTaskDefKeyIsNull(String itemId, String procDefId,
        String formId);

    @Query("From Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 order by t.tabIndex asc")
    List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKey(String itemId, String procDefId, String taskDefKey);

    @Query("From Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and t.taskDefKey=?3 and t.formId=?4 order by t.tabIndex asc")
    Y9FormItemBind findByItemIdAndProcDefIdAndTaskDefKeyAndFormId(String itemId, String procDefId, String taskDefKey,
        String formId);

    @Query("From Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 and (t.taskDefKey is null or t.taskDefKey='') order by t.tabIndex asc")
    List<Y9FormItemBind> findByItemIdAndProcDefIdAndTaskDefKeyIsNull(String itemId, String procDefId);

    List<Y9FormItemBind> findByItemIdAndProcessDefinitionIdOrderByTabIndexAsc(String itemId, String procDefId);

    @Query("select count(*) from Y9FormItemBind t where t.processDefinitionId=?1")
    int getCountByProcessDefinitionId(String procDefId);

    @Query("select count(*) from Y9FormItemBind t where t.processDefinitionId like ?1")
    int getCountByProcessDefinitionIdLike(String procDefId);

    @Transactional
    void deleteByItemId(String itemId);

    @Query("select max(t.tabIndex) from Y9FormItemBind t where t.itemId=?1 and t.processDefinitionId=?2 ")
    Integer getMaxTabIndex(String itemId, String processDefinitionId);

    /**
     * 根据id修改tabIndex
     *
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional
    @Query("update Y9FormItemBind t set t.tabIndex=?1 where t.id=?2")
    void updateOrder(Integer tabIndex, String id);
}
