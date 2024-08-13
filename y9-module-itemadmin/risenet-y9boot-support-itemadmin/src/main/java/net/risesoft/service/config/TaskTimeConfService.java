package net.risesoft.service.config;

import net.risesoft.entity.TaskTimeConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TaskTimeConfService {

    /**
     * Description:复制任务配置信息
     *
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 复制该事项配置的指定的流程定义的上一个版本流程定义的任务配置到该版本
     *
     * @param itemId
     * @param processDefinitionId
     */
    void copyTaskConf(String itemId, String processDefinitionId);

    /**
     * Description:删除任务配置信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 查当前任务节点的配置
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    TaskTimeConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * Description:
     *
     * @param t
     */
    void save(TaskTimeConf t);

}
