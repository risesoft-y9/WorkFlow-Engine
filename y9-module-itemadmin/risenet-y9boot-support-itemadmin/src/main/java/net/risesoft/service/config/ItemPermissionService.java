package net.risesoft.service.config;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ItemPermission;
import net.risesoft.enums.ItemPermissionEnum;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemPermissionService {

    /**
     * 
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     */
    void copyPerm(String itemId, String processDefinitionId);

    /**
     * 根据唯一标示查找
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据事项Id删除所有授权
     *
     * @param itemId 事项Id
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据事项Id,流程定义Id,任务节点Key,角色Id查找授权
     *
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     * @param taskdefKey 任务节点Key
     * @param roleId 角色Id
     * @return ItemPermission
     */
    ItemPermission findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(String itemId, String processDefinitionId,
        String taskdefKey, String roleId);

    /**
     * TODO 判断当前taskDefKey所拥有的权限角色主体是否包含人员、部门、角色、岗位
     * 
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 任务节点Key
     * @param processInstanceId 流程实例Id
     * @param taskId 任务Id
     * @return Map<String, Object>
     */
    Map<String, Object> getTabMap(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId, String taskId);

    /**
     * 根据事项Id，流程定义Id,和任务节点Key查找授权
     *
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 任务节点Key
     * @return List<ItemPermission>
     */
    List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 根据事项Id，流程定义Id,和任务节点Key查找授权,如果任务节点key没有绑定权限，则查找流程节点绑定的权限
     *
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 任务节点Key
     * @return List<ItemPermission>
     */
    List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 删除当前事项绑定的流程对应的最新流程定义的所有权限
     *
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     */
    void removePerm(String itemId, String processDefinitionId);

    /**
     *
     * 将值设置到ItemPermission中
     *
     * @param itemId 事项Id
     * @param processDefinitionId 流程定义Id
     * @param taskdefKey 任务节点Key
     * @param roleId 角色Id
     * @param roleType 角色类型
     * @return ItemPermission
     */
    ItemPermission save(String itemId, String processDefinitionId, String taskdefKey, String roleId,
        ItemPermissionEnum roleType);
}
