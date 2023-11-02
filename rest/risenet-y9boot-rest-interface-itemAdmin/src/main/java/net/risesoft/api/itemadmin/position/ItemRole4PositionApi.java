package net.risesoft.api.itemadmin.position;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemRole4PositionApi {

    /**
     *
     * Description: 根据id获取发送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findCsUser(String tenantId, String userId, String positionId, String id,
        Integer principalType, String processInstanceId);

    /**
     * 查询委办局下的部门
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findCsUserBureau(String tenantId, String userId, String positionId,
        Integer principalType);

    /**
     * 抄送搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例Id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findCsUserSearch(String tenantId, String userId, String positionId, String name,
        Integer principalType, String processInstanceId);

    /**
     * 获取发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @param processInstanceId 流程实例Id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findPermUser(String tenantId, String userId, String positionId, String itemId,
        String processDefinitionId, String taskDefKey, Integer principalType, String id, String processInstanceId);

    /**
     * 发送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 流程定义中节点Id
     * @param processInstanceId 流程实例Id
     *
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findPermUserByName(String tenantId, String userId, String positionId, String name,
        Integer principalType, String itemId, String processDefinitionId, String taskDefKey, String processInstanceId);

    /**
     *
     * Description: 获取发送人（收发单位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> findPermUserSendReceive(String tenantId, String positionId, String id);

    /**
     * 获取组织机构树
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 组织架构id
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @param name 人员名称
     *
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getOrgTree(String tenantId, String positionId, String id, String treeType, String name);
}
