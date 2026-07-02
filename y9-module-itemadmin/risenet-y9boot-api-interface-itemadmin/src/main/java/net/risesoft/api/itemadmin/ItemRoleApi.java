package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
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
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @param processInstanceId 流程实例Id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findAllPermUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findAllPermUser(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey, @RequestParam Integer principalType,
        @RequestParam(required = false) String id, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId);

    /**
     * 根据角色id获取发送人
     * 
     * @param roleId 角色id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     */
    @GetMapping("/findByRoleId")
    Y9Result<List<ItemRoleOrgUnitModel>> findByRoleId(@RequestParam String roleId, @RequestParam Integer principalType,
        @RequestParam(required = false) String id);

    /**
     * 获取抄送选人
     *
     * @param id 唯一标识
     * @param principalType 类型:2(部门)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUser")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam(required = false) String id,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId);

    /**
     * 获取抄送选人
     *
     * @param id 唯一标识
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUser4Bureau")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUser4Bureau(@RequestParam(required = false) String id);

    /**
     * 获取委办局
     *
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUserBureau")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserBureau(@RequestParam Integer principalType);

    /**
     * 抄送选人搜索
     *
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例Id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findCsUserSearch")
    Y9Result<List<ItemRoleOrgUnitModel>> findCsUserSearch(@RequestParam String name,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId);

    /**
     * 获取发送人
     *
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
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey, @RequestParam Integer principalType,
        @RequestParam(required = false) String id, @RequestParam(required = false) String processInstanceId);

    /**
     * 发送选人搜索
     *
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
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserByName(@RequestParam(required = false) String name,
        @RequestParam Integer principalType, @RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam(required = false) String processInstanceId);

    /**
     * 获取发送人（收发单位）
     *
     * @param id 父节点id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/findPermUserSendReceive")
    Y9Result<List<ItemRoleOrgUnitModel>> findPermUserSendReceive(@RequestParam(required = false) String id);

    /**
     * 获取组织机构树
     *
     * @param id 组织架构id
     * @param treeType 树的类型
     * @param name 人员名称
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @GetMapping("/getOrgTree")
    Y9Result<List<ItemRoleOrgUnitModel>> getOrgTree(@RequestParam(required = false) String id,
        @RequestParam OrgTreeTypeEnum treeType, @RequestParam(required = false) String name);

}
