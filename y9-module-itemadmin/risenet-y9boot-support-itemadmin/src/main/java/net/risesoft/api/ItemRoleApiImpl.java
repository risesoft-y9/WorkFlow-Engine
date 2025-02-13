package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.RoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发送选人接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemRole", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemRoleApiImpl implements ItemRoleApi {

    private final RoleService roleService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final OrganizationApi organizationApi;

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
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findAllPermUser(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String orgUnitId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey, @RequestParam Integer principalType,
        String id, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list =
            roleService.listAllPermUser(itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
        return Y9Result.success(list);
    }

    /**
     * 获取抄送选人组织机构数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param id 唯一标识
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String orgUnitId, String id, @RequestParam Integer principalType, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list = roleService.listCsUser(id, principalType, processInstanceId);
        return Y9Result.success(list);
    }

    /**
     * 获取抄送选人组织机构数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param id 唯一标识
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUser4Bureau(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String orgUnitId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list = roleService.listCsUser4Bureau(id);
        return Y9Result.success(list);
    }

    /**
     * 获取委办局组织机构数据
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUserBureau(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam Integer principalType) {
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        if (Objects.equals(principalType, ItemPrincipalTypeEnum.DEPT.getValue())) {
            OrgUnit orgunit = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
            model.setId(orgunit.getId());
            model.setName(orgunit.getName());
            model.setOrgType(orgunit.getOrgType().getValue());
            model.setIsParent(OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()));
            item.add(model);
        }
        return Y9Result.success(item);
    }

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
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUserSearch(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String orgUnitId, @RequestParam String name,
        @RequestParam Integer principalType, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list = roleService.listCsUserSearch(name, principalType, processInstanceId);
        return Y9Result.success(list);
    }

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
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String orgUnitId, @RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam Integer principalType, String id, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list =
            roleService.listPermUser(itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
        return Y9Result.success(list);
    }

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
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUserByName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String orgUnitId, String name, @RequestParam Integer principalType,
        @RequestParam String itemId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<ItemRoleOrgUnitModel> list = roleService.listPermUserByName(name, itemId, processDefinitionId, taskDefKey,
            principalType, processInstanceId);
        return Y9Result.success(list);
    }

    /**
     * 获取发送人（收发单位）
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param id 父节点id
     * @return {@code Y9Result<List<ItemRoleOrgUnitModel>>} 通用请求返回对象 - data 是发送选人组织架构
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUserSendReceive(@RequestParam String tenantId,
        @RequestParam String orgUnitId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        List<ItemRoleOrgUnitModel> list = roleService.listPermUserSendReceive(id);
        return Y9Result.success(list);
    }

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
    @Override
    public Y9Result<List<ItemRoleOrgUnitModel>> getOrgTree(@RequestParam String tenantId,
        @RequestParam String orgUnitId, String id, @RequestParam OrgTreeTypeEnum treeType, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<Organization> org = organizationApi.list(tenantId).getData();
            if (org != null && !org.isEmpty()) {
                id = org.get(0).getId();
            }
        }
        List<OrgUnit> orgUnitList;
        if (StringUtils.isNotBlank(name)) {
            orgUnitList = orgUnitApi.treeSearch(tenantId, name, treeType).getData();
        } else {
            orgUnitList = orgUnitApi.getSubTree(tenantId, id, treeType).getData();
        }
        for (OrgUnit orgUnit : orgUnitList) {
            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
            model.setId(orgUnit.getId());
            model.setName(orgUnit.getName());
            model.setParentId(orgUnit.getParentId());
            model.setIsParent(false);
            model.setOrgType(orgUnit.getOrgType().getValue());
            if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())) {
                model.setIsParent(true);
            } else if (OrgTypeEnum.POSITION.equals(orgUnit.getOrgType())) {
                model.setPerson("6:" + orgUnit.getId());
            }
            item.add(model);
        }
        return Y9Result.success(item);
    }
}
