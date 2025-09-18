package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemStartNodeRole;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.platform.Role;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemStartNodeRoleService {

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 删除绑定路由节点角色信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 
     *
     * @param id 唯一标识
     * @return ItemStartNodeRole
     */
    ItemStartNodeRole findById(String id);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @return ItemStartNodeRole
     */
    ItemStartNodeRole findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 获取有权限的优先级最大的开始任务节点，如果都没有权限，则返回优先级最小的，如果没有初始化权限，则返回第一个节点
     *
     * @param itemId 事项id
     * @return String - 开始节点key
     */
    String getStartTaskDefKey(String itemId);

    /**
     * 获取有权限的所有任务节点
     *
     * @param itemId 事项id
     * @return List<ItemStartNodeRoleModel>
     */
    List<ItemStartNodeRoleModel> getAllStartTaskDefKey(String itemId);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @param taskDefName 任务节点名称
     */
    void initRole(String itemId, String processDefinitionId, String taskDefKey, String taskDefName);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return List<ItemStartNodeRole>
     */
    List<ItemStartNodeRole> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @return List<Role>
     */
    List<Role> listRoleByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @param roleIds 角色id
     */
    void removeRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds);

    /**
     * 
     * 保存排序
     * 
     * @param idAndTabIndexs idAndTabIndexs
     */
    void saveOrder(String[] idAndTabIndexs);

    /**
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @param roleIds 角色id
     */
    void saveRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds);
}
