package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
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
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemPermissionServiceImpl implements ItemPermissionService {

    private final ItemPermissionRepository itemPermissionRepository;

    private final DynamicRoleMemberService dynamicRoleMemberService;

    private final RoleApi roleApi;

    private final DynamicRoleService dynamicRoleService;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public void copyPerm(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
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
                Integer roleType = ip.getRoleType();
                if (currentTaskDefKey.equals(taskDefKeyTemp)) {
                    ItemPermission ipTemp =
                        itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
                            latestpdId, currentTaskDefKey, roleId);
                    if (null == ipTemp) {
                        this.save(itemId, latestpdId, currentTaskDefKey, roleId, roleType);
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
        map.put("existPosition", false);
        map.put("existDepartment", false);
        for (ItemPermission o : objectPermList) {
            if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DEPARTMENT.getValue())) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    map.put("existDepartment", true);
                }
            } else if (Objects.equals(o.getRoleType(), ItemPermissionEnum.DYNAMICROLE.getValue())) {
                DynamicRole dynamicRole = dynamicRoleService.getById(o.getRoleId());
                List<Position> pList = new ArrayList<>();
                if (dynamicRole.getClassPath().contains("4SubProcess")) {// 针对岗位,加入岗位集合
                    pList = dynamicRoleMemberService.listByDynamicRoleIdAndTaskId(dynamicRole, taskId);
                    if (pList.size() > 0) {
                        map.put("existPosition", true);
                    }
                } else {
                    if (null == dynamicRole.getKinds()
                        || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
                        // 动态角色种类为【无】或null时，针对岗位或部门
                        List<OrgUnit> orgUnitList1 = dynamicRoleMemberService
                            .listByDynamicRoleIdAndProcessInstanceId(dynamicRole, processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList1) {
                            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                                map.put("existPosition", true);
                                break;
                            } else if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)
                                || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION)) {
                                map.put("existDepartment", true);
                                break;
                            }
                        }
                    } else {// 动态角色种类为【角色】或【部门配置分类】时，针对岗位
                        map.put("existPosition", true);
                    }
                }
            } else {
                map.put("existPosition", true);
            }

        }
        return map;
    }

    @Override
    public List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> ipList = itemPermissionRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId, processDefinitionId, taskDefKey);
        for (ItemPermission ip : ipList) {
            if ((ip.getRoleType() == 1)) {
                Role role = roleApi.getRole(ip.getRoleId()).getData();
                if (null != role) {
                    ip.setRoleName(role.getName());
                }
            } else if (ip.getRoleType() == 2 || ip.getRoleType() == 3 || ip.getRoleType() == 6) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, ip.getRoleId()).getData();
                if (null != orgUnit) {
                    ip.setRoleName(orgUnit.getName());
                }
            } else if (ip.getRoleType() == 4) {
                DynamicRole dr = dynamicRoleService.getById(ip.getRoleId());
                if (null != dr) {
                    ip.setRoleName(dr.getName());
                }
            }
        }
        return ipList;
    }

    @Override
    public List<ItemPermission> listByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(String itemId,
        String processDefinitionId, String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> ipList = itemPermissionRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId, processDefinitionId, taskDefKey);
        if (ipList.isEmpty()) {
            ipList = itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId,
                processDefinitionId);
        }
        for (ItemPermission ip : ipList) {
            if (ip.getRoleType() == 4) {
                DynamicRole dr = dynamicRoleService.getById(ip.getRoleId());
                if (null != dr) {
                    ip.setRoleName(dr.getName());
                }
            } else if (ip.getRoleType() == 2 || ip.getRoleType() == 3) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, ip.getRoleId()).getData();
                if (null != orgUnit) {
                    ip.setRoleName(orgUnit.getName());
                }
            } else if ((ip.getRoleType() == 1)) {
                Role role = roleApi.getRole(ip.getRoleId()).getData();
                if (null != role) {
                    ip.setRoleName(role.getName());
                }
            }
        }
        return ipList;
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
        Integer roleType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            newip.setCreatDate(sdf.format(new Date()));
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
