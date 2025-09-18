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
     * @param id 唯一标识
     * @param signOpinion 是否必签意见
     */
    void changeSignOpinion(String id, Boolean signOpinion);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 复制意见框绑定信息
     *
     * @param itemId 事项id
     * @param newItemId 新事项id
     * @param lastVersionPid 最新的流程定义id
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 删除意见框绑定信息
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 
     * @param ids 唯一标识集合
     */
    void delete(String[] ids);

    /**
     * 删除意见框绑定和角色绑定信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点id
     * @param opinionFrameMark 意见框标识
     * @return ItemOpinionFrameBind
     */
    ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(String itemId,
        String processDefinitionId, String taskDefKey, String opinionFrameMark);

    /**
     * TODO
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return List<String>
     */
    List<String> getBindOpinionFrame(String itemId, String processDefinitionId);

    /**
     * 
     * @param id 唯一标识
     * @return ItemOpinionFrameBind
     */
    ItemOpinionFrameBind getById(String id);

    /**
     * 
     * @param itemId 事项id
     * @return List<ItemOpinionFrameBind>
     */
    List<ItemOpinionFrameBind> listByItemId(String itemId);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return List<ItemOpinionFrameBind>
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点id
     * @return List<ItemOpinionFrameBind>
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点id
     * @return List<ItemOpinionFrameBind>
     */
    List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey);

    /**
     * 
     * @param mark 意见框标识
     * @return List<ItemOpinionFrameBind>
     */
    List<ItemOpinionFrameBind> listByMark(String mark);

    /**
     * 
     * @param page 索引
     * @param rows 每页数量
     * @return Page<ItemOpinionFrameBind>
     */
    Page<ItemOpinionFrameBind> pageAll(int page, int rows);

    /**
     * 
     * @param opinionFrameTaskRoleBind 绑定实体
     * @return ItemOpinionFrameBind
     */
    ItemOpinionFrameBind save(ItemOpinionFrameBind opinionFrameTaskRoleBind);

    /**
     * 
     * @param opinionFrameNameAndMarks 意见框名称和标识集合
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点id
     */
    void save(String opinionFrameNameAndMarks, String itemId, String processDefinitionId, String taskDefKey);
}
