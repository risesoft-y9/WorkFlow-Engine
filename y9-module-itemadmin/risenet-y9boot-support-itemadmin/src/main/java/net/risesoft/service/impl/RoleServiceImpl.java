package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.CustomGroupApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.receive.ReceiveDepartment;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.platform.org.CustomGroup;
import net.risesoft.model.platform.org.CustomGroupMember;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Position;
import net.risesoft.repository.receive.ReceiveDepartmentRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.RoleService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
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
                model.setPrincipalType(ItemPermissionEnum.POSITION);
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
            parentModel.setPrincipalType(ItemPermissionEnum.DEPARTMENT);
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<ItemPermission> permissions = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);

            boolean isPositionType = ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType);
            boolean isDepartmentType = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);

            if (StringUtils.isBlank(id)) {
                return handleBlankIdCase(tenantId, permissions, isPositionType, isDepartmentType, processInstanceId,
                    taskId);
            } else {
                return handleNonBlankIdCase(tenantId, id);
            }
        } catch (Exception e) {
            LOGGER.error("获取权限用户列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 处理id为空的情况
     */
    private List<ItemRoleOrgUnitModel> handleBlankIdCase(String tenantId, List<ItemPermission> permissions,
        boolean isPositionType, boolean isDepartmentType, String processInstanceId, String taskId) {

        PermissionData permissionData =
            collectPermissionData(tenantId, permissions, isPositionType, isDepartmentType, processInstanceId, taskId);

        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        // 处理岗位数据
        if (!permissionData.positionList.isEmpty()) {
            allItemList.addAll(buildPositionTree(permissionData.positionList));
        }

        // 处理部门数据
        if (!permissionData.deptList.isEmpty()) {
            allItemList.addAll(buildDepartmentTree(tenantId, permissionData.deptList));
        }

        return allItemList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 收集权限数据
     */
    private PermissionData collectPermissionData(String tenantId, List<ItemPermission> permissions,
        boolean isPositionType, boolean isDepartmentType, String processInstanceId, String taskId) {

        PermissionData permissionData = new PermissionData();

        for (ItemPermission permission : permissions) {
            switch (permission.getRoleType()) {
                case DEPARTMENT:
                    if (isDepartmentType) {
                        OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
                        if (dept != null) {
                            permissionData.deptList.add(dept);
                        }
                    }
                    break;
                case ROLE:
                    if (isPositionType) {
                        List<Position> positions =
                            positionRoleApi.listPositionsByRoleId(tenantId, permission.getRoleId()).getData();
                        permissionData.positionList.addAll(positions);
                    }
                    break;
                case POSITION:
                    if (isPositionType) {
                        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
                        if (orgUnit instanceof Position) {
                            permissionData.positionList.add((Position)orgUnit);
                        }
                    }
                    break;
                case ROLE_DYNAMIC:
                    handleDynamicRolePermission(permission, permissionData, processInstanceId, taskId);
                    break;
                default:
                    LOGGER.warn("collectPermissionData:未知的权限类型：{}", permission.getRoleType());
                    break;
            }
        }

        return permissionData;
    }

    /**
     * 处理动态角色权限
     */
    private void handleDynamicRolePermission(ItemPermission permission, PermissionData permissionData,
        String processInstanceId, String taskId) {

        DynamicRole dynamicRole = dynamicRoleService.getById(permission.getRoleId());
        if (dynamicRole == null)
            return;

        List<Position> positionList = new ArrayList<>();

        if (dynamicRole.getClassPath().contains("4SubProcess")) {
            positionList = dynamicRoleMemberService.listByDynamicRoleIdAndTaskId(dynamicRole, taskId);
        } else {
            if (dynamicRole.getKinds() == null || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
                handleNoneKindDynamicRole(dynamicRole, permissionData, processInstanceId);
            } else {
                positionList = dynamicRoleMemberService.listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole,
                    processInstanceId);
            }
        }

        permissionData.positionList.addAll(positionList);
    }

    /**
     * 处理无种类的动态角色
     */
    private void handleNoneKindDynamicRole(DynamicRole dynamicRole, PermissionData permissionData,
        String processInstanceId) {

        List<OrgUnit> orgUnitList =
            dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);

        for (OrgUnit orgUnit : orgUnitList) {
            switch (orgUnit.getOrgType()) {
                case POSITION:
                    permissionData.positionList.add((Position)orgUnit);
                    break;
                case DEPARTMENT:
                case ORGANIZATION:
                    permissionData.deptList.add(orgUnit);
                    break;
                default:
                    LOGGER.warn("未知的岗位类型：{}", orgUnit.getOrgType());
                    break;
            }
        }
    }

    /**
     * 构建岗位树结构
     */
    private List<ItemRoleOrgUnitModel> buildPositionTree(List<Position> positionList) {
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();

        // 构建岗位列表
        for (Position position : positionList) {
            ItemRoleOrgUnitModel model = createPositionModel(position);
            if (!itemList.contains(model)) {
                itemList.add(model);
            }
        }

        // 岗位排序
        itemList = itemList.stream().sorted().collect(Collectors.toList());
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>(itemList);

        // 获取父级节点
        List<String> parentIdList = new ArrayList<>();
        for (ItemRoleOrgUnitModel model : itemList) {
            if (!parentIdList.contains(model.getParentId())) {
                getParent(allItemList, model);
                parentIdList.add(model.getParentId());
            }
        }

        return allItemList;
    }

    /**
     * 创建岗位模型
     */
    private ItemRoleOrgUnitModel createPositionModel(Position position) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(position.getId());
        model.setParentId(position.getParentId());
        model.setName(position.getName());
        model.setIsParent(false);
        model.setOrgType(position.getOrgType().getValue());
        model.setOrderedPath(position.getOrderedPath());
        model.setPrincipalType(ItemPermissionEnum.POSITION);
        model.setPerson("6:" + position.getId());
        model.setGuidPath(position.getGuidPath());
        return model;
    }

    /**
     * 构建部门树结构
     */
    private List<ItemRoleOrgUnitModel> buildDepartmentTree(String tenantId, List<OrgUnit> deptList) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        for (OrgUnit org : deptList) {
            if (org.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                allItemList.addAll(buildOrganizationTree(tenantId, org));
            } else {
                ItemRoleOrgUnitModel model = createDepartmentModel(org);
                if (!allItemList.contains(model)) {
                    allItemList.add(model);
                }
            }
        }

        return allItemList;
    }

    /**
     * 构建组织树结构
     */
    private List<ItemRoleOrgUnitModel> buildOrganizationTree(String tenantId, OrgUnit org) {
        List<ItemRoleOrgUnitModel> orgItemList = new ArrayList<>();

        List<OrgUnit> orgList =
            orgUnitApi.getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();

        for (OrgUnit orgUnit : orgList) {
            ItemRoleOrgUnitModel model = createOrgUnitModel(orgUnit, org.getId());
            if (!orgItemList.contains(model)) {
                orgItemList.add(model);
            }
        }

        return orgItemList;
    }

    /**
     * 创建部门模型
     */
    private ItemRoleOrgUnitModel createDepartmentModel(OrgUnit org) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(org.getId());
        model.setParentId(org.getParentId());
        model.setName(getOrgUnitDisplayName(org));
        model.setIsParent(true);
        model.setGuidPath(org.getGuidPath());
        model.setOrgType(org.getOrgType().getValue());
        model.setPrincipalType(ItemPermissionEnum.DEPARTMENT);
        return model;
    }

    /**
     * 创建组织单元模型
     */
    private ItemRoleOrgUnitModel createOrgUnitModel(OrgUnit orgUnit, String parentId) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(orgUnit.getId());
        model.setParentId(parentId);
        model.setName(getOrgUnitDisplayName(orgUnit));
        model.setGuidPath(orgUnit.getGuidPath());
        model.setIsParent(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
        model.setOrgType(orgUnit.getOrgType().getValue());
        model.setPrincipalType(orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT) ? ItemPermissionEnum.DEPARTMENT
            : ItemPermissionEnum.POSITION);
        if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
            model.setPerson("6:" + orgUnit.getId());
        }
        return model;
    }

    /**
     * 获取组织单元显示名称
     */
    private String getOrgUnitDisplayName(OrgUnit orgUnit) {
        if (orgUnit instanceof Department) {
            Department dept = (Department)orgUnit;
            return StringUtils.isNotBlank(dept.getAliasName()) ? dept.getAliasName() : dept.getName();
        }
        return orgUnit.getName();
    }

    /**
     * 处理id不为空的情况
     */
    private List<ItemRoleOrgUnitModel> handleNonBlankIdCase(String tenantId, String id) {
        List<ItemRoleOrgUnitModel> itemList = new ArrayList<>();

        List<OrgUnit> orgList = orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();

        for (OrgUnit orgunit : orgList) {
            ItemRoleOrgUnitModel model = createSubOrgUnitModel(orgunit, id);
            if (!itemList.contains(model)) {
                itemList.add(model);
            }
        }

        return itemList;
    }

    /**
     * 创建子组织单元模型
     */
    private ItemRoleOrgUnitModel createSubOrgUnitModel(OrgUnit orgunit, String parentId) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(orgunit.getId());
        model.setParentId(parentId);
        model.setName(orgunit.getName());
        model.setIsParent(orgunit.getOrgType().equals(OrgTypeEnum.DEPARTMENT));
        model.setOrgType(orgunit.getOrgType().getValue());
        model.setGuidPath(orgunit.getGuidPath());

        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
            model.setPrincipalType(ItemPermissionEnum.DEPARTMENT);
            model.setName(getOrgUnitDisplayName(orgunit));
        } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
            model.setPrincipalType(ItemPermissionEnum.POSITION);
            model.setPerson("6:" + orgunit.getId());
        }

        return model;
    }

    @Override
    public List<ItemRoleOrgUnitModel> listCsUser(String id, Integer principalType, String processInstanceId) {
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();

        try {
            String effectiveId = getEffectiveId(id);
            boolean isDepartmentType = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);

            if (StringUtils.isBlank(effectiveId)) {
                items.addAll(handleEmptyIdCase(tenantId, userId, isDepartmentType));
            } else {
                items.addAll(handleWithIdCase(tenantId, userId, effectiveId, isDepartmentType));
            }
        } catch (Exception e) {
            LOGGER.error("获取抄送用户列表失败", e);
        }

        return items;
    }

    /**
     * 获取有效的ID
     */
    private String getEffectiveId(String id) {
        if (StringUtils.isBlank(id) || UtilConsts.NULL.equals(id)) {
            return ""; // 空ID表示根节点
        }
        return id;
    }

    /**
     * 处理ID为空的情况
     */
    private List<ItemRoleOrgUnitModel> handleEmptyIdCase(String tenantId, String userId, boolean isDepartmentType) {
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();

        if (isDepartmentType) {
            handleDepartmentTypeForEmptyId(tenantId, userId, items);
        } else {
            handleCustomGroupTypeForEmptyId(tenantId, userId, items);
        }

        return items;
    }

    /**
     * 处理ID为空时的部门类型
     */
    private void handleDepartmentTypeForEmptyId(String tenantId, String userId, List<ItemRoleOrgUnitModel> items) {
        try {
            Organization organization = orgUnitApi.getOrganization(tenantId, userId).getData();
            List<OrgUnit> orgUnitList =
                orgUnitApi.getSubTree(tenantId, organization.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();

            for (OrgUnit orgUnit : orgUnitList) {
                items.add(createOrgUnitModel(orgUnit, ""));
            }
        } catch (Exception e) {
            LOGGER.warn("获取部门树失败", e);
        }
    }

    /**
     * 处理ID为空时的自定义组类型
     */
    private void handleCustomGroupTypeForEmptyId(String tenantId, String userId, List<ItemRoleOrgUnitModel> items) {
        try {
            List<CustomGroup> groupList = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
            for (CustomGroup customGroup : groupList) {
                ItemRoleOrgUnitModel model = createCustomGroupModel(customGroup);
                if (!items.contains(model)) {
                    items.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("获取自定义组失败", e);
        }
    }

    /**
     * 处理ID不为空的情况
     */
    private List<ItemRoleOrgUnitModel> handleWithIdCase(String tenantId, String userId, String id,
        boolean isDepartmentType) {
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();

        if (isDepartmentType) {
            handleDepartmentTypeWithId(tenantId, id, items);
        } else {
            handleCustomGroupTypeWithId(tenantId, userId, id, items);
        }

        return items;
    }

    /**
     * 处理ID不为空时的部门类型
     */
    private void handleDepartmentTypeWithId(String tenantId, String id, List<ItemRoleOrgUnitModel> items) {
        try {
            List<OrgUnit> orgList = orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
            for (OrgUnit orgUnit : orgList) {
                ItemRoleOrgUnitModel model = createOrgUnitModel(orgUnit, id);
                if (!items.contains(model)) {
                    items.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("获取部门子树失败", e);
        }
    }

    /**
     * 处理ID不为空时的自定义组类型
     */
    private void handleCustomGroupTypeWithId(String tenantId, String userId, String id,
        List<ItemRoleOrgUnitModel> items) {
        try {
            List<CustomGroupMember> customGroupMemberList =
                customGroupApi.listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, id, OrgTypeEnum.POSITION)
                    .getData();

            if (customGroupMemberList != null && !customGroupMemberList.isEmpty()) {
                for (CustomGroupMember customGroupMember : customGroupMemberList) {
                    OrgUnit user =
                        orgUnitApi.getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();

                    if (user != null && !user.getDisabled()) {
                        ItemRoleOrgUnitModel model = createCustomGroupMemberModel(user, customGroupMember, id);
                        if (!items.contains(model)) {
                            items.add(model);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("获取自定义组成员失败", e);
        }
    }

    /**
     * 创建自定义组模型
     */
    private ItemRoleOrgUnitModel createCustomGroupModel(CustomGroup customGroup) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(customGroup.getId());
        model.setParentId("");
        model.setName(customGroup.getGroupName());
        model.setIsParent(true);
        model.setOrgType(ItemConsts.CUSTOMGROUP_KEY);
        model.setPrincipalType(ItemPermissionEnum.GROUP_CUSTOM);
        return model;
    }

    /**
     * 创建自定义组成员模型
     */
    private ItemRoleOrgUnitModel createCustomGroupMemberModel(OrgUnit user, CustomGroupMember customGroupMember,
        String parentId) {
        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(customGroupMember.getMemberId());
        model.setParentId(parentId);
        model.setName(user.getName());
        model.setIsParent(false);
        model.setOrgType(user.getOrgType().getValue());
        model.setPerson("6:" + user.getId() + ":" + user.getParentId());
        model.setPrincipalType(ItemPermissionEnum.POSITION);
        return model;
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
                    ? ItemPermissionEnum.DEPARTMENT : ItemPermissionEnum.POSITION);
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

    @Override
    public List<ItemRoleOrgUnitModel> listCsUserSearch(String name, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();

        try {
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                items.addAll(handleDepartmentSearch(tenantId, userId, name));
            } else if (ItemPrincipalTypeEnum.GROUP_CUSTOM.getValue().equals(principalType)) {
                items.addAll(handleCustomGroupSearch(tenantId, userId, name));
            }
        } catch (Exception e) {
            LOGGER.error("搜索抄送用户失败", e);
        }

        return items;
    }

    /**
     * 处理部门类型搜索
     */
    private List<ItemRoleOrgUnitModel> handleDepartmentSearch(String tenantId, String userId, String name) {
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();

        try {
            Organization organization = orgUnitApi.getOrganization(tenantId, userId).getData();
            List<Department> deptList = departmentApi.listByParentId(tenantId, organization.getId()).getData();

            List<OrgUnit> orgUnitListTemp = new ArrayList<>();
            for (OrgUnit orgUnitTemp : deptList) {
                orgUnitListTemp.addAll(orgUnitApi
                    .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, orgUnitTemp.getDn())
                    .getData());
            }

            for (OrgUnit orgUnitTemp : orgUnitListTemp) {
                ItemRoleOrgUnitModel model = createDepartmentSearchModel(orgUnitTemp);
                if (model != null && !items.contains(model)) {
                    items.add(model);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("部门搜索失败", e);
        }

        return items;
    }

    /**
     * 创建部门搜索模型
     */
    private ItemRoleOrgUnitModel createDepartmentSearchModel(OrgUnit orgUnit) {
        // 过滤不需要的组织类型
        if (orgUnit.getOrgType().equals(OrgTypeEnum.PERSON) || orgUnit.getOrgType().equals(OrgTypeEnum.GROUP)) {
            return null;
        }

        ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
        model.setId(orgUnit.getId());
        model.setParentId(orgUnit.getParentId());
        model.setName(orgUnit.getName());
        model.setOrgType(orgUnit.getOrgType().getValue());

        if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
            || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
            model.setIsParent(true);
        } else if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
            model.setPerson("6:" + orgUnit.getId());
            model.setIsParent(true);
        }

        return model;
    }

    /**
     * 处理自定义组搜索
     */
    private List<ItemRoleOrgUnitModel> handleCustomGroupSearch(String tenantId, String userId, String name) {
        List<ItemRoleOrgUnitModel> items = new ArrayList<>();

        try {
            List<CustomGroup> groupList = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
            for (CustomGroup customGroup : groupList) {
                ItemRoleOrgUnitModel groupModel = createCustomGroupModel(customGroup);

                boolean hasMatchingMember = addMatchingGroupMembers(items, tenantId, userId, customGroup, name);

                // 如果有匹配的成员且组模型不在列表中，则添加组模型
                if (hasMatchingMember && !items.contains(groupModel)) {
                    items.add(groupModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("自定义组搜索失败", e);
        }

        return items;
    }

    /**
     * 添加匹配的组成员
     */
    private boolean addMatchingGroupMembers(List<ItemRoleOrgUnitModel> items, String tenantId, String userId,
        CustomGroup customGroup, String name) {
        boolean hasMatchingMember = false;

        try {
            List<CustomGroupMember> customGroupMemberList =
                customGroupApi
                    .listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, customGroup.getId(),
                        OrgTypeEnum.POSITION)
                    .getData();

            if (customGroupMemberList != null && !customGroupMemberList.isEmpty()) {
                for (CustomGroupMember customGroupMember : customGroupMemberList) {
                    OrgUnit user =
                        orgUnitApi.getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();

                    if (user != null && user.getName().contains(name) && !user.getDisabled()) {
                        ItemRoleOrgUnitModel memberModel =
                            createCustomGroupMemberModel(user, customGroupMember, customGroup.getId());
                        if (!items.contains(memberModel)) {
                            items.add(memberModel);
                            hasMatchingMember = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("获取自定义组成员失败", e);
        }
        return hasMatchingMember;
    }

    @Override
    public List<ItemRoleOrgUnitModel> listPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();

        try {
            List<ItemPermission> permissions = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);

            boolean isPositionType = ItemPrincipalTypeEnum.POSITION.getValue().equals(principalType);
            boolean isDepartmentType = ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType);
            boolean isCustomGroupType = ItemPrincipalTypeEnum.GROUP_CUSTOM.getValue().equals(principalType);

            if ((isPositionType || isDepartmentType)) {
                allItemList.addAll(handlePositionAndDepartmentType(permissions, tenantId, isPositionType,
                    isDepartmentType, id, processInstanceId));
            } else if (isCustomGroupType) {
                allItemList.addAll(handleCustomGroupType(tenantId, id));
            }
        } catch (Exception e) {
            LOGGER.error("获取权限用户列表失败", e);
        }

        return allItemList;
    }

    /**
     * 处理岗位和部门类型
     */
    private List<ItemRoleOrgUnitModel> handlePositionAndDepartmentType(List<ItemPermission> permissions,
        String tenantId, boolean isPositionType, boolean isDepartmentType, String id, String processInstanceId) {

        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        if (StringUtils.isBlank(id)) {
            allItemList.addAll(handlePositionAndDepartmentEmptyId(permissions, tenantId, isPositionType,
                isDepartmentType, processInstanceId));
        } else {
            allItemList.addAll(handlePositionAndDepartmentWithId(tenantId, id));
        }

        return allItemList;
    }

    /**
     * 处理岗位和部门类型（ID为空的情况）
     */
    private List<ItemRoleOrgUnitModel> handlePositionAndDepartmentEmptyId(List<ItemPermission> permissions,
        String tenantId, boolean isPositionType, boolean isDepartmentType, String processInstanceId) {

        PermissionData permissionData = collectPositionAndDepartmentPermissionData(permissions, tenantId,
            isPositionType, isDepartmentType, processInstanceId);

        List<ItemRoleOrgUnitModel> positionModels = buildPositionModels(permissionData.positionList);
        List<ItemRoleOrgUnitModel> allItems = new ArrayList<>(positionModels);

        // 添加岗位的父级部门
        addParentDepartments(allItems, positionModels);

        // 添加授权的部门
        addAuthorizedDepartments(allItems, permissionData.deptList, tenantId);

        return allItems;
    }

    /**
     * 收集岗位和部门权限数据
     */
    private PermissionData collectPositionAndDepartmentPermissionData(List<ItemPermission> permissions, String tenantId,
        boolean isPositionType, boolean isDepartmentType, String processInstanceId) {

        PermissionData data = new PermissionData();

        for (ItemPermission permission : permissions) {
            switch (permission.getRoleType()) {
                case DEPARTMENT:
                    if (isDepartmentType) {
                        OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
                        if (dept != null) {
                            data.deptList.add(dept);
                        }
                    }
                    break;
                case ROLE:
                    if (isPositionType) {
                        List<Position> positions =
                            positionRoleApi.listPositionsByRoleId(tenantId, permission.getRoleId()).getData();
                        data.positionList.addAll(positions);
                    }
                    break;
                case POSITION:
                    if (isPositionType) {
                        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
                        if (orgUnit instanceof Position) {
                            data.positionList.add((Position)orgUnit);
                        }
                    }
                    break;
                case ROLE_DYNAMIC:
                    handleDynamicRolePermission(data, permission, processInstanceId);
                    break;
                default:
                    LOGGER.warn("PermissionData:未知的权限类型：{}", permission.getRoleType());
                    break;
            }
        }

        return data;
    }

    /**
     * 处理动态角色权限
     */
    private void handleDynamicRolePermission(PermissionData data, ItemPermission permission, String processInstanceId) {
        DynamicRole dynamicRole = dynamicRoleService.getById(permission.getRoleId());
        if (dynamicRole == null)
            return;

        List<Position> positionList = new ArrayList<>();

        if (dynamicRole.getKinds() == null || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
            handleNoneKindDynamicRole(data, dynamicRole, processInstanceId);
        } else {
            positionList = dynamicRoleMemberService.listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole,
                processInstanceId);
        }

        data.positionList.addAll(positionList);
    }

    /**
     * 处理无种类的动态角色
     */
    private void handleNoneKindDynamicRole(PermissionData data, DynamicRole dynamicRole, String processInstanceId) {
        List<OrgUnit> orgUnitList =
            dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);

        for (OrgUnit orgUnit : orgUnitList) {
            switch (orgUnit.getOrgType()) {
                case POSITION:
                    data.positionList.add((Position)orgUnit);
                    break;
                case DEPARTMENT:
                case ORGANIZATION:
                    data.deptList.add(orgUnit);
                    break;
                default:
                    LOGGER.warn("未知的部门类型：{}", orgUnit.getOrgType());
                    break;
            }
        }
    }

    /**
     * 构建岗位模型列表
     */
    private List<ItemRoleOrgUnitModel> buildPositionModels(List<Position> positionList) {
        List<ItemRoleOrgUnitModel> models = new ArrayList<>();

        for (Position position : positionList) {
            ItemRoleOrgUnitModel model = createPositionModel(position);
            if (!models.contains(model)) {
                models.add(model);
            }
        }

        return models.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 添加父级部门
     */
    private void addParentDepartments(List<ItemRoleOrgUnitModel> allItems, List<ItemRoleOrgUnitModel> positionModels) {
        List<String> processedParentIds = new ArrayList<>();

        for (ItemRoleOrgUnitModel model : positionModels) {
            allItems.add(model);
            if (!processedParentIds.contains(model.getParentId())) {
                getParent(allItems, model);
                processedParentIds.add(model.getParentId());
            }
        }
    }

    /**
     * 添加授权的部门
     */
    private void addAuthorizedDepartments(List<ItemRoleOrgUnitModel> allItems, List<OrgUnit> deptList,
        String tenantId) {
        for (OrgUnit org : deptList) {
            if (org.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                addOrganizationDepartments(allItems, org, tenantId);
            } else {
                addSingleDepartment(allItems, org);
            }
        }
    }

    /**
     * 添加组织下的部门
     */
    private void addOrganizationDepartments(List<ItemRoleOrgUnitModel> allItems, OrgUnit org, String tenantId) {
        List<OrgUnit> orgList =
            orgUnitApi.getSubTree(tenantId, org.getId(), OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();

        for (OrgUnit orgUnit : orgList) {
            ItemRoleOrgUnitModel model = createOrgUnitModel(orgUnit, org.getId());
            if (!allItems.contains(model)) {
                allItems.add(model);
            }
        }
    }

    /**
     * 添加单个部门
     */
    private void addSingleDepartment(List<ItemRoleOrgUnitModel> allItems, OrgUnit org) {
        ItemRoleOrgUnitModel model = createDepartmentModel(org);
        if (!allItems.contains(model)) {
            allItems.add(model);
        }
    }

    /**
     * 处理岗位和部门类型（ID不为空的情况）
     */
    private List<ItemRoleOrgUnitModel> handlePositionAndDepartmentWithId(String tenantId, String id) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        List<OrgUnit> orgList = orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
        for (OrgUnit orgunit : orgList) {
            ItemRoleOrgUnitModel model = createSubOrgUnitModel(orgunit, id);
            if (!allItemList.contains(model)) {
                allItemList.add(model);
            }
        }

        return allItemList;
    }

    /**
     * 处理自定义组类型
     */
    private List<ItemRoleOrgUnitModel> handleCustomGroupType(String tenantId, String id) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        if (StringUtils.isBlank(id)) {
            allItemList.addAll(handleCustomGroupEmptyId(tenantId));
        } else {
            allItemList.addAll(handleCustomGroupWithId(tenantId, id));
        }

        return allItemList;
    }

    /**
     * 处理自定义组类型（ID为空的情况）
     */
    private List<ItemRoleOrgUnitModel> handleCustomGroupEmptyId(String tenantId) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        List<CustomGroup> customGroupList =
            customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();

        for (CustomGroup customGroup : customGroupList) {
            ItemRoleOrgUnitModel model = createCustomGroupModel(customGroup);
            if (!allItemList.contains(model)) {
                allItemList.add(model);
            }
        }

        return allItemList;
    }

    /**
     * 处理自定义组类型（ID不为空的情况）
     */
    private List<ItemRoleOrgUnitModel> handleCustomGroupWithId(String tenantId, String id) {
        List<ItemRoleOrgUnitModel> allItemList = new ArrayList<>();

        List<CustomGroupMember> customGroupMemberList =
            customGroupApi
                .listCustomGroupMemberByGroupIdAndMemberType(tenantId, Y9LoginUserHolder.getPersonId(), id,
                    OrgTypeEnum.POSITION)
                .getData();

        if (customGroupMemberList != null && !customGroupMemberList.isEmpty()) {
            for (CustomGroupMember customGroupMember : customGroupMemberList) {
                OrgUnit user =
                    orgUnitApi.getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId()).getData();

                if (user != null && !user.getDisabled()) {
                    ItemRoleOrgUnitModel model = createCustomGroupMemberModel(user, customGroupMember, id);
                    if (!allItemList.contains(model)) {
                        allItemList.add(model);
                    }
                }
            }
        }

        return allItemList;
    }

    @Override
    public List<OrgUnit> listPermUser4SubmitTo(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgList = new ArrayList<>();
        try {
            List<ItemPermission> permissions = itemPermissionService
                .listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);

            List<OrgUnit> orgListTemp = collectPermissionOrgUnits(permissions, tenantId, processInstanceId);

            // 去重并添加到结果列表
            for (OrgUnit orgUnit : orgListTemp) {
                if (!orgList.contains(orgUnit)) {
                    orgList.add(orgUnit);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取提交用户列表失败", e);
        }

        return orgList;
    }

    /**
     * 收集权限相关的组织单元
     */
    private List<OrgUnit> collectPermissionOrgUnits(List<ItemPermission> permissions, String tenantId,
        String processInstanceId) {
        List<OrgUnit> orgListTemp = new ArrayList<>();

        for (ItemPermission permission : permissions) {
            switch (permission.getRoleType()) {
                case ROLE:
                    orgListTemp.addAll(getRolePositions(tenantId, permission.getRoleId()));
                    break;
                case ROLE_DYNAMIC:
                    orgListTemp.addAll(getDynamicRolePositions(permission, processInstanceId));
                    break;
                case POSITION:
                    OrgUnit position = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
                    if (position != null) {
                        orgListTemp.add(position);
                    }
                    break;
                default:
                    LOGGER.warn("collectPermissionOrgUnits:未知的权限类型：{}", permission.getRoleType());
                    break;
            }
        }
        return orgListTemp;
    }

    /**
     * 获取角色下的所有岗位
     */
    private List<Position> getRolePositions(String tenantId, String roleId) {
        try {
            return positionRoleApi.listPositionsByRoleId(tenantId, roleId).getData();
        } catch (Exception e) {
            LOGGER.warn("获取角色岗位失败，roleId: {}", roleId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取动态角色下的岗位
     */
    private List<OrgUnit> getDynamicRolePositions(ItemPermission permission, String processInstanceId) {
        List<OrgUnit> positionList = new ArrayList<>();

        try {
            DynamicRole dynamicRole = dynamicRoleService.getById(permission.getRoleId());
            if (dynamicRole == null) {
                return positionList;
            }

            if (dynamicRole.getKinds() == null || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
                // 动态角色种类为【无】或null时，针对岗位或部门
                List<OrgUnit> orgUnitList =
                    dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);

                for (OrgUnit orgUnit : orgUnitList) {
                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                        positionList.add(orgUnit);
                    }
                }
            } else {
                // 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                List<Position> positions = dynamicRoleMemberService
                    .listPositionByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                positionList.addAll(positions);
            }
        } catch (Exception e) {
            LOGGER.warn("获取动态角色岗位失败，roleId: {}", permission.getRoleId(), e);
        }

        return positionList;
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
                if (o.getRoleType() == ItemPermissionEnum.ROLE && p) {
                    positionList.addAll(positionRoleApi.listPositionsByRoleId(tenantId, o.getRoleId()).getData());
                }
                // 授权岗位,加入岗位集合
                if (o.getRoleType().equals(ItemPermissionEnum.POSITION) && p) {
                    positionList.add((Position)orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData());
                }
                // 授权部门,加入部门集合
                if (o.getRoleType() == ItemPermissionEnum.DEPARTMENT && d) {
                    Department dept = departmentApi.get(tenantId, o.getRoleId()).getData();
                    if (dept != null) {
                        deptList.add(dept);
                    }
                }
                // 授权动态角色,根据动态角色种类区分
                if (o.getRoleType() == ItemPermissionEnum.ROLE_DYNAMIC) {
                    DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                    List<Position> pList = new ArrayList<>();
                    if (null == dynamicRole.getKinds() || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
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
                    positionList.addAll(pList);
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
                model.setPrincipalType(ItemPermissionEnum.POSITION);
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
                List<OrgUnit> orgUnitList = new ArrayList<>(
                    orgUnitApi.treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION, org.getDn())
                        .getData());
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
        } else if (ItemPrincipalTypeEnum.GROUP_CUSTOM.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
                for (CustomGroup customGroup : grouplist) {
                    ItemRoleOrgUnitModel model = new ItemRoleOrgUnitModel();
                    model.setId(customGroup.getId());
                    model.setName(customGroup.getGroupName());
                    model.setOrgType(ItemConsts.CUSTOMGROUP_KEY);
                    model.setParentId("");
                    model.setPrincipalType(ItemPermissionEnum.GROUP_CUSTOM);
                    model.setIsParent(true);
                    if (allItemList.contains(model)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId, Y9LoginUserHolder.getPersonId(),
                            customGroup.getId(), OrgTypeEnum.POSITION)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            OrgUnit user =
                                orgUnitApi.getOrgUnitPersonOrPosition(tenantId, customGroupMember.getMemberId())
                                    .getData();
                            if (user != null && user.getName().contains(name) && !user.getDisabled()) {
                                ItemRoleOrgUnitModel model1 = new ItemRoleOrgUnitModel();
                                model1.setId(customGroupMember.getMemberId());
                                model1.setName(user.getName());
                                model1.setOrgType(user.getOrgType().getValue());
                                model1.setParentId(customGroup.getId());
                                model1.setPerson("6:" + user.getId() + ":" + user.getParentId());
                                model1.setPrincipalType(ItemPermissionEnum.POSITION);
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
                    model.setPrincipalType(ItemPermissionEnum.DEPARTMENT);
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
                    model.setPrincipalType(ItemPermissionEnum.DEPARTMENT);
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

    /**
     * 权限数据容器类
     */
    private static class PermissionData {
        List<OrgUnit> deptList = new ArrayList<>();
        List<Position> positionList = new ArrayList<>();
    }
}
