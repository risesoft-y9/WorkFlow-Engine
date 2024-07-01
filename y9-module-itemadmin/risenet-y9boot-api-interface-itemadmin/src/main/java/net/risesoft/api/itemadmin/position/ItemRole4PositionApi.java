package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemRole4PositionApi {

    /**
     * Description: 根据id获取发送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/findCsUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("id") String id, @RequestParam("principalType") Integer principalType,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 查询委办局下的部门
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/findCsUserBureau")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserBureau(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("principalType") Integer principalType);

    /**
     * 抄送搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例Id
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/findCsUserSearch")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserSearch(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("name") String name, @RequestParam("principalType") Integer principalType,
        @RequestParam("processInstanceId") String processInstanceId);

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
    @GetMapping("/findPermUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("principalType") Integer principalType,
        @RequestParam("id") String id, @RequestParam("processInstanceId") String processInstanceId);

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
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/findPermUserByName")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserByName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("name") String name, @RequestParam("principalType") Integer principalType,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * Description: 获取发送人（收发单位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id id
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/findPermUserSendReceive")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserSendReceive(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("id") String id);

    /**
     * 获取组织机构树
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 组织架构id
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @param name 人员名称
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping("/getOrgTree")
    Y9Result<List<ItemRoleOrgUnitModel>> getOrgTree(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("id") String id,
        @RequestParam("treeType") OrgTreeTypeEnum treeType, @RequestParam("name") String name);

}
