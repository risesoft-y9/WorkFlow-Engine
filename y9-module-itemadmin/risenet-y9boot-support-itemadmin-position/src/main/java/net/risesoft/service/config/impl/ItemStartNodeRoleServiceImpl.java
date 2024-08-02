package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemStartNodeRole;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemStartNodeRoleRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.util.SysVariables;
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
public class ItemStartNodeRoleServiceImpl implements ItemStartNodeRoleService {

    private final ItemStartNodeRoleRepository itemStartNodeRoleRepository;

    private final RoleApi roleManager;

    private final PositionRoleApi positionRoleApi;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final RepositoryApi repositoryManager;

    private final ProcessDefinitionApi processDefinitionManager;

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userName = person.getName();
        SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryManager.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                previouspdId = previouspd.getId();
            }
        }
        List<ItemStartNodeRole> isnrList =
            itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);

        String startNodeKey =
            processDefinitionManager.getStartNodeKeyByProcessDefinitionId(tenantId, latestpdId).getData();
        List<TargetModel> nodes = processDefinitionManager.getTargetNodes(tenantId, latestpdId, startNodeKey).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            for (ItemStartNodeRole isnr : isnrList) {
                if (currentTaskDefKey.equals(isnr.getTaskDefKey())) {
                    ItemStartNodeRole oldisnr = itemStartNodeRoleRepository
                        .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, latestpdId, currentTaskDefKey);
                    if (null == oldisnr) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        oldisnr = new ItemStartNodeRole();
                        oldisnr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        oldisnr.setItemId(itemId);
                        oldisnr.setProcessDefinitionId(latestpdId);
                        oldisnr.setTaskDefKey(currentTaskDefKey);
                        oldisnr.setRoleIds(isnr.getRoleIds());
                        oldisnr.setUserName(userName);
                        oldisnr.setCreateTime(sdf.format(new Date()));
                        Integer index = itemStartNodeRoleRepository.getMaxTabIndex(itemId, latestpdId);
                        if (index == null) {
                            oldisnr.setTabIndex(1);
                        } else {
                            oldisnr.setTabIndex(index + 1);
                        }
                        itemStartNodeRoleRepository.save(oldisnr);
                    } else {
                        String oldRoleIds = oldisnr.getRoleIds();
                        String newRoleIds = StringUtils.isNotBlank(isnr.getRoleIds()) ? isnr.getRoleIds() : "";
                        String[] newRoleIdArr = newRoleIds.split(";");
                        Role role = null;
                        for (String newRoleId : newRoleIdArr) {
                            if (StringUtils.isBlank(oldRoleIds)) {
                                oldRoleIds = newRoleId;
                            } else {
                                if (!oldRoleIds.contains(newRoleId)) {
                                    role = roleManager.getRole(newRoleId).getData();
                                    if (null != role) {
                                        oldRoleIds += ";" + newRoleId;
                                    }
                                }
                            }
                        }
                        oldisnr.setRoleIds(oldRoleIds);
                        itemStartNodeRoleRepository.save(oldisnr);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            itemStartNodeRoleRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除路由配置信息失败", e.getMessage());
        }
    }

    @Override
    public ItemStartNodeRole findById(String id) {
        return itemStartNodeRoleRepository.findById(id).orElse(null);
    }

    @Override
    public ItemStartNodeRole findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        return itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
            taskDefKey);
    }

    @Override
    public String getStartTaskDefKey(String itemId) {
        String startTaskDefKey = "", tenantId = Y9LoginUserHolder.getTenantId(),
            userId = Y9LoginUserHolder.getOrgUnitId();
        SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestpd =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = latestpd.getId();
        List<ItemStartNodeRole> list = itemStartNodeRoleRepository
            .findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(itemId, processDefinitionId);
        if (list.size() > 1) {
            list:
            for (ItemStartNodeRole isnr : list) {
                if (1 != isnr.getTabIndex()) {
                    String roleIds = isnr.getRoleIds();
                    if (StringUtils.isNotEmpty(roleIds)) {
                        String[] roleIdArr = roleIds.split(";");
                        for (String roleId : roleIdArr) {
                            boolean has = positionRoleApi.hasRole(tenantId, roleId, userId).getData();
                            if (has) {
                                startTaskDefKey = isnr.getTaskDefKey();
                                break list;
                            }
                        }
                    }
                } else {
                    startTaskDefKey = isnr.getTaskDefKey();
                    break;
                }
            }
        } else if (list.size() == 1) {
            startTaskDefKey = list.get(0).getTaskDefKey();
        } else {
            String startNodeKey =
                processDefinitionManager.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
            List<TargetModel> nodes =
                processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, startNodeKey).getData();
            startTaskDefKey = nodes.get(0).getTaskDefKey();
        }
        return startTaskDefKey;
    }

    @Override
    @Transactional
    public void initRole(String itemId, String processDefinitionId, String taskDefKey, String taskDefName) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole isnr =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null == isnr) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            isnr = new ItemStartNodeRole();
            isnr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            isnr.setItemId(itemId);
            isnr.setProcessDefinitionId(processDefinitionId);
            isnr.setTaskDefKey(taskDefKey);
            isnr.setTaskDefName(taskDefName);
            isnr.setRoleIds("");
            isnr.setUserName(userName);
            isnr.setCreateTime(sdf.format(new Date()));
            Integer index = itemStartNodeRoleRepository.getMaxTabIndex(itemId, processDefinitionId);
            if (index == null) {
                isnr.setTabIndex(1);
            } else {
                isnr.setTabIndex(index + 1);
            }
            itemStartNodeRoleRepository.save(isnr);
        }
    }

    @Override
    public List<ItemStartNodeRole> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(itemId,
            processDefinitionId);
    }

    @Override
    public List<Role> listRoleByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        List<Role> list = new ArrayList<>();
        ItemStartNodeRole isnr =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != isnr && StringUtils.isNotEmpty(isnr.getRoleIds())) {
            String roleIds = isnr.getRoleIds();
            String[] roleIdArr = roleIds.split(";");
            Role role = null;
            for (String roleId : roleIdArr) {
                role = roleManager.getRole(roleId).getData();
                if (null != role) {
                    list.add(role);
                } else {
                    this.removeRole(itemId, processDefinitionId, taskDefKey, roleId);
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void removeRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole isnr =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != isnr) {
            String oldRoleIds = isnr.getRoleIds();
            String[] oldRoleIdArr = oldRoleIds.split(";");
            String[] roleIdArr = roleIds.split(",");
            String newRoleIds = "";
            for (String oldRoleId : oldRoleIdArr) {
                boolean deleted = false;
                for (String roleId : roleIdArr) {
                    if (oldRoleId.equals(roleId)) {
                        deleted = true;
                        break;
                    }
                }
                if (!deleted) {
                    if (StringUtils.isEmpty(newRoleIds)) {
                        newRoleIds = oldRoleId;
                    } else {
                        newRoleIds += ";" + oldRoleId;
                    }
                }
            }
            isnr.setRoleIds(newRoleIds);
            isnr.setUserName(userName);
            itemStartNodeRoleRepository.save(isnr);
        }
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        List<ItemStartNodeRole> oldtibList = new ArrayList<>();
        for (String idAndTabIndex : idAndTabIndexs) {
            String[] arr = idAndTabIndex.split(SysVariables.COLON);
            ItemStartNodeRole oldisnr = this.findById(arr[0]);
            oldisnr.setTabIndex(Integer.valueOf(arr[1]));
            oldisnr.setUserName(userName);
            oldtibList.add(oldisnr);
        }
        itemStartNodeRoleRepository.saveAll(oldtibList);
    }

    @Override
    @Transactional
    public void saveRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole isnr =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != isnr) {
            String oldRoleIds = isnr.getRoleIds();
            String[] roleIdArr = roleIds.split(";");
            for (String roleId : roleIdArr) {
                if (StringUtils.isEmpty(oldRoleIds)) {
                    oldRoleIds = roleId;
                } else {
                    if (!oldRoleIds.contains(roleId)) {
                        oldRoleIds += ";" + roleId;
                    }
                }
            }
            isnr.setRoleIds(oldRoleIds);
            isnr.setUserName(userName);

            itemStartNodeRoleRepository.save(isnr);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        isnr = new ItemStartNodeRole();
        isnr.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        isnr.setItemId(itemId);
        isnr.setProcessDefinitionId(processDefinitionId);
        isnr.setTaskDefKey(taskDefKey);
        isnr.setRoleIds(roleIds);
        isnr.setUserName(userName);
        isnr.setCreateTime(sdf.format(new Date()));
        Integer index = itemStartNodeRoleRepository.getMaxTabIndex(itemId, processDefinitionId);
        if (index == null) {
            isnr.setTabIndex(1);
        } else {
            isnr.setTabIndex(index + 1);
        }
        itemStartNodeRoleRepository.save(isnr);
    }
}