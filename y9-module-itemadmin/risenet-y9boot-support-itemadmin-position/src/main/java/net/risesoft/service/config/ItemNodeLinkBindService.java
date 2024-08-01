package net.risesoft.service.config;

import net.risesoft.entity.ItemNodeLinkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public interface ItemNodeLinkBindService {

    /**
     * 复制流程版本配置
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 复制事项链接绑定信息
     *
     * @param itemId
     * @param newItemId
     * @param latestpdId
     */
    void copyBindInfo(String itemId, String newItemId, String latestpdId);

    /**
     * 删除事项链接绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据事项id、流程定义id、任务节点key获取绑定列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    ItemNodeLinkBind listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 根据id删除绑定关系
     *
     * @param bindId
     */
    void removeBind(String bindId);

    /**
     * 根据id删除权限绑定
     *
     * @param ids
     */
    void removeRole(String[] ids);

    /**
     * 保存绑定角色
     *
     * @param itemLinkId 绑定关系id
     * @param roleIds 角色id
     */
    void saveBindRole(String itemLinkId, String roleIds);

    /**
     * 保存绑定关系
     *
     * @param itemId
     * @param linkId
     * @param processDefinitionId
     * @param taskDefKey
     */
    void saveItemNodeLinkBind(String itemId, String linkId, String processDefinitionId, String taskDefKey);
}
