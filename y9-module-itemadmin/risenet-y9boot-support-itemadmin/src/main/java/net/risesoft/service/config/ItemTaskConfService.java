package net.risesoft.service.config;

import net.risesoft.entity.ItemTaskConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemTaskConfService {

    /**
     * Description:复制任务签收绑定信息
     *
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 复制该事项绑定的指定的流程定义的上一个版本流程定义的任务配置到该版本
     *
     * @param itemId
     * @param processDefinitionId
     */
    void copyTaskConf(String itemId, String processDefinitionId);

    /**
     * 删除
     *
     * @param id 主键Id
     */
    void delete(String id);

    /**
     * Description:删除任务绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 查找一个
     *
     * @param id
     * @return
     */
    ItemTaskConf findById(String id);

    /**
     * 查当前任务节点的配置，当前任务节点没有配置则查询流程定义的配置
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 只查当前任务节点的配置，当前任务节点没有配置则返回null
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 查询是否具有主协办状态
     *
     * @param itemId 事项唯一标示
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @return true or false
     */
    boolean getSponserStatus(String itemId, String processDefinitionId, String taskDefKey);

    /**
     * Description:
     *
     * @param t
     */
    void save(ItemTaskConf t);

    /**
     * Description:
     *
     * @param id
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    ItemTaskConf save(String id, String processDefinitionId, String taskDefKey);
}
