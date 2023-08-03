package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.ItemOpinionFrameBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public interface ItemOpinionFrameBindService {

    /**
     * 改变是否必签意见
     *
     * @param id
     * @param signOpinion
     * @return
     */
    void changeSignOpinion(String id, Boolean signOpinion);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * Description:
     * 
     * @param id
     */
    void delete(String id);

    /**
     * Description:
     * 
     * @param ids
     */
    void delete(String[] ids);

    /**
     * Description:
     * 
     * @param page
     * @param rows
     * @return
     */
    Page<ItemOpinionFrameBind> findAll(int page, int rows);

    /**
     * Description:
     * 
     * @param itemId
     * @return
     */
    List<ItemOpinionFrameBind> findByItemId(String itemId);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param opinionFrameMark
     * @return
     */
    ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(String itemId,
        String processDefinitionId, String taskDefKey, String opinionFrameMark);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * Description:
     * 
     * @param mark
     * @return
     */
    List<ItemOpinionFrameBind> findByMark(String mark);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    ItemOpinionFrameBind findOne(String id);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    List<String> getBindOpinionFrame(String itemId, String processDefinitionId);

    /**
     * Description:
     * 
     * @param opinionFrameTaskRoleBind
     * @return
     */
    ItemOpinionFrameBind save(ItemOpinionFrameBind opinionFrameTaskRoleBind);

    /**
     * Description:
     * 
     * @param opinionFrameNameAndMarks
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     */
    void save(String opinionFrameNameAndMarks, String itemId, String processDefinitionId, String taskDefKey);
}
