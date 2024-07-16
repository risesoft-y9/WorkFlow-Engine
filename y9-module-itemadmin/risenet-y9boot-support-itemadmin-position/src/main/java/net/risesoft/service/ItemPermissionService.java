package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ItemPermission;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemPermissionService {

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void copyPerm(String itemId, String processDefinitionId);

    /**
     * 根据唯一标示查找
     *
     * @param id
     */
    void delete(String id);

    /**
     * 根据事项Id删除所有授权
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据事项Id，流程定义Id,和任务节点Key查找授权
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 根据事项Id,流程定义Id,任务节点Key,角色Id查找授权
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskdefKey
     * @param roleId
     * @return
     */
    ItemPermission findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(String itemId, String processDefinitionId,
        String taskdefKey, String roleId);

    /**
     * 根据事项Id，流程定义Id,和任务节点Key查找授权,如果任务节点key没有绑定权限，则查找流程节点绑定的权限
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 
     * Description: 判断当前taskDefKey所拥有的权限角色主体是否包含人员、部门、角色、岗位
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param processInstanceId
     * @return
     */
    Map<String, Object> getTabMap(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId);

    /**
     * 
     * Description: 判断当前taskDefKey所拥有的权限角色主体是否包含人员、部门、角色、岗位
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param processInstanceId
     * @return
     */
    Map<String, Object> getTabMap4Position(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId);

    /**
     * Description: 删除当前事项绑定的流程对应的最新流程定义的所有权限
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void removePerm(String itemId, String processDefinitionId);

    /**
     * 
     * Description: 将值设置到ItemPermission中
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskdefKey
     * @param roleId
     * @param roleType
     * @return
     */
    ItemPermission save(String itemId, String processDefinitionId, String taskdefKey, String roleId, Integer roleType);
}
