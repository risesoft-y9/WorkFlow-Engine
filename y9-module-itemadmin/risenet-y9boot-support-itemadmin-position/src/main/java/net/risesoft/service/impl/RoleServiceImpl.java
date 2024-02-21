package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Position;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.ItemPermissionService;
import net.risesoft.service.RoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private ItemPermissionService itemPermissionService;

    @Autowired
    private DynamicRoleMemberService dynamicRoleMemberService;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private OrganizationApi organizationManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private ReceiveDepartmentRepository receiveDepartmentRepository;

    @Autowired
    private CustomGroupApi customGroupApi;

    @Override
    public List<Map<String, Object>> findCsUser(String id, Integer principalType, String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            if (StringUtils.isBlank(id) || UtilConsts.NULL.equals(id)) {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    Organization organization = orgUnitManager.getOrganization(tenantId, userId).getData();
                    List<OrgUnit> orgUnitList = orgUnitManager
                        .getSubTree(tenantId, organization.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgUnit : orgUnitList) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", orgUnit.getId());
                        map.put("parentId", id);
                        map.put("name", orgUnit.getName());
                        map.put("isParent", orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT) ? true : false);
                        map.put("orgType", orgUnit.getOrgType());
                        map.put("principalType", orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                            ? ItemPermissionEnum.DEPARTMENT.getValue() : ItemPermissionEnum.POSITION.getValue());
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            map.put("person", "6:" + orgUnit.getId());
                        }
                        item.add(map);
                    }
                } else {
                    List<CustomGroup> grouplist = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                    for (CustomGroup customGroup : grouplist) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", customGroup.getId());
                        map.put("parentId", "");
                        map.put("name", customGroup.getGroupName());
                        map.put("isParent", true);
                        map.put("orgType", "customGroup");
                        map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                }
            } else {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    List<OrgUnit> orgList =
                        orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        Map<String, Object> map = new HashMap<>(16);
                        String orgunitId = orgunit.getId();
                        map.put("id", orgunitId);
                        map.put("parentId", id);
                        map.put("name", orgunit.getName());
                        map.put("orgType", orgunit.getOrgType());
                        map.put("isParent", OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()));
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            map.put("person", "6:" + orgunit.getId());
                            map.put("principalType", ItemPermissionEnum.POSITION.getValue());
                        }
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, id, OrgTypeEnum.POSITION)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Position position =
                                positionManager.getPosition(tenantId, customGroupMember.getMemberId()).getData();
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", customGroupMember.getMemberId());
                            map.put("parentId", id);
                            map.put("name", position.getName());
                            map.put("isParent", false);
                            map.put("orgType", position.getOrgType());
                            map.put("person", "6:" + position.getId() + ":" + position.getParentId());
                            map.put("principalType", ItemPermissionEnum.POSITION.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * Description:
     *
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    /**
     * Description:
     *
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    @Override
    public List<Map<String, Object>> findCsUserSearch(String name, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<Map<String, Object>> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            Organization organization = orgUnitManager.getOrganization(tenantId, userId).getData();
            List<Department> deptList = organizationManager.listDepartments(tenantId, organization.getId()).getData();
            List<OrgUnit> orgUnitListTemp = new ArrayList<>();
            for (OrgUnit orgUnitTemp : deptList) {
                orgUnitListTemp.addAll(orgUnitManager
                    .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, orgUnitTemp.getDn())
                    .getData());
            }
            for (OrgUnit orgUnitTemp : orgUnitListTemp) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", orgUnitTemp.getId());
                map.put("name", orgUnitTemp.getName());
                map.put("parentId", orgUnitTemp.getParentId());
                map.put("orgType", orgUnitTemp.getOrgType());
                if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                    map.put("isParent", true);
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                    map.put("isParent", true);
                    continue;
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.POSITION)) {
                    map.put("person", "6:" + orgUnitTemp.getId());
                    map.put("isParent", false);
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.PERSON)) {
                    continue;
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.GROUP)) {
                    continue;
                }
                boolean b = item.contains(map);
                if (!b) {
                    item.add(map);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                for (CustomGroup customGroup : grouplist) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", customGroup.getId());
                    map.put("parentId", "");
                    map.put("name", customGroup.getGroupName());
                    map.put("isParent", true);
                    map.put("orgType", "customGroup");
                    map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId,
                            customGroup.getId(), OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Position position =
                                positionManager.getPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (position != null && position.getName().contains(name)) {
                                Map<String, Object> map0 = new HashMap<>();
                                map0.put("id", customGroupMember.getMemberId());
                                map0.put("parentId", customGroup.getId());
                                map0.put("isParent", false);
                                map0.put("orgType", position.getOrgType());
                                map0.put("person", "6:" + position.getId() + ":" + position.getParentId());
                                map0.put("principalType", ItemPermissionEnum.POSITION.getValue());
                                map0.put("name", position.getName());
                                if (item.contains(map0)) {
                                    continue;// 去重
                                }
                                item.add(map0);
                                b = true;
                            }
                        }
                        if (b && !item.contains(map)) {
                            item.add(map);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return item;

    }

    @Override
    public List<Map<String, Object>> findPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> list = itemPermissionService
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                if (StringUtils.isBlank(id)) {
                    List<OrgUnit> deptList = new ArrayList<>();
                    for (ItemPermission o : list) {
                        if (o.getRoleType() == 1) {
                            deptList.addAll(roleManager
                                .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                            deptList.addAll(roleManager
                                .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                        }
                        if (o.getRoleType() == 2) {
                            deptList.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        if (o.getRoleType() == 4) {
                            List<OrgUnit> orgUnitList =
                                dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                            for (OrgUnit orgUnit : orgUnitList) {
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                    || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                    deptList.add(orgUnit);
                                }
                            }
                        }
                    }
                    for (OrgUnit org : deptList) {
                        if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                            List<OrgUnit> orgList = orgUnitManager
                                .getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                            for (OrgUnit orgUnit : orgList) {
                                Map<String, Object> map = new HashMap<>(16);
                                map.put("id", orgUnit.getId());
                                map.put("parentId", id);
                                map.put("name", orgUnit.getName());
                                map.put("isParent", orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT) ? true : false);
                                map.put("orgType", orgUnit.getOrgType());
                                map.put("principalType",
                                    orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                        ? ItemPermissionEnum.DEPARTMENT.getValue()
                                        : ItemPermissionEnum.POSITION.getValue());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    map.put("person", "6:" + orgUnit.getId());
                                }
                                if (item.contains(map)) {
                                    continue;// 去重
                                }
                                item.add(map);
                            }
                        } else {
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("id", org.getId());
                            map.put("parentId", id);
                            map.put("name", org.getName());
                            map.put("isParent", true);
                            map.put("orgType",
                                org.getOrgType() == null ? OrgTypeEnum.ORGANIZATION.getEnName() : org.getOrgType());
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                } else {
                    // 取部门下的部门或人员
                    List<OrgUnit> orgList =
                        orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        Map<String, Object> map = new HashMap<>(16);
                        String orgunitId = orgunit.getId();
                        map.put("id", orgunitId);
                        map.put("parentId", id);
                        map.put("name", orgunit.getName());
                        map.put("orgType", orgunit.getOrgType());
                        map.put("isParent", orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            map.put("person", "6:" + orgunit.getId());
                            map.put("principalType", ItemPermissionEnum.POSITION.getValue());
                        }
                        if (item.contains(map)) {
                            // 去重
                            continue;
                        }
                        item.add(map);
                    }
                }
            } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
                // 岗位
                List<OrgUnit> orgList = new ArrayList<>();
                for (ItemPermission o : list) {
                    if (o.getRoleType() == 1) {
                        orgList.addAll(
                            roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                    }
                    if (o.getRoleType() == 6) {
                        orgList.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    if (o.getRoleType() == 4) {
                        List<OrgUnit> orgUnitList =
                            dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                orgList.add(orgUnit);
                            }
                        }
                    }
                }
                for (OrgUnit orgUnit : orgList) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", orgUnit.getId());
                    map.put("parentId", orgUnit.getParentId());
                    map.put("person", "6:" + orgUnit.getId());
                    map.put("name", orgUnit.getName());
                    map.put("isParent", false);
                    map.put("orgType", orgUnit.getOrgType());
                    map.put("principalType", ItemPermissionEnum.POSITION.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    item.add(map);
                }
            } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
                List<CustomGroup> customGrouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
                if (StringUtils.isBlank(id)) {
                    for (CustomGroup customGroup : customGrouplist) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", customGroup.getId());
                        map.put("parentId", "");
                        map.put("name", customGroup.getGroupName());
                        map.put("isParent", true);
                        map.put("orgType", "customGroup");
                        map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getPersonId(), id, OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Position position =
                                positionManager.getPosition(tenantId, customGroupMember.getMemberId()).getData();
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", customGroupMember.getMemberId());
                            map.put("parentId", id);
                            map.put("name", position.getName());
                            map.put("isParent", false);
                            map.put("orgType", position.getOrgType());
                            map.put("person", "6:" + position.getId());
                            map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<OrgUnit> findPermUser4SUbmitTo(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgList = new ArrayList<>();
        try {
            List<OrgUnit> orgListTemp = new ArrayList<>();
            List<ItemPermission> list = itemPermissionService
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            for (ItemPermission o : list) {
                /**
                 * 1暂时只获取角色中的岗位
                 */
                if (o.getRoleType() == 1) {
                    orgListTemp
                        .addAll(roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                }
                /**
                 * 2暂时只获取角色中的岗位
                 */
                /*if (o.getRoleType() == 2) {
                    orgListTemp.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                }*/
                /**
                 * 4暂时只解析动态角色里面的岗位
                 */
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList =
                        dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            orgListTemp.add(orgUnit);
                        }
                    }
                }
                /**
                 * 6岗位
                 */
                if (o.getRoleType() == 6) {
                    orgListTemp.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                }
            }
            for (OrgUnit orgUnit : orgListTemp) {
                if (!orgList.contains(orgUnit)) {
                    orgList.add(orgUnit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orgList;
    }

    @Override
    public List<Map<String, Object>> findPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> list = itemPermissionService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId,
            processDefinitionId, taskDefKey);
        List<Map<String, Object>> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            List<OrgUnit> deptList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    deptList.addAll(
                        roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                    deptList.addAll(
                        roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList =
                        dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                            || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                            deptList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 2) {
                    Department dept = departmentManager.getDepartment(tenantId, o.getRoleId()).getData();
                    if (dept != null) {
                        deptList.add(dept);
                    }
                }
            }
            for (OrgUnit org : deptList) {
                if (OrgTypeEnum.DEPARTMENT.equals(org.getOrgType())) {
                    List<OrgUnit> orgUnitList = new ArrayList<>();
                    for (OrgUnit orgUnitTemp : deptList) {
                        orgUnitList.addAll(orgUnitManager
                            .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, orgUnitTemp.getDn())
                            .getData());
                    }
                    for (OrgUnit orgUnitTemp : orgUnitList) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", orgUnitTemp.getId());
                        map.put("name", orgUnitTemp.getName());
                        map.put("orgType", orgUnitTemp.getOrgType());
                        map.put("parentId", orgUnitTemp.getParentId());
                        if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                            map.put("isParent", true);
                            if (!item.contains(map)) {
                                item.add(map);
                            }
                        } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            map.put("person", "6:" + orgUnitTemp.getId());
                            map.put("isParent", false);
                            if (!item.contains(map)) {
                                item.add(map);
                            }
                        }
                    }
                } else if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                    // 租户组织机构树查询，会查询多个组织机构
                    List<OrgUnit> orgUnitList =
                        orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION).getData();
                    for (int i = 0; i < orgUnitList.size(); i++) {
                        if (OrgTypeEnum.ORGANIZATION.equals(orgUnitList.get(i).getOrgType())) {
                            continue;// 不显示组织机构
                        }
                        // 一个租户下有多个组织架构，如果不是在当前组织机构下的都排除
                        if (!orgUnitList.get(i).getGuidPath().contains(org.getId())) {
                            continue;
                        }
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", orgUnitList.get(i).getId());
                        map.put("name", orgUnitList.get(i).getName());
                        map.put("orgType", orgUnitList.get(i).getOrgType());
                        map.put("parentId", orgUnitList.get(i).getParentId());
                        if (OrgTypeEnum.POSITION.equals(orgUnitList.get(i).getOrgType())) {
                            map.put("person", "6:" + orgUnitList.get(i).getId());
                        }
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                }
            }
        } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
            List<OrgUnit> orgList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    orgList
                        .addAll(roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList =
                        dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            orgList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 6) {
                    OrgUnit per = orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData();
                    orgList.add(per);
                }
            }
            for (OrgUnit orgUnit : orgList) {
                if (orgUnit.getName().contains(name)) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", orgUnit.getId());
                    map.put("name", orgUnit.getName());
                    map.put("orgType", orgUnit.getOrgType());
                    map.put("parentId", orgUnit.getParentId());
                    if (OrgTypeEnum.POSITION.equals(orgUnit.getOrgType())) {
                        map.put("person", "6:" + orgUnit.getId());
                    }
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    item.add(map);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
                for (CustomGroup customGroup : grouplist) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", customGroup.getId());
                    map.put("parentId", "");
                    map.put("name", customGroup.getGroupName());
                    map.put("isParent", true);
                    map.put("orgType", "customGroup");
                    map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getPersonId(), customGroup.getId(), OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Position position =
                                positionManager.getPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (position != null && position.getName().contains(name)) {
                                Map<String, Object> map0 = new HashMap<>();
                                map0.put("id", customGroupMember.getMemberId());
                                map0.put("parentId", customGroup.getId());
                                map0.put("isParent", false);
                                map0.put("orgType", position.getOrgType());
                                map0.put("person", "6:" + position.getId() + ":" + position.getParentId());
                                map0.put("principalType", ItemPermissionEnum.POSITION.getValue());
                                map0.put("name", position.getName());
                                if (item.contains(map0)) {
                                    continue;// 去重
                                }
                                item.add(map0);
                                b = true;
                            }
                        }
                        if (b && !item.contains(map)) {
                            item.add(map);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    @Override
    public List<Map<String, Object>> findPermUserSendReceive(String id) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isBlank(id)) {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findAll();
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department =
                        departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", receiveDepartment.getDeptId());
                    map.put("parentId", receiveDepartment.getParentId());
                    map.put("name", department.getName());
                    map.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    map.put("isParent", count > 0);
                    map.put("orgType", OrgTypeEnum.DEPARTMENT.getEnName());
                    map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                    item.add(map);
                }
            } else {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findByParentIdOrderByTabIndex(id);
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department =
                        departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", receiveDepartment.getDeptId());
                    map.put("parentId", receiveDepartment.getParentId());
                    map.put("name", department.getName());
                    map.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    map.put("isParent", count > 0);
                    map.put("orgType", OrgTypeEnum.DEPARTMENT.getEnName());
                    map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                    item.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
