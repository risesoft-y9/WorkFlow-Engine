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
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.receive.ReceiveDepartment;
import net.risesoft.enums.DynamicRoleKindsEnum;
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

    private final DepartmentApi departmentApi;

    private final PositionRoleApi positionRoleApi;

    private final OrgUnitApi orgUnitApi;

    private final ReceiveDepartmentRepository receiveDepartmentRepository;

    private final CustomGroupApi customGroupApi;

    private final DynamicRoleService dynamicRoleService;

    @Override
    public List<ItemRoleOrgUnitModel> findByRoleId(String roleId, Integer principalType, String id) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Position> orgList = positionRoleApi.listPositionsByRoleId(tenantId, roleId).getData();
            for (Position position : orgList) {
                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                model.setId(position.getId());
                model.setParentId(position.getParentId());
                model.setName(position.getName());
                model.setIsParent(false);
                model.setOrgType(position.getOrgType().getValue());
                model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                model.setPerson("6:" + position.getId());
                model.setOrderedPath(position.getOrderedPath());
                model.setGuidPath(position.getGuidPath());
                if (itemList.contains(model)) {
                    continue;// 去重
                }
                itemList.add(model);
            }
            // 排序
            itemList = itemList.stream().sorted().collect(Collectors.toList());
            // 获取父级节点,有当前parentId的节点，不再调用getParent
            List<String> parentIdList = new ArrayList<>();
            for (ItemRoleOrgUnitModel model : itemList) {
                allItemList.add(model);
                if (parentIdList.contains(model.getParentId())) {
                    continue;
                }
                allItemList = getParent(allItemList, model);
                parentIdList.add(model.getParentId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allItemList;
    }

    public List<ItemRoleOrgUnitModel> getParent(List<ItemRoleOrgUnitModel> itemList, ItemRoleOrgUnitModel model) {
        OrgUnit parent = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), model.getParentId()).getData();
        if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
            Department department = (Department)parent;
            ItemRoleOrgUnitModel parentModel = new ItemRoleOrgUnitModel();
            parentModel.setId(parent.getId());
            parentModel.setParentId(parent.getParentId());
            parentModel.setName(
                StringUtils.isNotBlank(department.getAliasName()) ? department.getAliasName() : department.getName());
            parentModel.setIsParent(true);
            parentModel.setOrgType(parent.getOrgType().getValue());
            parentModel.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
            parentModel.setGuidPath(department.getGuidPath());
            if (!itemList.contains(parentModel)) {
                itemList.add(parentModel);
            }
            getParent(itemList, parentModel);
        }
        return itemList;
    }

    @Override
    public List<ItemRoleOrgUnitModel> listAllPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId, String taskId) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> list = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            boolean p = ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType);
            boolean d = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);
            if (StringUtils.isBlank(id)) {
                // 部门集合
                List<OrgUnit> deptList = new ArrayList<>();
                // 岗位集合
                List<Position> positionList = new ArrayList<>();
                for (ItemPermission o : list) {
                    // 授权部门,加入部门集合
                    if (o.getRoleType().equals(ItemPermissionEnum.DEPARTMENT.getValue()) && d) {
                        deptList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    // 授权角色,获取角色下所有岗位加入岗位集合
                    if (o.getRoleType().equals(ItemPermissionEnum.ROLE.getValue()) && p) {
                        positionList.addAll(positionRoleApi.listPositionsByRoleId(tenantId, o.getRoleId()).getData());
                    }
                    // 授权岗位,加入岗位集合
                    if (o.getRoleType().equals(ItemPermissionEnum.POSITION.getValue()) && p) {
                        positionList.add((Position)orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    // 授权动态角色,根据动态角色种类区分
                    if (o.getRoleType().equals(ItemPermissionEnum.DYNAMICROLE.getValue())) {
                        DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                        List<Position> pList = new ArrayList<>();
                        if (dynamicRole.getClassPath().contains("4SubProcess")) {// 针对岗位,加入岗位集合
                            pList = dynamicRoleMemberService.listByDynamicRoleIdAndTaskId(dynamicRole, taskId);
                        } else {
                            if (null == dynamicRole.getKinds()
                                || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
                                // 动态角色种类为【无】或null时，针对岗位或部门
                                List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                                    .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                                for (OrgUnit orgUnit : orgUnitList1) {
                                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                        pList.add((Position)orgUnit);
                                    } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                        || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                        deptList.add(orgUnit);
                                    }
                                }
                            } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                                pList = dynamicRoleMemberService
                                    .listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                            }
                        }
                        for (Position position : pList) {
                            positionList.add(position);
                        }
                    }
                }
                // 遍历岗位集合
                for (Position position : positionList) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(position.getId());
                    model.setParentId(position.getParentId());
                    model.setName(position.getName());
                    model.setIsParent(false);
                    model.setOrgType(position.getOrgType().getValue());
                    model.setOrderedPath(position.getOrderedPath());
                    model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                    model.setPerson("6:" + position.getId());
                    model.setGuidPath(position.getGuidPath());
                    if (itemList.contains(model)) {
                        continue;// 去重
                    }
                    itemList.add(model);
                }
                // 岗位排序
                itemList = itemList.stream().sorted().collect(Collectors.toList());
                // 获取父级节点,有当前parentId的节点，不再调用getParent
                List<String> parentIdList = new ArrayList<>();
                for (ItemRoleOrgUnitModel model : itemList) {
                    allItemList.add(model);
                    if (parentIdList.contains(model.getParentId())) {
                        continue;
                    }
                    allItemList = getParent(allItemList, model);
                    parentIdList.add(model.getParentId());
                }

                // 添加授权的部门
                for (OrgUnit org : deptList) {
                    if (org.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                        List<OrgUnit> orgList =
                            orgUnitApi.getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                        for (OrgUnit orgUnit : orgList) {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(orgUnit.getId());
                            model.setParentId(org.getId());
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                                model.setName(StringUtils.isNotBlank(((Department)orgUnit).getAliasName())
                                    ? ((Department)orgUnit).getAliasName() : orgUnit.getName());
                            } else {
                                model.setName(orgUnit.getName());
                            }
                            model.setGuidPath(orgUnit.getGuidPath());
                            model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                            model.setOrgType(orgUnit.getOrgType().getValue());
                            model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                ? ItemPermissionEnum.DEPARTMENT.getValue() : ItemPermissionEnum.POSITION.getValue());
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                model.setPerson("6:" + orgUnit.getId());

                            }
                            if (allItemList.contains(model)) {
                                continue;// 去重
                            }
                            allItemList.add(model);
                        }
                    } else {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(org.getId());
                        model.setParentId(org.getParentId());
                        model.setName(StringUtils.isNotBlank(((Department)org).getAliasName())
                            ? ((Department)org).getAliasName() : org.getName());
                        model.setIsParent(true);
                        model.setGuidPath(org.getGuidPath());
                        model.setOrgType(org.getOrgType().getValue());
                        model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        if (allItemList.contains(model)) {
                            continue;// 去重
                        }
                        allItemList.add(model);
                    }
                }
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
                    model.setGuidPath(orgunit.getGuidPath());
                    if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                        model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        model.setName(StringUtils.isNotBlank(((Department)orgunit).getAliasName())
                            ? ((Department)orgunit).getAliasName() : orgunit.getName());
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
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> list = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            boolean p = ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType);
            boolean d = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);
            if (p || d) {
                if (StringUtils.isBlank(id)) {
                    // 部门集合
                    List<OrgUnit> deptList = new ArrayList<>();
                    // 岗位集合
                    List<Position> positionList = new ArrayList<>();
                    for (ItemPermission o : list) {
                        // 授权部门,加入部门集合
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DEPARTMENT.getValue()) && d) {
                            deptList.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        // 授权角色,获取角色下所有岗位加入岗位集合
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.ROLE.getValue()) && p) {
                            positionList
                                .addAll(positionRoleApi.listPositionsByRoleId(tenantId, o.getRoleId()).getData());
                        }
                        // 授权岗位,加入岗位集合
                        if (o.getRoleType().equals(ItemPermissionEnum.POSITION.getValue()) && p) {
                            positionList.add((Position)orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        // 授权动态角色,根据动态角色种类区分
                        if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DYNAMICROLE.getValue())) {
                            DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                            List<Position> pList = new ArrayList<>();
                            if (null == dynamicRole.getKinds()
                                || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
                                // 动态角色种类为【无】或null时，针对岗位或部门
                                List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                                    .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                                for (OrgUnit orgUnit : orgUnitList1) {
                                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                        pList.add((Position)orgUnit);
                                    } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                        || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                        deptList.add(orgUnit);
                                    }
                                }
                            } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                                pList = dynamicRoleMemberService
                                    .listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                            }
                            for (Position position : pList) {
                                positionList.add(position);
                            }
                        }
                    }
                    // 遍历岗位集合
                    for (Position position : positionList) {
                        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                        model.setId(position.getId());
                        model.setParentId(position.getParentId());
                        model.setName(position.getName());
                        model.setIsParent(false);
                        model.setOrgType(position.getOrgType().getValue());
                        model.setOrderedPath(position.getOrderedPath());
                        model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                        model.setPerson("6:" + position.getId());
                        model.setGuidPath(position.getGuidPath());
                        if (itemList.contains(model)) {
                            continue;// 去重
                        }
                        itemList.add(model);
                    }
                    // 岗位排序
                    itemList = itemList.stream().sorted().collect(Collectors.toList());
                    // 获取父级节点,有当前parentId的节点，不再调用getParent
                    List<String> parentIdList = new ArrayList<>();
                    for (ItemRoleOrgUnitModel model : itemList) {
                        allItemList.add(model);
                        if (parentIdList.contains(model.getParentId())) {
                            continue;
                        }
                        allItemList = getParent(allItemList, model);
                        parentIdList.add(model.getParentId());
                    }

                    // 添加授权的部门
                    for (OrgUnit org : deptList) {
                        if (org.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                            List<OrgUnit> orgList = orgUnitApi
                                .getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
                            for (OrgUnit orgUnit : orgList) {
                                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                                model.setId(orgUnit.getId());
                                model.setParentId(org.getId());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                                    model.setName(StringUtils.isNotBlank(((Department)orgUnit).getAliasName())
                                        ? ((Department)orgUnit).getAliasName() : orgUnit.getName());
                                } else {
                                    model.setName(orgUnit.getName());
                                }
                                model.setGuidPath(orgUnit.getGuidPath());
                                model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
                                model.setOrgType(orgUnit.getOrgType().getValue());
                                model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                    ? ItemPermissionEnum.DEPARTMENT.getValue()
                                    : ItemPermissionEnum.POSITION.getValue());
                                if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                    model.setPerson("6:" + orgUnit.getId());
                                }
                                if (allItemList.contains(model)) {
                                    continue;// 去重
                                }
                                allItemList.add(model);
                            }
                        } else {
                            ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                            model.setId(org.getId());
                            model.setParentId(org.getParentId());
                            model.setName(StringUtils.isNotBlank(((Department)org).getAliasName())
                                ? ((Department)org).getAliasName() : org.getName());
                            model.setIsParent(true);
                            model.setOrgType(org.getOrgType().getValue());
                            model.setGuidPath(org.getGuidPath());
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                            if (allItemList.contains(model)) {
                                continue;// 去重
                            }
                            allItemList.add(model);
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
                        model.setGuidPath(orgunit.getGuidPath());
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                            model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                            model.setPerson("6:" + orgunit.getId());
                        }
                        if (allItemList.contains(model)) {
                            // 去重
                            continue;
                        }
                        allItemList.add(model);
                    }
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
                        if (allItemList.contains(model)) {
                            continue;// 去重
                        }
                        allItemList.add(model);
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
                                model.setGuidPath(user.getGuidPath());
                                model.setPrincipalType(ItemPermissionEnum.CUSTOMGROUP.getValue());
                                if (allItemList.contains(model)) {
                                    continue;// 去重
                                }
                                allItemList.add(model);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allItemList;
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
                 * 1获取角色中的岗位
                 */
                if (o.getRoleType() == 1) {
                    orgListTemp.addAll(positionRoleApi.listPositionsByRoleId(tenantId, o.getRoleId()).getData());
                }
                /*
                 * 2获取部门的岗位
                 */
                // if (o.getRoleType() == 2) {
                // orgListTemp.add(orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                // }
                /*
                 * 4暂时只解析动态角色里面的岗位
                 */
                if (o.getRoleType() == 4) {
                    DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                    List<Position> pList = new ArrayList<>();
                    if (null == dynamicRole.getKinds()
                        || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
                        // 动态角色种类为【无】或null时，针对岗位或部门
                        List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList1) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                pList.add((Position)orgUnit);
                            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                                // deptList.add(orgUnit);
                            }
                        }
                    } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                        pList = dynamicRoleMemberService.listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole,
                            processInstanceId);
                    }
                    for (OrgUnit orgUnit : pList) {
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
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();
        boolean p = ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType);
        boolean d = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);
        if (p || d) {
            // 部门集合
            List<OrgUnit> deptList = new ArrayList<>();
            // 岗位集合
            List<Position> positionList = new ArrayList<>();
            for (ItemPermission o : list) {
                // 授权角色,获取角色下所有岗位加入岗位集合
                if (o.getRoleType() == 1 && p) {
                    positionList.addAll(positionRoleApi.listPositionsByRoleId(tenantId, o.getRoleId()).getData());
                }
                // 授权岗位,加入岗位集合
                if (o.getRoleType().equals(ItemPermissionEnum.POSITION.getValue()) && p) {
                    positionList.add((Position)orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                }
                // 授权部门,加入部门集合
                if (o.getRoleType() == 2 && d) {
                    Department dept = departmentApi.get(tenantId, o.getRoleId()).getData();
                    if (dept != null) {
                        deptList.add(dept);
                    }
                }
                // 授权动态角色,根据动态角色种类区分
                if (o.getRoleType() == 4) {
                    DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                    List<Position> pList = new ArrayList<>();
                    if (null == dynamicRole.getKinds()
                        || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
                        // 动态角色种类为【无】或null时，针对岗位或部门
                        List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList1) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                pList.add((Position)orgUnit);
                            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                deptList.add(orgUnit);
                            }
                        }
                    } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                        pList = dynamicRoleMemberService.listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole,
                            processInstanceId);
                    }
                    for (Position position : pList) {
                        positionList.add(position);
                    }
                }
            }

            // 遍历岗位集合
            for (Position position : positionList) {
                ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                model.setId(position.getId());
                model.setParentId(position.getParentId());
                model.setName(position.getName());
                model.setIsParent(false);
                model.setOrgType(position.getOrgType().getValue());
                model.setOrderedPath(position.getOrderedPath());
                model.setPrincipalType(ItemPermissionEnum.POSITION.getValue());
                model.setPerson("6:" + position.getId());
                model.setGuidPath(position.getGuidPath());
                if (!position.getName().contains(name)) {
                    continue;
                }
                if (itemList.contains(model)) {
                    continue;// 去重
                }
                itemList.add(model);
            }
            // 岗位排序
            itemList = itemList.stream().sorted().collect(Collectors.toList());
            // 获取父级节点,有当前parentId的节点，不再调用getParent
            List<String> parentIdList = new ArrayList<>();
            for (ItemRoleOrgUnitModel model : itemList) {
                allItemList.add(model);
                if (parentIdList.contains(model.getParentId())) {
                    continue;
                }
                allItemList = getParent(allItemList, model);
                parentIdList.add(model.getParentId());
            }

            for (OrgUnit org : deptList) {
                List<OrgUnit> orgUnitList = new ArrayList<>();
                orgUnitList.addAll(orgUnitApi
                    .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, org.getDn()).getData());
                for (OrgUnit orgUnitTemp : orgUnitList) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(orgUnitTemp.getId());
                    model.setName(orgUnitTemp.getName());
                    model.setOrgType(orgUnitTemp.getOrgType().getValue());
                    model.setParentId(orgUnitTemp.getParentId());
                    model.setGuidPath(orgUnitTemp.getGuidPath());
                    if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                        model.setIsParent(true);
                        if (!allItemList.contains(model)) {
                            allItemList.add(model);
                        }
                    } else if (orgUnitTemp.getOrgType().equals(OrgTypeEnum.POSITION)) {
                        model.setIsParent(false);
                        model.setPerson("6:" + orgUnitTemp.getId());
                        if (!allItemList.contains(model)) {
                            allItemList.add(model);
                        }
                    }
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
                    if (allItemList.contains(model)) {
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
                                model.setGuidPath(user.getGuidPath());
                                if (allItemList.contains(model1)) {
                                    continue;// 去重
                                }
                                allItemList.add(model1);
                                b = true;
                            }
                        }
                        if (b && !allItemList.contains(model)) {
                            allItemList.add(model);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allItemList;
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
