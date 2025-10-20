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
import net.risesoft.util.Y9DateTimeUtils;
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
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                previouspdId = previouspd.getId();
            }
        }
        List<ItemPermission> previousipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);

        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId).getData();
        /*
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的角色，没有就保存
         */
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            for (ItemPermission ip : previousipList) {
                String taskDefKeyTemp = ip.getTaskDefKey(), roleId = ip.getRoleId();
                ItemPermissionEnum roleType = ip.getRoleType();
                if (currentTaskDefKey.equals(taskDefKeyTemp)) {
                    ItemPermission ipTemp =
                        itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
                            latestpdId, currentTaskDefKey, roleId);
                    if (null == ipTemp) {
                        self.save(itemId, latestpdId, currentTaskDefKey, roleId, roleType);
                    }
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
        map.put("existDepartment", false);
        for (ItemPermission o : objectPermList) {
            if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DEPARTMENT)) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    map.put("existDepartment", true);
                }
            } else if (Objects.equals(o.getRoleType(), ItemPermissionEnum.ROLE_DYNAMIC)) {
                DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                if (dynamicRole.getClassPath().contains("4SubProcess")) {// 针对岗位,加入岗位集合
                    List<Position> pList = dynamicRoleMemberService.listByDynamicRoleIdAndTaskId(dynamicRole, taskId);
                    if (!pList.isEmpty()) {
                        map.put(ItemConsts.EXISTPOSITION_KEY, true);
                    }
                } else {
                    if (null == dynamicRole.getKinds() || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE)) {
                        // 动态角色种类为【无】或null时，针对岗位或部门
                        List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList1) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                map.put(ItemConsts.EXISTPOSITION_KEY, true);
                                break;
                            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                map.put("existDepartment", true);
                                break;
                            }
                        }
                    } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                        map.put(ItemConsts.EXISTPOSITION_KEY, true);
                    }
                }
            } else {
                map.put(ItemConsts.EXISTPOSITION_KEY, true);
            }

        }
        return map;
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
        ItemPermission oldip = this.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
            processDefinitionId, taskDefKey, roleId);
        if (null == oldip) {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemPermission newip = new ItemPermission();
            newip.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newip.setItemId(itemId);
            newip.setProcessDefinitionId(processDefinitionId);
            newip.setRoleId(roleId);
            newip.setRoleType(roleType);
            newip.setTenantId(tenantId);
            newip.setCreatDate(Y9DateTimeUtils.formatCurrentDateTime());
            newip.setTaskDefKey(taskDefKey);
            Integer tabIndex = itemPermissionRepository.getMaxTabIndex(itemId, processDefinitionId, taskDefKey);
            if (null != tabIndex) {
                ++tabIndex;
            } else {
                tabIndex = 1;
            }
            newip.setTabIndex(tabIndex);

            itemPermissionRepository.save(newip);
            return newip;
        }
        return oldip;
    }
}
