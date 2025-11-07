package net.risesoft.service.config.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemPermission;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemPermissionServiceImpl implements ItemPermissionService {

    private final ItemPermissionRepository itemPermissionRepository;

    private final DynamicRoleMemberService dynamicRoleMemberService;

    private final RoleApi roleApi;

    private final DynamicRoleService dynamicRoleService;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ItemRepository itemRepository;

    private final OrgUnitApi orgUnitApi;

    private final ItemPermissionService self;

    public ItemPermissionServiceImpl(
        ItemPermissionRepository itemPermissionRepository,
        DynamicRoleMemberService dynamicRoleMemberService,
        RoleApi roleApi,
        DynamicRoleService dynamicRoleService,
        RepositoryApi repositoryApi,
        ProcessDefinitionApi processDefinitionApi,
        ItemRepository itemRepository,
        OrgUnitApi orgUnitApi,
        @Lazy ItemPermissionService self) {
        this.itemPermissionRepository = itemPermissionRepository;
        this.dynamicRoleMemberService = dynamicRoleMemberService;
        this.roleApi = roleApi;
        this.dynamicRoleService = dynamicRoleService;
        this.repositoryApi = repositoryApi;
        this.processDefinitionApi = processDefinitionApi;
        this.itemRepository = itemRepository;
        this.orgUnitApi = orgUnitApi;
        this.self = self;
    }

    @Override
    @Transactional
    public void copyPerm(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null;
        // 获取最新和前一版本的流程定义
        ProcessDefinitionModel latestProcessDefinition = getLatestProcessDefinition(tenantId, item);
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        String previousProcessDefinitionId =
            getPreviousProcessDefinitionId(tenantId, processDefinitionId, latestProcessDefinition);
        // 获取前一版本的权限列表
        List<ItemPermission> previousPermissions =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, previousProcessDefinitionId);
        // 获取最新流程定义的节点并复制权限
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        /*
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的角色，没有就保存
         */
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyPermissionsForNode(itemId, latestProcessDefinitionId, currentTaskDefKey, previousPermissions);
        }
    }

    /**
     * 获取最新流程定义
     */
    private ProcessDefinitionModel getLatestProcessDefinition(String tenantId, Item item) {
        String processDefinitionKey = item.getWorkflowGuid();
        return repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
    }

    /**
     * 获取前一版本流程定义ID
     */
    private String getPreviousProcessDefinitionId(String tenantId, String processDefinitionId,
        ProcessDefinitionModel latestProcessDefinition) {
        String previousProcessDefinitionId = processDefinitionId;
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        if (processDefinitionId.equals(latestProcessDefinitionId) && latestProcessDefinition.getVersion() > 1) {
            ProcessDefinitionModel previousProcessDefinition =
                repositoryApi.getPreviousProcessDefinitionById(tenantId, latestProcessDefinitionId).getData();
            previousProcessDefinitionId = previousProcessDefinition.getId();
        }
        return previousProcessDefinitionId;
    }

    /**
     * 为指定节点复制权限
     */
    private void copyPermissionsForNode(String itemId, String latestProcessDefinitionId, String currentTaskDefKey,
        List<ItemPermission> previousPermissions) {
        for (ItemPermission permission : previousPermissions) {
            String taskDefKeyTemp = permission.getTaskDefKey();
            String roleId = permission.getRoleId();
            ItemPermissionEnum roleType = permission.getRoleType();
            if (currentTaskDefKey.equals(taskDefKeyTemp)) {
                ItemPermission existingPermission =
                    itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
                        latestProcessDefinitionId, currentTaskDefKey, roleId);
                if (null == existingPermission) {
                    self.save(itemId, latestProcessDefinitionId, currentTaskDefKey, roleId, roleType);
                }
            }
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        itemPermissionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            itemPermissionRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除绑定信息失败", e);
        }
    }

    @Override
    public ItemPermission findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(String itemId,
        String processDefinitionId, String taskdefKey, String roleId) {
        return itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
            processDefinitionId, taskdefKey, roleId);
    }

    @Override
    public Map<String, Object> getTabMap(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId, String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> objectPermList =
            listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
        Map<String, Object> map = new HashMap<>(16);
        map.put(ItemConsts.EXISTPOSITION_KEY, false);
        map.put(ItemConsts.EXISTDEPARTMENT_KEY, false);
        for (ItemPermission permission : objectPermList) {
            updateTabMapForPermission(map, permission, tenantId, processInstanceId, taskId);
            // 如果两个标识都为true，可以提前退出循环
            if ((Boolean)map.get(ItemConsts.EXISTPOSITION_KEY) && (Boolean)map.get(ItemConsts.EXISTDEPARTMENT_KEY)) {
                break;
            }
        }
        return map;
    }

    /**
     * 根据权限类型更新tabMap
     */
    private void updateTabMapForPermission(Map<String, Object> map, ItemPermission permission, String tenantId,
        String processInstanceId, String taskId) {

        if (Objects.equals(permission.getRoleType(), ItemPermissionEnum.DEPARTMENT)) {
            handleDepartmentPermission(map, permission, tenantId);
        } else if (Objects.equals(permission.getRoleType(), ItemPermissionEnum.ROLE_DYNAMIC)) {
            handleDynamicRolePermission(map, permission, processInstanceId, taskId);
        } else {
            // 其他类型默认设置岗位为true
            map.put(ItemConsts.EXISTPOSITION_KEY, true);
        }
    }

    /**
     * 处理部门权限类型
     */
    private void handleDepartmentPermission(Map<String, Object> map, ItemPermission permission, String tenantId) {
        OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, permission.getRoleId()).getData();
        if (null != orgUnit) {
            map.put(ItemConsts.EXISTDEPARTMENT_KEY, true);
        }
    }

    /**
     * 处理动态角色权限类型
     */
    private void handleDynamicRolePermission(Map<String, Object> map, ItemPermission permission,
        String processInstanceId, String taskId) {
        DynamicRole dynamicRole = dynamicRoleService.getById(permission.getRoleId());
        if (dynamicRole.getClassPath().contains("4SubProcess")) {
            // 针对岗位,加入岗位集合
            List<Position> positionList = dynamicRoleMemberService.listByDynamicRoleIdAndTaskId(dynamicRole, taskId);
            if (!positionList.isEmpty()) {
                map.put(ItemConsts.EXISTPOSITION_KEY, true);
            }
        } else {
            if (null == dynamicRole.getKinds() || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
                // 动态角色种类为【无】或null时，针对岗位或部门
                handleDynamicRoleWithNoKind(map, dynamicRole, processInstanceId);
            } else {
                // 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                map.put(ItemConsts.EXISTPOSITION_KEY, true);
            }
        }
    }

    /**
     * 处理无种类的动态角色
     */
    private void handleDynamicRoleWithNoKind(Map<String, Object> map, DynamicRole dynamicRole,
        String processInstanceId) {
        List<OrgUnit> orgUnitList =
            dynamicRoleMemberService.listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
        for (OrgUnit orgUnit : orgUnitList) {
            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                map.put(ItemConsts.EXISTPOSITION_KEY, true);
                break;
            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                map.put(ItemConsts.EXISTDEPARTMENT_KEY, true);
                break;
            }
        }
    }

    @Override
    public List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemPermission> ipList = itemPermissionRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId, processDefinitionId, taskDefKey);
        ipList.forEach(ip -> ip.setRoleName(getRoleName(ip)));
        return ipList;
    }

    @Override
    public List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemPermission> ipList = itemPermissionRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId, processDefinitionId, taskDefKey);
        if (ipList.isEmpty()) {
            ipList = itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId,
                processDefinitionId);
        }
        for (ItemPermission ip : ipList) {
            ip.setRoleName(getRoleName(ip));
        }
        ipList.forEach(ip -> ip.setRoleName(getRoleName(ip)));
        return ipList;
    }

    private String getRoleName(ItemPermission ip) {
        String roleName = "角色不存在";
        switch (ip.getRoleType()) {
            case ROLE:
                Role role = roleApi.getRole(ip.getRoleId()).getData();
                if (null != role) {
                    roleName = role.getName();
                }
                break;
            case DEPARTMENT:
            case USER:
            case POSITION:
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), ip.getRoleId()).getData();
                if (null != orgUnit) {
                    roleName = orgUnit.getName();
                }
                break;
            case ROLE_DYNAMIC:
                DynamicRole dr = dynamicRoleService.getById(ip.getRoleId());
                if (null != dr) {
                    roleName = dr.getName();
                }
                break;
            default:
                roleName = "角色不存在";
                break;
        }
        return roleName;
    }

    @Override
    @Transactional
    public void removePerm(String itemId, String processDefinitionId) {
        List<ItemPermission> ipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        itemPermissionRepository.deleteAll(ipList);
    }

    @Override
    @Transactional
    public ItemPermission save(String itemId, String processDefinitionId, String taskDefKey, String roleId,
        ItemPermissionEnum roleType) {
        ItemPermission existingPermission = this.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
            processDefinitionId, taskDefKey, roleId);
        if (null == existingPermission) {
            ItemPermission newPermission =
                createItemPermission(itemId, processDefinitionId, taskDefKey, roleId, roleType);
            itemPermissionRepository.save(newPermission);
            return newPermission;
        }
        return existingPermission;
    }

    /**
     * 创建事项权限对象
     */
    private ItemPermission createItemPermission(String itemId, String processDefinitionId, String taskDefKey,
        String roleId, ItemPermissionEnum roleType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ItemPermission newPermission = new ItemPermission();
        newPermission.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newPermission.setItemId(itemId);
        newPermission.setProcessDefinitionId(processDefinitionId);
        newPermission.setRoleId(roleId);
        newPermission.setRoleType(roleType);
        newPermission.setTenantId(tenantId);
        newPermission.setTaskDefKey(taskDefKey);
        // 设置标签索引
        Integer maxTabIndex = itemPermissionRepository.getMaxTabIndex(itemId, processDefinitionId, taskDefKey);
        newPermission.setTabIndex(maxTabIndex == null ? 1 : maxTabIndex + 1);
        return newPermission;
    }
}
