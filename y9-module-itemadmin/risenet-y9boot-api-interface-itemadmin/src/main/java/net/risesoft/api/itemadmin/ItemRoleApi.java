package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.pojo.Y9Result;

/**
 * 发送选人接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemRoleApi {

    /**
     * 获取发送人gfg
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @param processInstanceId 流程实例Id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findAllPermUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findAllPermUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("principalType") Integer principalType,
        @RequestParam(value = "id", required = false) String id,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 获取抄送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 岗位id
     * @param id 唯一标识
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "id", required = false) String id, @RequestParam("principalType") Integer principalType,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 获取抄送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 岗位id
     * @param id 唯一标识
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUser4Bureau")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUser4Bureau(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "id", required = false) String id);

    /**
     * 获取委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUserBureau")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserBureau(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("principalType") Integer principalType);

    /**
     * 抄送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例Id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUserSearch")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserSearch(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "name") String name, @RequestParam("principalType") Integer principalType,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 获取发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @param processInstanceId 流程实例Id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findPermUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("principalType") Integer principalType,
        @RequestParam(value = "id", required = false) String id,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 发送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 流程定义中节点Id
     * @param processInstanceId 流程实例Id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findPermUserByName")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserByName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam("principalType") Integer principalType, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 获取发送人（收发单位）
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param id 父节点id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findPermUserSendReceive")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserSendReceive(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam(value = "id", required = false) String id);

    /**
     * 获取组织机构树
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param id 组织架构id
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @param name 人员名称
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/getOrgTree")
    Y9Result<List<ItemRoleOrgUnitModel>> getOrgTree(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam(value = "id", required = false) String id,
        @RequestParam("treeType") OrgTreeTypeEnum treeType,
        @RequestParam(value = "name", required = false) String name);

}
