package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.BackTaskConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemBackTaskConfService {

    /**
     * 复制该事项绑定的指定的流程定义的上一个版本流程定义的任务配置到该版本
     *
     * @param itemId 事项唯一标示
     * @param processDefinitionId 流程定义Id
     */
    void copyTaskConf(String itemId, String processDefinitionId);

    /**
     * 查当前任务节点的配置，当前任务节点没有配置则查询流程定义的配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @return BackTaskConf
     */
    BackTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 查询当前任务节节点的配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 节点Id
     * @return List<BackTaskConf>
     */
    List<BackTaskConf> listByTaskDefKey(String itemId, String processDefinitionId, String taskDefKey);

    /**
     * 删除退回任务绑定信息
     *
     * @param itemId 事项唯一标示
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 任务节点Id
     * @param removeTaskKey 删除退回任务节点Id
     */
    void removeBind(String itemId, String processDefinitionId, String taskDefKey, String[] removeTaskKey);

    /**
     * 保存退回任务绑定信息
     *
     * @param itemId 事项唯一标示
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 退回任务节点Id
     * @param bindTaskDefKey 绑定退回任务节点Id
     */
    void saveBindTask(String itemId, String processDefinitionId, String taskDefKey, String[] bindTaskDefKey);
}
