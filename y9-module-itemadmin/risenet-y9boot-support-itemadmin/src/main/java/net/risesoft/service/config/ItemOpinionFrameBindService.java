package net.risesoft.service.config;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.opinion.ItemOpinionFrameBind;

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
     * Description: 复制意见框绑定信息
     *
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * Description: 删除意见框绑定信息
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
     * Description:删除意见框绑定和角色绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

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
     * @return
     */
    List<String> getBindOpinionFrame(String itemId, String processDefinitionId);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    ItemOpinionFrameBind getById(String id);

    /**
     * Description:
     *
     * @param itemId
     * @return
     */
    List<ItemOpinionFrameBind> listByItemId(String itemId);

    /**
     * Description:
     *
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * Description:
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * Description:
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * Description:
     *
     * @param mark
     * @return
     */
    List<ItemOpinionFrameBind> listByMark(String mark);

    /**
     * Description:
     *
     * @param page
     * @param rows
     * @return
     */
    Page<ItemOpinionFrameBind> pageAll(int page, int rows);

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
