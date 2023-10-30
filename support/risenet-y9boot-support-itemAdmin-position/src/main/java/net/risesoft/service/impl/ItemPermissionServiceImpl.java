package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.repository.jpa.ItemPermissionRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.ItemPermissionService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemPermissionService")
public class ItemPermissionServiceImpl implements ItemPermissionService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemPermissionRepository itemPermissionRepository;

    @Autowired
    private DynamicRoleMemberService dynamicRoleMemberService;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private DynamicRoleService dynamicRoleService;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Override
    @Transactional(readOnly = false)
    public void copyPerm(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd = repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey);
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryManager.getPreviousProcessDefinitionById(tenantId, latestpdId);
                previouspdId = previouspd.getId();
            }
        }
        List<ItemPermission> previousipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);

        List<Map<String, Object>> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false);
        /**
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的角色，没有就保存
         */
        for (Map<String, Object> map : nodes) {
            String currentTaskDefKey = (String)map.get("taskDefKey");
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
    @Transactional(readOnly = false)
    public void delete(String id) {
        itemPermissionRepository.deleteById(id);
    }

    @Override
    public List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> ipList = itemPermissionRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByTabIndexAsc(itemId, processDefinitionId, taskDefKey);
        for (ItemPermission ip : ipList) {
            if ((ip.getRoleType() == 1)) {
                Role role = roleManager.getRole(ip.getRoleId()).getData();
                if (null != role) {
                    ip.setRoleName(role.getName());
                }
            } else if (ip.getRoleType() == 2 || ip.getRoleType() == 3 || ip.getRoleType() == 6) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, ip.getRoleId()).getData();
                if (null != orgUnit) {
                    ip.setRoleName(orgUnit.getName());
                }
            } else if (ip.getRoleType() == 4) {
                DynamicRole dr = dynamicRoleService.findOne(ip.getRoleId());
                if (null != dr) {
                    ip.setRoleName(dr.getName());
                }
            }
        }
        return ipList;
    }

    @Override
    public ItemPermission findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(String itemId,
        String processDefinitionId, String taskdefKey, String roleId) {
        return itemPermissionRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndRoleId(itemId,
            processDefinitionId, taskdefKey, roleId);
    }

    @Override
    public List<ItemPermission> findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(String itemId,
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
                DynamicRole dr = dynamicRoleService.findOne(ip.getRoleId());
                if (null != dr) {
                    ip.setRoleName(dr.getName());
                }
            } else if (ip.getRoleType() == 2 || ip.getRoleType() == 3) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, ip.getRoleId()).getData();
                if (null != orgUnit) {
                    ip.setRoleName(orgUnit.getName());
                }
            } else if ((ip.getRoleType() == 1)) {
                Role role = roleManager.getRole(ip.getRoleId()).getData();
                if (null != role) {
                    ip.setRoleName(role.getName());
                }
            }
        }
        return ipList;
    }

    @Override
    public Map<String, Object> getTabMap(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> objectPermList =
            findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("existPosition", false);
        map.put("existDepartment", false);
        for (ItemPermission o : objectPermList) {
            if (o.getRoleType() == ItemPermissionEnum.DEPARTMENT.getValue()) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    map.put("existDepartment", true);
                    continue;
                }
            }

            if (o.getRoleType() == ItemPermissionEnum.POSITION.getValue()) {
                OrgUnit orgUnit = orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData();
                if (null != orgUnit) {
                    map.put("existPosition", true);
                    continue;
                }
            }
            if (o.getRoleType() == ItemPermissionEnum.ROLE.getValue()) {
                Integer positionSize = roleManager
                    .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.POSITION.getEnName()).getData().size();
                Integer departmentSize = roleManager
                    .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT.getEnName()).getData().size();
                Integer organizationSize = roleManager
                    .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION.getEnName()).getData().size();
                if (positionSize > 0) {
                    map.put("existPosition", true);
                }
                if (departmentSize > 0 || organizationSize > 0) {
                    map.put("existDepartment", true);
                }
            }
            if (o.getRoleType() == ItemPermissionEnum.DYNAMICROLE.getValue()) {
                List<OrgUnit> orgUnitList = dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                for (OrgUnit orgUnit : orgUnitList) {
                    if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION.getEnName())) {
                        map.put("existPosition", true);
                    }
                    if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT.getEnName())
                        || orgUnit.getOrgType().equals(OrgTypeEnum.ORGANIZATION.getEnName())) {
                        map.put("existDepartment", true);
                    }
                }
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getTabMap4Position(String itemId, String processDefinitionId, String taskDefKey,
        String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> objectPermList =
            findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
        Map<String, Object> map = new HashMap<String, Object>(16);
        boolean existDepartment = false;
        boolean existPosition = false;
        for (ItemPermission o : objectPermList) {
            if (existPosition && existDepartment) {
                break;
            }
            if (o.getRoleType() == ItemPermissionEnum.ROLE.getValue()) {
                Integer positionSize =
                    roleManager.listOrgUnitsById(tenantId, o.getRoleId(), "Position").getData().size();
                if (positionSize > 0) {
                    existPosition = true;
                }
                Integer departmentSize =
                    roleManager.listOrgUnitsById(tenantId, o.getRoleId(), "Department").getData().size();
                if (departmentSize > 0) {
                    existDepartment = true;
                }
                continue;
            }
            if (o.getRoleType() == ItemPermissionEnum.DYNAMICROLE.getValue()) {
                List<OrgUnit> orgUnitList = dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                for (OrgUnit orgUnit : orgUnitList) {
                    if (existPosition && existDepartment) {
                        break;
                    }
                    if (orgUnit.getOrgType().equals("Position")) {
                        existPosition = true;
                        continue;
                    }
                    if (orgUnit.getOrgType().equals("Department")) {
                        existDepartment = true;
                        continue;
                    }
                }
                continue;
            }
        }
        map.put("existDepartment", existDepartment);
        map.put("existPosition", existPosition);
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public void removePerm(String itemId, String processDefinitionId) {
        List<ItemPermission> ipList =
            itemPermissionRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        itemPermissionRepository.deleteAll(ipList);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemPermission save(String itemId, String processDefinitionId, String taskDefKey, String roleId,
        Integer roleType) {
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
