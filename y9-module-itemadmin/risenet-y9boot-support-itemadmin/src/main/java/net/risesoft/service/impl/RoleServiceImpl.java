package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Position;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.RoleService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.y9.Y9LoginUserHolder;

/*
 * @author qinman
 *
 * @author zhangchongjie
 *
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final ItemPermissionService itemPermissionService;

    private final DynamicRoleMemberService dynamicRoleMemberService;

    private final RoleApi roleApi;

    private final DepartmentApi departmentApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final ReceiveDepartmentRepository receiveDepartmentRepository;

    private final CustomGroupApi customGroupApi;

    private final DynamicRoleService dynamicRoleService;

    public List<ItemRoleOrgUnitModel> getParent(List<ItemRoleOrgUnitModel> itemList, ItemRoleOrgUnitModel model) {
        OrgUnit parent = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), model.getParentId()).getData();
        if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
            ItemRoleOrgUnitModel parentModel = new ItemRoleOrgUnitModel();
            parentModel.setId(parent.getId());
            parentModel.setParentId(parent.getParentId());
            parentModel.setName(parent.getName());
            parentModel.setIsParent(true);
            parentModel.setOrgType(parent.getOrgType().getValue());
            parentModel.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
            if (!itemList.contains(parentModel)) {
                itemList.add(parentModel);
            }
            getParent(itemList, parentModel);
        }
        return itemList;
    }

    @Override
    public List<ItemRoleOrgUnitModel> listAllPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> list = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            boolean isSort = true;
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                if (StringUtils.isBlank(id)) {
                    List<OrgUnit> deptList = new ArrayList<>();
                    for (ItemPermission o : list) {
                        if (o.getRoleType() == 1) {
                            deptList.addAll(
                                roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                            deptList.addAll(
                                roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                        }
                        if (o.getRoleType() == 2) {
                            deptList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        if (o.getRoleType() == 4) {
                            List<OrgUnit> orgUnitList = dynamicRoleMemberService
                                .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                            DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                            String classFullPath = dynamicRole.getClassPath();
                            if (classFullPath.contains("SignDeptSecretary")) {// 会签部门，获取部门秘书后，不需要再获取上级部门和排序
                                isSort = false;
                            }
                            for (OrgUnit orgUnit : orgUnitList) {
                                // if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                // || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                deptList.add(orgUnit);
                                // }
                            }
                        }
                    }
                    for (OrgUnit org : deptList) {
                        if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                            List<OrgUnit> orgList = orgUnitApi
                                .getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                            for (OrgUnit orgUnit : orgList) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(orgUnit.getId());
                                model.setParentId(org.getId());
                                model.setName(orgUnit.getName());
                                model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                                model.setOrgType(orgUnit.getOrgType().getValue());
                                model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                    ? ItemPermissionEnum.DEPARTMENT.getValue()
                                    : ItemPermissionEnum.POSITION.getValue());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    model.setPerson("6:" + orgUnit.getId());
                                }
                                if (itemList.contains(model)) {
                                    continue;// 去重
                                }
                                itemList.add(model);
                            }
                        } else if (OrgTypeEnum.DEPARTMENT.equals(org.getOrgType())) {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(org.getParentId());
                            model.setName(org.getName());
                            model.setIsParent(true);
                            model.setOrgType(org.getOrgType().getValue());
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                            if (itemList.contains(model)) {
                                continue;// 去重
                            }
                            itemList.add(model);
                        } else if (OrgTypeEnum.POSITION.equals(org.getOrgType())) {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(org.getParentId());
                            model.setName(org.getName());
                            model.setIsParent(false);
                            model.setOrgType(org.getOrgType().getValue());
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + org.getId());
                            if (itemList.contains(model)) {
                                continue;// 去重
                            }
                            itemList.add(model);
                        }
                    }
                    allItemList.addAll(itemList);
                } else {
                    // 取部门下的部门或人员
                    List<OrgUnit> orgList =
                        orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        String orgunitId = orgunit.getId();
                        model.setId(orgunitId);
                        model.setParentId(id);
                        model.setName(orgunit.getName());
                        model.setIsParent(orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        model.setOrgType(orgunit.getOrgType().getValue());
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + orgunit.getId());
                        }
                        if (itemList.contains(model)) {
                            // 去重
                            continue;
                        }
                        itemList.add(model);
                    }
                    allItemList.addAll(itemList);
                }
            } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
                // 岗位
                List<OrgUnit> orgList = new ArrayList<>();
                for (ItemPermission o : list) {
                    if (o.getRoleType() == 1) {
                        orgList
                            .addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                    }
                    if (o.getRoleType() == 6) {
                        orgList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    if (o.getRoleType() == 4) {
                        List<OrgUnit> orgUnitList = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                orgList.add(orgUnit);
                            }
                        }
                    }
                }
                for (OrgUnit orgUnit : orgList) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    Position position = positionApi.get(tenantId, orgUnit.getId()).getData();
                    if (position != null && !position.getDisabled()) {
                        model.setId(orgUnit.getId());
                        model.setParentId(orgUnit.getParentId());
                        model.setName(orgUnit.getName());
                        model.setIsParent(false);
                        model.setOrgType(orgUnit.getOrgType().getValue());
                        model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                        model.setPerson("6:" + orgUnit.getId());
                        model.setOrderedPath(position.getOrderedPath());
                        if (itemList.contains(model)) {
                            continue;// 去重
                        }
                        itemList.add(model);
                    }
                }
                if (isSort) {
                    // 排序
                    itemList = itemList.stream().sorted().collect(Collectors.toList());
                    for (ItemRoleOrgUnitModel model : itemList) {
                        allItemList.add(model);
                        allItemList = getParent(allItemList, model);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allItemList;
    }

    @Override
    public List<ItemRoleOrgUnitModel> findByRoleId(String roleId, Integer principalType, String id) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                if (StringUtils.isBlank(id)) {
                    List<OrgUnit> deptList = new ArrayList<>();
                    deptList.addAll(roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.DEPARTMENT).getData());
                    deptList.addAll(roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.ORGANIZATION).getData());
                    for (OrgUnit org : deptList) {
                        if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                            List<OrgUnit> orgList = orgUnitApi
                                .getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                            for (OrgUnit orgUnit : orgList) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(orgUnit.getId());
                                model.setParentId(org.getId());
                                model.setName(orgUnit.getName());
                                model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                                model.setOrgType(orgUnit.getOrgType().getValue());
                                model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                    ? ItemPermissionEnum.DEPARTMENT.getValue()
                                    : ItemPermissionEnum.POSITION.getValue());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    model.setPerson("6:" + orgUnit.getId());
                                }
                                if (itemList.contains(model)) {
                                    continue;// 去重
                                }
                                itemList.add(model);
                            }
                        } else if (OrgTypeEnum.DEPARTMENT.equals(org.getOrgType())) {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(org.getParentId());
                            model.setName(org.getName());
                            model.setIsParent(true);
                            model.setOrgType(org.getOrgType().getValue());
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                            if (itemList.contains(model)) {
                                continue;// 去重
                            }
                            itemList.add(model);
                        } else if (OrgTypeEnum.POSITION.equals(org.getOrgType())) {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(org.getParentId());
                            model.setName(org.getName());
                            model.setIsParent(false);
                            model.setOrgType(org.getOrgType().getValue());
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + org.getId());
                            if (itemList.contains(model)) {
                                continue;// 去重
                            }
                            itemList.add(model);
                        }
                    }
                    allItemList.addAll(itemList);
                } else {
                    // 取部门下的部门或人员
                    List<OrgUnit> orgList =
                        orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        String orgunitId = orgunit.getId();
                        model.setId(orgunitId);
                        model.setParentId(id);
                        model.setName(orgunit.getName());
                        model.setIsParent(orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        model.setOrgType(orgunit.getOrgType().getValue());
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + orgunit.getId());
                        }
                        if (itemList.contains(model)) {
                            // 去重
                            continue;
                        }
                        itemList.add(model);
                    }
                    allItemList.addAll(itemList);
                }
            } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
                // 岗位
                List<OrgUnit> orgList =
                    new ArrayList<>(roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData());
                for (OrgUnit orgUnit : orgList) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    Position position = positionApi.get(tenantId, orgUnit.getId()).getData();
                    if (position != null && !position.getDisabled()) {
                        model.setId(orgUnit.getId());
                        model.setParentId(orgUnit.getParentId());
                        model.setName(orgUnit.getName());
                        model.setIsParent(false);
                        model.setOrgType(orgUnit.getOrgType().getValue());
                        model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                        model.setPerson("6:" + orgUnit.getId());
                        model.setOrderedPath(position.getOrderedPath());
                        if (itemList.contains(model)) {
                            continue;// 去重
                        }
                        itemList.add(model);
                    }
                }
                // 排序
                itemList = itemList.stream().sorted().collect(Collectors.toList());
                for (ItemRoleOrgUnitModel model : itemList) {
                    allItemList.add(model);
                    allItemList = getParent(allItemList, model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allItemList;
    }

    @Override
    public List<ItemRoleOrgUnitModel> listCsUser(String id, Integer principalType, String processInstanceId) {
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            if (StringUtils.isBlank(id) || UtilConsts.NULL.equals(id)) {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    Organization organization = orgUnitApi.getOrganization(tenantId, userId).getData();
                    List<OrgUnit> orgUnitList = orgUnitApi
                        .getSubTree(tenantId, organization.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgUnit : orgUnitList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(orgUnit.getId());
                        model.setParentId(id);
                        model.setName(orgUnit.getName());
                        model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        model.setOrgType(orgUnit.getOrgType().getValue());
                        model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                            ? ItemPermissionEnum.DEPARTMENT.getValue() : ItemPermissionEnum.POSITION.getValue());
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            model.setPerson("6:" + orgUnit.getId());
                        }
                        item.add(model);
                    }
                } else {
                    List<CustomGroup> grouplist = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                    for (CustomGroup customGroup : grouplist) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(customGroup.getId());
                        model.setParentId("");
                        model.setName(customGroup.getGroupName());
                        model.setIsParent(true);
                        model.setOrgType("customGroup");
                        model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(model)) {
                            continue;// 去重
                        }
                        item.add(model);
                    }
                }
            } else {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    List<OrgUnit> orgList =
                        orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        String orgunitId = orgunit.getId();
                        model.setId(orgunitId);
                        model.setParentId(id);
                        model.setName(orgunit.getName());
                        model.setOrgType(orgunit.getOrgType().getValue());
                        model.setIsParent(orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + orgunit.getId());
                        }
                        if (item.contains(model)) {
                            continue;// 去重
                        }
                        item.add(model);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, id, OrgTypeEnum.POSITION)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            OrgUnit user = orgUnitApi
                                .getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (user != null && !user.getDisabled()) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(customGroupMember.getMemberId());
                                model.setParentId(id);
                                model.setName(user.getName());
                                model.setIsParent(false);
                                model.setOrgType(user.getOrgType().getValue());
                                model.setPerson("6:" + user.getId() + ":" + user.getParentId());
                                model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                                if (item.contains(model)) {
                                    continue;// 去重
                                }
                                item.add(model);
                            }
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
    public List<ItemRoleOrgUnitModel> listCsUser4Bureau(String id) {
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isBlank(id) || UtilConsts.NULL.equals(id)) {
                id = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getOrgUnit().getId()).getData().getId();
            }
            List<OrgUnit> orgUnitList =
                orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
            String finalId = id;
            orgUnitList.forEach(orgUnit -> {
                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                model.setId(orgUnit.getId());
                model.setParentId(finalId);
                model.setName(orgUnit.getName());
                model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                model.setOrgType(orgUnit.getOrgType().getValue());
                model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                    ? ItemPermissionEnum.DEPARTMENT.getValue() : ItemPermissionEnum.POSITION.getValue());
                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                    model.setPerson("6:" + orgUnit.getId());
                }
                item.add(model);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /*
     * Description:
     *
     * @param name
     * @param principalType
     * @param processInstanceId
     * @return
     */
    @Override
    public List<ItemRoleOrgUnitModel> listCsUserSearch(String name, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            Organization organization = orgUnitApi.getOrganization(tenantId, userId).getData();
            List<Department> deptList = departmentApi.listByParentId(tenantId, organization.getId()).getData();
            List<OrgUnit> orgUnitListTemp = new ArrayList<>();
            for (OrgUnit orgUnitTemp : deptList) {
                orgUnitListTemp.addAll(orgUnitApi
                    .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, orgUnitTemp.getDn())
                    .getData());
            }
            for (OrgUnit orgUnitTemp : orgUnitListTemp) {
                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                model.setId(orgUnitTemp.getId());
                model.setParentId(orgUnitTemp.getParentId());
                model.setName(orgUnitTemp.getName());
                model.setOrgType(orgUnitTemp.getOrgType().getValue());
                if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                    model.setIsParent(true);
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                    model.setIsParent(true);
                    continue;
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.POSITION)) {
                    model.setPerson("6:" + orgUnitTemp.getId());
                    model.setIsParent(true);
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.PERSON)) {
                    continue;
                } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.GROUP)) {
                    continue;
                }
                boolean b = item.contains(model);
                if (!b) {
                    item.add(model);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                for (CustomGroup customGroup : grouplist) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(customGroup.getId());
                    model.setParentId("");
                    model.setName(customGroup.getGroupName());
                    model.setOrgType("customGroup");
                    model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                    model.setIsParent(true);
                    if (item.contains(model)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId,
                            customGroup.getId(), OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            OrgUnit user = orgUnitApi
                                .getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (user != null && user.getName().contains(name) && !user.getDisabled()) {
                                ItemRoleOrgUnitModel model0 = new ItemRoleOrgUnitModel();
                                model0.setIsParent(false);
                                model0.setPerson("6:" + user.getId() + ":" + user.getParentId());
                                model0.setId(customGroupMember.getMemberId());
                                model0.setOrgType(user.getOrgType().getValue());
                                model0.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                                model0.setName(user.getName());
                                model0.setParentId(customGroup.getId());
                                if (item.contains(model0)) {
                                    continue;// 去重
                                }
                                item.add(model0);
                                b = true;
                            }
                        }
                        if (b && !item.contains(model)) {
                            item.add(model);
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
    public List<ItemRoleOrgUnitModel> listPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId) {
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> list = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                if (StringUtils.isBlank(id)) {
                    List<OrgUnit> deptList = new ArrayList<>();
                    for (ItemPermission o : list) {
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.ROLE.getValue())) {
                            deptList.addAll(
                                roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                            deptList.addAll(
                                roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                        }
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DEPARTMENT.getValue())) {
                            deptList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DYNAMICROLE.getValue())) {
                            List<OrgUnit> orgUnitList = dynamicRoleMemberService
                                .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
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
                            List<OrgUnit> orgList = orgUnitApi
                                .getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                            for (OrgUnit orgUnit : orgList) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(orgUnit.getId());
                                model.setParentId(id);
                                model.setName(orgUnit.getName());
                                model.setIsParent(org.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                                model.setOrgType(orgUnit.getOrgType().getValue());
                                model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                    ? ItemPermissionEnum.DEPARTMENT.getValue()
                                    : ItemPermissionEnum.POSITION.getValue());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    model.setPerson("6:" + orgUnit.getId());
                                }
                                if (item.contains(model)) {
                                    continue;// 去重
                                }
                                item.add(model);
                            }
                        } else {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(id);
                            model.setName(org.getName());
                            model.setIsParent(true);
                            model.setOrgType(org.getOrgType() == null ? OrgTypeEnum.ORGANIZATION.getEnName()
                                : org.getOrgType().getValue());
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                            if (item.contains(model)) {
                                continue;// 去重
                            }
                            item.add(model);
                        }
                    }
                } else {
                    // 取部门下的部门或人员
                    List<OrgUnit> orgList =
                        orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                    for (OrgUnit orgunit : orgList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        String orgUnitId = orgunit.getId();
                        model.setId(orgUnitId);
                        model.setParentId(id);
                        model.setName(orgunit.getName());
                        model.setIsParent(orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                        model.setOrgType(orgunit.getOrgType().getValue());
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + orgunit.getId());
                        }
                        if (item.contains(model)) {
                            // 去重
                            continue;
                        }
                        item.add(model);
                    }
                }
            } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
                // 岗位
                List<OrgUnit> orgList = new ArrayList<>();
                for (ItemPermission o : list) {
                    if (Objects.equals(o.getRoleType(), ItemPermissionEnum.ROLE.getValue())) {
                        orgList
                            .addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                    }
                    if (Objects.equals(o.getRoleType(), ItemPermissionEnum.POSITION.getValue())) {
                        orgList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DYNAMICROLE.getValue())) {
                        List<OrgUnit> orgUnitList = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                orgList.add(orgUnit);
                            }
                        }
                    }
                }
                for (OrgUnit orgUnit : orgList) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(orgUnit.getId());
                    model.setParentId(orgUnit.getParentId());
                    model.setName(orgUnit.getName());
                    model.setIsParent(false);
                    model.setOrgType(orgUnit.getOrgType().getValue());
                    model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                    model.setPerson("6:" + orgUnit.getId());
                    if (item.contains(model)) {
                        continue;// 去重
                    }
                    item.add(model);
                }
            } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
                List<CustomGroup> customGrouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
                if (StringUtils.isBlank(id)) {
                    for (CustomGroup customGroup : customGrouplist) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(customGroup.getId());
                        model.setParentId("");
                        model.setName(customGroup.getGroupName());
                        model.setIsParent(true);
                        model.setOrgType("customGroup");
                        model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(model)) {
                            continue;// 去重
                        }
                        item.add(model);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getPersonId(), id, OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            OrgUnit user = orgUnitApi
                                .getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (user != null && !user.getDisabled()) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(customGroupMember.getMemberId());
                                model.setParentId(id);
                                model.setName(user.getName());
                                model.setIsParent(false);
                                model.setOrgType(user.getOrgType().getValue());
                                model.setPerson("6:" + user.getId());
                                model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                                if (item.contains(model)) {
                                    continue;// 去重
                                }
                                item.add(model);
                            }
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
    public List<OrgUnit> listPermUser4SUbmitTo(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgList = new ArrayList<>();
        try {
            List<OrgUnit> orgListTemp = new ArrayList<>();
            List<ItemPermission> list = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            for (ItemPermission o : list) {
                /*
                 * 1暂时只获取角色中的岗位
                 */
                if (o.getRoleType() == 1) {
                    orgListTemp
                        .addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                }
                /*
                 * 2暂时只获取角色中的岗位
                 */
                /*if (o.getRoleType() == 2) {
                    orgListTemp.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                }*/
                /*
                 * 4暂时只解析动态角色里面的岗位
                 */
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList = dynamicRoleMemberService
                        .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            orgListTemp.add(orgUnit);
                        }
                    }
                }
                /*
                 * 6岗位
                 */
                if (o.getRoleType() == 6) {
                    orgListTemp.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
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
    public List<ItemRoleOrgUnitModel> listPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> list = itemPermissionService.listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId,
            processDefinitionId, taskDefKey);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            List<OrgUnit> deptList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    deptList
                        .addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                    deptList
                        .addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList = dynamicRoleMemberService
                        .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                            || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                            deptList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 2) {
                    Department dept = departmentApi.get(tenantId, o.getRoleId()).getData();
                    if (dept != null) {
                        deptList.add(dept);
                    }
                }
            }
            for (OrgUnit org : deptList) {
                if (OrgTypeEnum.DEPARTMENT.equals(org.getOrgType())) {
                    List<OrgUnit> orgUnitList = new ArrayList<>();
                    for (OrgUnit orgUnitTemp : deptList) {
                        orgUnitList.addAll(orgUnitApi
                            .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, orgUnitTemp.getDn())
                            .getData());
                    }
                    for (OrgUnit orgUnitTemp : orgUnitList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(orgUnitTemp.getId());
                        model.setName(orgUnitTemp.getName());
                        model.setOrgType(orgUnitTemp.getOrgType().getValue());
                        model.setParentId(orgUnitTemp.getParentId());
                        if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                            model.setIsParent(true);
                            if (!item.contains(model)) {
                                item.add(model);
                            }
                        } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            model.setIsParent(false);
                            model.setPerson("6:" + orgUnitTemp.getId());
                            if (!item.contains(model)) {
                                item.add(model);
                            }
                        }
                    }
                } else if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                    // 租户组织机构树查询，会查询多个组织机构
                    List<OrgUnit> orgUnitList =
                        orgUnitApi.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION).getData();
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (OrgTypeEnum.ORGANIZATION.equals(orgUnit.getOrgType())) {
                            continue;// 不显示组织机构
                        }
                        // 一个租户下有多个组织架构，如果不是在当前组织机构下的都排除
                        if (!orgUnit.getGuidPath().contains(org.getId())) {
                            continue;
                        }
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(orgUnit.getId());
                        model.setName(orgUnit.getName());
                        model.setOrgType(orgUnit.getOrgType().getValue());
                        model.setParentId(orgUnit.getParentId());
                        if (OrgTypeEnum.POSITION.equals(orgUnit.getOrgType())) {
                            model.setPerson("6:" + orgUnit.getId());
                        }
                        if (item.contains(model)) {
                            continue;// 去重
                        }
                        item.add(model);
                    }
                }
            }
        } else if (ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType)) {
            List<OrgUnit> orgList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    orgList.addAll(roleApi.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList = dynamicRoleMemberService
                        .listByDynamicRoleIdAndProcessInstanceId(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                            orgList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 6) {
                    OrgUnit per = orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData();
                    orgList.add(per);
                }
            }
            for (OrgUnit orgUnit : orgList) {
                if (orgUnit.getName().contains(name)) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(orgUnit.getId());
                    model.setName(orgUnit.getName());
                    model.setOrgType(orgUnit.getOrgType().getValue());
                    model.setParentId(orgUnit.getParentId());
                    if (OrgTypeEnum.POSITION.equals(orgUnit.getOrgType())) {
                        model.setPerson("6:" + orgUnit.getId());
                    }
                    if (item.contains(model)) {
                        continue;// 去重
                    }
                    item.add(model);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
                for (CustomGroup customGroup : grouplist) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(customGroup.getId());
                    model.setName(customGroup.getGroupName());
                    model.setOrgType("customGroup");
                    model.setParentId("");
                    model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                    model.setIsParent(true);
                    if (item.contains(model)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList =
                        customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getPersonId(), customGroup.getId(), OrgTypeEnum.POSITION).getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            OrgUnit user = orgUnitApi
                                .getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();
                            if (user != null && user.getName().contains(name) && !user.getDisabled()) {
                                ItemRoleOrgUnitModel model1 = new ItemRoleOrgUnitModel();
                                model1.setId(customGroupMember.getMemberId());
                                model1.setName(user.getName());
                                model1.setOrgType(user.getOrgType().getValue());
                                model1.setParentId(customGroup.getId());
                                model1.setPerson("6:" + user.getId() + ":" + user.getParentId());
                                model1.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                                model1.setIsParent(false);
                                if (item.contains(model1)) {
                                    continue;// 去重
                                }
                                item.add(model1);
                                b = true;
                            }
                        }
                        if (b && !item.contains(model)) {
                            item.add(model);
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
    public List<ItemRoleOrgUnitModel> listPermUserSendReceive(String id) {
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isBlank(id)) {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findAll();
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(receiveDepartment.getDeptId());
                    model.setName(department.getName());
                    model.setOrgType(OrgTypeEnum.DEPARTMENT.getEnName());
                    model.setParentId(receiveDepartment.getParentId());
                    model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    model.setIsParent(count > 0);
                    item.add(model);
                }
            } else {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findByParentIdOrderByTabIndex(id);
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department = departmentApi.get(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(receiveDepartment.getDeptId());
                    model.setName(department.getName());
                    model.setOrgType(OrgTypeEnum.DEPARTMENT.getEnName());
                    model.setParentId(receiveDepartment.getParentId());
                    model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    model.setIsParent(count > 0);
                    item.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
