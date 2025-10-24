package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemStartNodeRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.repository.jpa.ItemStartNodeRoleRepository;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemStartNodeRoleServiceImpl implements ItemStartNodeRoleService {

    private final ItemStartNodeRoleRepository itemStartNodeRoleRepository;

    private final RoleApi roleApi;

    private final PositionRoleApi positionRoleApi;

    private final ItemRepository itemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ItemStartNodeRoleService self;

    public ItemStartNodeRoleServiceImpl(
        ItemStartNodeRoleRepository itemStartNodeRoleRepository,
        RoleApi roleApi,
        PositionRoleApi positionRoleApi,
        ItemRepository itemRepository,
        RepositoryApi repositoryApi,
        ProcessDefinitionApi processDefinitionApi,
        @Lazy ItemStartNodeRoleService self) {
        this.itemStartNodeRoleRepository = itemStartNodeRoleRepository;
        this.roleApi = roleApi;
        this.positionRoleApi = positionRoleApi;
        this.itemRepository = itemRepository;
        this.repositoryApi = repositoryApi;
        this.processDefinitionApi = processDefinitionApi;
        this.self = self;
    }

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userName = person.getName();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在数据itemId=" + itemId + "事项";
        // 获取最新和前一版本的流程定义
        ProcessDefinitionModel latestProcessDefinition = getLatestProcessDefinition(tenantId, item);
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        String previousProcessDefinitionId =
            getPreviousProcessDefinitionId(tenantId, processDefinitionId, latestProcessDefinition);
        // 获取前一版本的起始节点角色列表
        List<ItemStartNodeRole> previousStartNodeRoles =
            itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionId(itemId, previousProcessDefinitionId);
        // 获取起始节点和目标节点
        String startNodeKey =
            processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, latestProcessDefinitionId).getData();
        List<TargetModel> targetNodes =
            processDefinitionApi.getTargetNodes(tenantId, latestProcessDefinitionId, startNodeKey).getData();
        // 为每个目标节点复制绑定信息
        for (TargetModel targetModel : targetNodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyStartNodeRoleForTask(itemId, latestProcessDefinitionId, currentTaskDefKey, previousStartNodeRoles,
                userName);
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
     * 为指定任务节点复制起始节点角色配置
     */
    private void copyStartNodeRoleForTask(String itemId, String latestProcessDefinitionId, String currentTaskDefKey,
        List<ItemStartNodeRole> previousStartNodeRoles, String userName) {
        for (ItemStartNodeRole startNodeRole : previousStartNodeRoles) {
            if (currentTaskDefKey.equals(startNodeRole.getTaskDefKey())) {
                ItemStartNodeRole existingStartNodeRole =
                    itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                        latestProcessDefinitionId, currentTaskDefKey);
                if (null == existingStartNodeRole) {
                    createNewStartNodeRole(itemId, latestProcessDefinitionId, currentTaskDefKey, startNodeRole,
                        userName);
                } else {
                    mergeStartNodeRole(existingStartNodeRole, startNodeRole);
                }
            }
        }
    }

    /**
     * 创建新的起始节点角色配置
     */
    private void createNewStartNodeRole(String itemId, String processDefinitionId, String taskDefKey,
        ItemStartNodeRole sourceStartNodeRole, String userName) {
        ItemStartNodeRole newStartNodeRole = new ItemStartNodeRole();
        newStartNodeRole.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newStartNodeRole.setItemId(itemId);
        newStartNodeRole.setProcessDefinitionId(processDefinitionId);
        newStartNodeRole.setTaskDefKey(taskDefKey);
        newStartNodeRole.setRoleIds(sourceStartNodeRole.getRoleIds());
        newStartNodeRole.setUserName(userName);
        newStartNodeRole.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        Integer maxTabIndex = itemStartNodeRoleRepository.getMaxTabIndex(itemId, processDefinitionId);
        newStartNodeRole.setTabIndex(maxTabIndex == null ? 1 : maxTabIndex + 1);
        itemStartNodeRoleRepository.save(newStartNodeRole);
    }

    /**
     * 合并起始节点角色配置
     */
    private void mergeStartNodeRole(ItemStartNodeRole existingStartNodeRole, ItemStartNodeRole sourceStartNodeRole) {
        String existingRoleIds = existingStartNodeRole.getRoleIds();
        String sourceRoleIds =
            StringUtils.isNotBlank(sourceStartNodeRole.getRoleIds()) ? sourceStartNodeRole.getRoleIds() : "";
        String[] sourceRoleIdArray = sourceRoleIds.split(";");
        for (String sourceRoleId : sourceRoleIdArray) {
            if (StringUtils.isBlank(existingRoleIds)) {
                existingRoleIds = sourceRoleId;
            } else {
                if (!existingRoleIds.contains(sourceRoleId)) {
                    Role role = roleApi.getRole(sourceRoleId).getData();
                    if (null != role) {
                        existingRoleIds += ";" + sourceRoleId;
                    }
                }
            }
        }
        existingStartNodeRole.setRoleIds(existingRoleIds);
        itemStartNodeRoleRepository.save(existingStartNodeRole);
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            itemStartNodeRoleRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除路由配置信息失败{}", e.getMessage());
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId：" + itemId + "事项";
        // 获取最新流程定义
        ProcessDefinitionModel latestProcessDefinition = getLatestProcessDefinition(tenantId, item);
        String processDefinitionId = latestProcessDefinition.getId();
        // 获取起始节点角色列表
        List<ItemStartNodeRole> startNodeRoles = itemStartNodeRoleRepository
            .findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(itemId, processDefinitionId);
        // 根据不同情况确定起始任务定义键
        if (startNodeRoles.size() > 1) {
            return getStartTaskDefKeyForMultipleRoles(startNodeRoles, tenantId, userId);
        } else if (startNodeRoles.size() == 1) {
            return startNodeRoles.get(0).getTaskDefKey();
        } else {
            return getStartTaskDefKeyFromProcessDefinition(tenantId, processDefinitionId);
        }
    }

    /**
     * 处理多个起始节点角色的情况
     */
    private String getStartTaskDefKeyForMultipleRoles(List<ItemStartNodeRole> startNodeRoles, String tenantId,
        String userId) {
        for (ItemStartNodeRole itemStartNodeRole : startNodeRoles) {
            if (1 != itemStartNodeRole.getTabIndex()) {
                String roleIds = itemStartNodeRole.getRoleIds();
                if (StringUtils.isNotEmpty(roleIds)) {
                    String[] roleIdArr = roleIds.split(";");
                    for (String roleId : roleIdArr) {
                        boolean has = positionRoleApi.hasRole(tenantId, roleId, userId).getData();
                        if (has) {
                            return itemStartNodeRole.getTaskDefKey();
                        }
                    }
                }
            } else {
                return itemStartNodeRole.getTaskDefKey();
            }
        }
        return startNodeRoles.get(0).getTaskDefKey();
    }

    /**
     * 从流程定义获取起始任务定义键
     */
    private String getStartTaskDefKeyFromProcessDefinition(String tenantId, String processDefinitionId) {
        String startNodeKey =
            processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
        List<TargetModel> nodes =
            processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNodeKey).getData();
        return nodes.get(0).getTaskDefKey();
    }

    @Override
    public List<ItemStartNodeRoleModel> getAllStartTaskDefKey(String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId=" + itemId + "事项";
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestPd =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = latestPd.getId();
        List<ItemStartNodeRole> list = itemStartNodeRoleRepository
            .findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(itemId, processDefinitionId);
        List<ItemStartNodeRoleModel> itemStartNodeRoleModelList = new ArrayList<>();
        ItemStartNodeRoleModel itemStartNodeRoleModel;
        for (ItemStartNodeRole itemStartNodeRole : list) {
            String roleIds = itemStartNodeRole.getRoleIds();
            if (StringUtils.isEmpty(roleIds)) {
                itemStartNodeRoleModel = new ItemStartNodeRoleModel();
                Y9BeanUtil.copyProperties(itemStartNodeRole, itemStartNodeRoleModel);
                itemStartNodeRoleModelList.add(itemStartNodeRoleModel);
            } else {
                String[] roleIdArr = roleIds.split(";");
                for (String roleId : roleIdArr) {
                    boolean has = positionRoleApi.hasRole(tenantId, roleId, userId).getData();
                    if (has) {
                        itemStartNodeRoleModel = new ItemStartNodeRoleModel();
                        Y9BeanUtil.copyProperties(itemStartNodeRole, itemStartNodeRoleModel);
                        itemStartNodeRoleModelList.add(itemStartNodeRoleModel);
                    }
                }
            }
        }

        return itemStartNodeRoleModelList;
    }

    @Override
    @Transactional
    public void initRole(String itemId, String processDefinitionId, String taskDefKey, String taskDefName) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole itemStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null == itemStartNodeRole) {
            itemStartNodeRole = new ItemStartNodeRole();
            itemStartNodeRole.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            itemStartNodeRole.setItemId(itemId);
            itemStartNodeRole.setProcessDefinitionId(processDefinitionId);
            itemStartNodeRole.setTaskDefKey(taskDefKey);
            itemStartNodeRole.setTaskDefName(taskDefName);
            itemStartNodeRole.setRoleIds("");
            itemStartNodeRole.setUserName(userName);
            itemStartNodeRole.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            Integer index = itemStartNodeRoleRepository.getMaxTabIndex(itemId, processDefinitionId);
            if (index == null) {
                itemStartNodeRole.setTabIndex(1);
            } else {
                itemStartNodeRole.setTabIndex(index + 1);
            }
            itemStartNodeRoleRepository.save(itemStartNodeRole);
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
        ItemStartNodeRole itemStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != itemStartNodeRole && StringUtils.isNotEmpty(itemStartNodeRole.getRoleIds())) {
            String roleIds = itemStartNodeRole.getRoleIds();
            String[] roleIdArr = roleIds.split(";");
            Role role;
            for (String roleId : roleIdArr) {
                role = roleApi.getRole(roleId).getData();
                if (null != role) {
                    list.add(role);
                } else {
                    self.removeRole(itemId, processDefinitionId, taskDefKey, roleId);
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void removeRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds) {
        UserInfo currentUser = Y9LoginUserHolder.getUserInfo();
        String userName = currentUser.getName();
        ItemStartNodeRole itemStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != itemStartNodeRole) {
            String filteredRoleIds = filterRoleIds(itemStartNodeRole.getRoleIds(), roleIds);
            itemStartNodeRole.setRoleIds(filteredRoleIds);
            itemStartNodeRole.setUserName(userName);
            itemStartNodeRoleRepository.save(itemStartNodeRole);
        }
    }

    /**
     * 过滤需要删除的角色ID
     *
     * @param originalRoleIds 原始角色ID字符串
     * @param roleIdsToRemove 需要删除的角色ID字符串
     * @return 过滤后的角色ID字符串
     */
    private String filterRoleIds(String originalRoleIds, String roleIdsToRemove) {
        if (StringUtils.isEmpty(originalRoleIds)) {
            return "";
        }
        String[] originalRoleIdArray = originalRoleIds.split(";");
        String[] roleIdToRemoveArray = roleIdsToRemove.split(",");
        StringBuilder filteredRoleIdsBuilder = new StringBuilder();
        for (String originalRoleId : originalRoleIdArray) {
            boolean needToRemove = false;
            for (String roleIdToRemove : roleIdToRemoveArray) {
                if (originalRoleId.equals(roleIdToRemove)) {
                    needToRemove = true;
                    break;
                }
            }
            // 如果不需要删除，则保留在结果中
            if (!needToRemove) {
                if (filteredRoleIdsBuilder.length() > 0) {
                    filteredRoleIdsBuilder.append(";");
                }
                filteredRoleIdsBuilder.append(originalRoleId);
            }
        }
        return filteredRoleIdsBuilder.toString();
    }

    @Override
    @Transactional
    public void saveOrder(String[] idAndTabIndexs) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        List<ItemStartNodeRole> oldtibList = new ArrayList<>();
        for (String idAndTabIndex : idAndTabIndexs) {
            String[] arr = idAndTabIndex.split(SysVariables.COLON);
            ItemStartNodeRole oldItemStartNodeRole = this.findById(arr[0]);
            oldItemStartNodeRole.setTabIndex(Integer.valueOf(arr[1]));
            oldItemStartNodeRole.setUserName(userName);
            oldtibList.add(oldItemStartNodeRole);
        }
        itemStartNodeRoleRepository.saveAll(oldtibList);
    }

    @Override
    @Transactional
    public void saveRole(String itemId, String processDefinitionId, String taskDefKey, String roleIds) {
        UserInfo currentUser = Y9LoginUserHolder.getUserInfo();
        String userName = currentUser.getName();
        ItemStartNodeRole existingStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != existingStartNodeRole) {
            updateExistingStartNodeRole(existingStartNodeRole, roleIds, userName);
            itemStartNodeRoleRepository.save(existingStartNodeRole);
        } else {
            ItemStartNodeRole newStartNodeRole =
                createNewStartNodeRole(itemId, processDefinitionId, taskDefKey, roleIds, userName);
            itemStartNodeRoleRepository.save(newStartNodeRole);
        }
    }

    /**
     * 更新现有的起始节点角色配置
     */
    private void updateExistingStartNodeRole(ItemStartNodeRole existingStartNodeRole, String newRoleIds,
        String userName) {
        String currentRoleIds = existingStartNodeRole.getRoleIds();
        String[] newRoleIdArray = newRoleIds.split(";");
        for (String roleId : newRoleIdArray) {
            if (StringUtils.isEmpty(currentRoleIds)) {
                currentRoleIds = roleId;
            } else {
                if (!currentRoleIds.contains(roleId)) {
                    currentRoleIds += ";" + roleId;
                }
            }
        }
        existingStartNodeRole.setRoleIds(currentRoleIds);
        existingStartNodeRole.setUserName(userName);
    }

    /**
     * 创建新的起始节点角色配置
     */
    private ItemStartNodeRole createNewStartNodeRole(String itemId, String processDefinitionId, String taskDefKey,
        String roleIds, String userName) {
        ItemStartNodeRole newStartNodeRole = new ItemStartNodeRole();
        newStartNodeRole.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newStartNodeRole.setItemId(itemId);
        newStartNodeRole.setProcessDefinitionId(processDefinitionId);
        newStartNodeRole.setTaskDefKey(taskDefKey);
        newStartNodeRole.setRoleIds(roleIds);
        newStartNodeRole.setUserName(userName);
        newStartNodeRole.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        Integer maxTabIndex = itemStartNodeRoleRepository.getMaxTabIndex(itemId, processDefinitionId);
        newStartNodeRole.setTabIndex(maxTabIndex == null ? 1 : maxTabIndex + 1);
        return newStartNodeRole;
    }
}