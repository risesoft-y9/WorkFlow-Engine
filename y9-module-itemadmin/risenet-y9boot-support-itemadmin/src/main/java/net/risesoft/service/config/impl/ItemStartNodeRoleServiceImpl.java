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
        String tenantId = Y9LoginUserHolder.getTenantId(), userName = person.getName();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId=" + itemId + "事项";
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestPd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestPdId = latestPd.getId();
        String previousId = processDefinitionId;
        if (processDefinitionId.equals(latestPdId)) {
            if (latestPd.getVersion() > 1) {
                ProcessDefinitionModel previousPd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestPdId).getData();
                previousId = previousPd.getId();
            }
        }
        List<ItemStartNodeRole> isnrList =
            itemStartNodeRoleRepository.findByItemIdAndProcessDefinitionId(itemId, previousId);

        String startNodeKey = processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, latestPdId).getData();
        List<TargetModel> nodes = processDefinitionApi.getTargetNodes(tenantId, latestPdId, startNodeKey).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            for (ItemStartNodeRole ItemStartNodeRole : isnrList) {
                if (currentTaskDefKey.equals(ItemStartNodeRole.getTaskDefKey())) {
                    ItemStartNodeRole oldItemStartNodeRole = itemStartNodeRoleRepository
                        .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, latestPdId, currentTaskDefKey);
                    if (null == oldItemStartNodeRole) {
                        oldItemStartNodeRole = new ItemStartNodeRole();
                        oldItemStartNodeRole.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        oldItemStartNodeRole.setItemId(itemId);
                        oldItemStartNodeRole.setProcessDefinitionId(latestPdId);
                        oldItemStartNodeRole.setTaskDefKey(currentTaskDefKey);
                        oldItemStartNodeRole.setRoleIds(ItemStartNodeRole.getRoleIds());
                        oldItemStartNodeRole.setUserName(userName);
                        oldItemStartNodeRole.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                        Integer index = itemStartNodeRoleRepository.getMaxTabIndex(itemId, latestPdId);
                        if (index == null) {
                            oldItemStartNodeRole.setTabIndex(1);
                        } else {
                            oldItemStartNodeRole.setTabIndex(index + 1);
                        }
                        itemStartNodeRoleRepository.save(oldItemStartNodeRole);
                    } else {
                        String oldRoleIds = oldItemStartNodeRole.getRoleIds();
                        String newRoleIds = StringUtils.isNotBlank(ItemStartNodeRole.getRoleIds())
                            ? ItemStartNodeRole.getRoleIds() : "";
                        String[] newRoleIdArr = newRoleIds.split(";");
                        Role role;
                        for (String newRoleId : newRoleIdArr) {
                            if (StringUtils.isBlank(oldRoleIds)) {
                                oldRoleIds = newRoleId;
                            } else {
                                if (!oldRoleIds.contains(newRoleId)) {
                                    role = roleApi.getRole(newRoleId).getData();
                                    if (null != role) {
                                        oldRoleIds += ";" + newRoleId;
                                    }
                                }
                            }
                        }
                        oldItemStartNodeRole.setRoleIds(oldRoleIds);
                        itemStartNodeRoleRepository.save(oldItemStartNodeRole);
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
        String startTaskDefKey = "", tenantId = Y9LoginUserHolder.getTenantId(),
            userId = Y9LoginUserHolder.getOrgUnitId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId=" + itemId + "事项";
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel latestPd =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = latestPd.getId();
        List<ItemStartNodeRole> list = itemStartNodeRoleRepository
            .findByItemIdAndProcessDefinitionIdOrderByTabIndexDesc(itemId, processDefinitionId);
        if (list.size() > 1) {
            list:
            for (ItemStartNodeRole itemStartNodeRole : list) {
                if (1 != itemStartNodeRole.getTabIndex()) {
                    String roleIds = itemStartNodeRole.getRoleIds();
                    if (StringUtils.isNotEmpty(roleIds)) {
                        String[] roleIdArr = roleIds.split(";");
                        for (String roleId : roleIdArr) {
                            boolean has = positionRoleApi.hasRole(tenantId, roleId, userId).getData();
                            if (has) {
                                startTaskDefKey = itemStartNodeRole.getTaskDefKey();
                                break list;
                            }
                        }
                    }
                } else {
                    startTaskDefKey = itemStartNodeRole.getTaskDefKey();
                    break;
                }
            }
        } else if (list.size() == 1) {
            startTaskDefKey = list.get(0).getTaskDefKey();
        } else {
            String startNodeKey =
                processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
            List<TargetModel> nodes =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNodeKey).getData();
            startTaskDefKey = nodes.get(0).getTaskDefKey();
        }
        return startTaskDefKey;
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
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole itemStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != itemStartNodeRole) {
            String oldRoleIds = itemStartNodeRole.getRoleIds();
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
            itemStartNodeRole.setRoleIds(newRoleIds);
            itemStartNodeRole.setUserName(userName);
            itemStartNodeRoleRepository.save(itemStartNodeRole);
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
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userName = person.getName();
        ItemStartNodeRole itemStartNodeRole =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        if (null != itemStartNodeRole) {
            String oldRoleIds = itemStartNodeRole.getRoleIds();
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
            itemStartNodeRole.setRoleIds(oldRoleIds);
            itemStartNodeRole.setUserName(userName);

            itemStartNodeRoleRepository.save(itemStartNodeRole);
            return;
        }
        itemStartNodeRole = new ItemStartNodeRole();
        itemStartNodeRole.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        itemStartNodeRole.setItemId(itemId);
        itemStartNodeRole.setProcessDefinitionId(processDefinitionId);
        itemStartNodeRole.setTaskDefKey(taskDefKey);
        itemStartNodeRole.setRoleIds(roleIds);
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