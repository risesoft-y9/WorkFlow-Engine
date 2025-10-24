package net.risesoft.service.config.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.documentword.ItemWordConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.documentword.ItemWordConfRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.config.ItemWordConfService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemWordConfServiceImpl implements ItemWordConfService {

    private final ItemWordConfRepository itemWordConfRepository;
    private final RoleApi roleApi;
    private final PositionRoleApi positionRoleApi;
    private final ItemRepository itemRepository;
    private final RepositoryApi repositoryApi;
    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void bindRole(String id, String roleIds) {
        ItemWordConf itemWordConf = itemWordConfRepository.findById(id).orElse(null);
        if (itemWordConf != null) {
            if (StringUtils.isNotBlank(itemWordConf.getRoleIds())) {
                String newRoleIds = itemWordConf.getRoleIds();
                for (String rid : roleIds.split(",")) {
                    if (!newRoleIds.contains(rid)) {
                        newRoleIds = Y9Util.genCustomStr(newRoleIds, rid);
                    }
                }
                itemWordConf.setRoleIds(newRoleIds);
            } else {
                itemWordConf.setRoleIds(roleIds);
            }
            itemWordConfRepository.save(itemWordConf);
        }
    }

    @Override
    @Transactional
    public void copyWordConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId=" + itemId + "事项";
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel latest = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestId = latest.getId();
        String previousId = processDefinitionId;
        if (processDefinitionId.equals(latestId)) {
            if (latest.getVersion() > 1) {
                ProcessDefinitionModel previous =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestId).getData();
                previousId = previous.getId();
            }
        }
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            List<ItemWordConf> bindList = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                previousId, currentTaskDefKey);
            for (ItemWordConf bind : bindList) {
                ItemWordConf oldBind =
                    itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(itemId, latestId,
                        currentTaskDefKey, bind.getWordType());
                if (null == oldBind) {
                    String newBindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    ItemWordConf newBind = new ItemWordConf();
                    newBind.setId(newBindId);
                    newBind.setItemId(itemId);
                    newBind.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
                    newBind.setWordType(bind.getWordType());
                    newBind.setProcessDefinitionId(latestId);
                    newBind.setTaskDefKey(currentTaskDefKey);
                    newBind.setRoleIds(bind.getRoleIds());
                    itemWordConfRepository.save(newBind);
                }
            }
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        itemWordConfRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteRole(String id, String roleId) {
        ItemWordConf itemWordConf = itemWordConfRepository.findById(id).orElse(null);
        if (itemWordConf != null) {
            if (StringUtils.isNotBlank(itemWordConf.getRoleIds())) {
                String roleIds = "";
                for (String rid : itemWordConf.getRoleIds().split(",")) {
                    if (!rid.equals(roleId)) {
                        roleIds = Y9Util.genCustomStr(roleIds, rid);
                    }
                }
                itemWordConf.setRoleIds(roleIds);
                itemWordConfRepository.save(itemWordConf);
            }
        }
    }

    @Override
    public Boolean getPermissionWord(String positionId, String itemId, String processDefinitionId, String taskDefKey,
        String wordType) {
        ItemWordConf itemWordConf = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(
            itemId, processDefinitionId, taskDefKey, wordType);
        if (itemWordConf == null) {// 为空则查找流程配置
            itemWordConf = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(itemId,
                processDefinitionId, "", wordType);
        }
        if (itemWordConf != null) {
            if (StringUtils.isBlank(itemWordConf.getRoleIds())) {
                return false;
            }
            for (String roleId : itemWordConf.getRoleIds().split(",")) {
                Boolean hasRole =
                    positionRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), roleId, positionId).getData();
                if (hasRole) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ItemWordConf> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        List<ItemWordConf> bindList = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        for (ItemWordConf bind : bindList) {
            String roleNames = getRoleNames(bind.getRoleIds());
            bind.setRoleNames(roleNames);
        }
        return bindList;
    }

    /**
     * 根据角色ID列表获取角色名称
     *
     * @param roleIds 角色ID字符串，以逗号分隔
     * @return 角色名称字符串，以"、"分隔
     */
    private String getRoleNames(String roleIds) {
        if (StringUtils.isBlank(roleIds)) {
            return "";
        }
        StringBuilder roleNamesBuilder = new StringBuilder();
        String[] roleIdArray = roleIds.split(",");
        for (int i = 0; i < roleIdArray.length; i++) {
            Role role = roleApi.getRole(roleIdArray[i]).getData();
            String roleName = (role == null) ? "角色不存在" : role.getName();
            if (i == 0) {
                roleNamesBuilder.append(roleName);
            } else {
                roleNamesBuilder.append("、").append(roleName);
            }
        }
        return roleNamesBuilder.toString();
    }

    @Override
    @Transactional
    public void save(String wordType, String itemId, String processDefinitionId, String taskDefKey) {
        ItemWordConf itemWordConf = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(
            itemId, processDefinitionId, taskDefKey, wordType);
        if (itemWordConf == null) {
            itemWordConf = new ItemWordConf();
            itemWordConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            itemWordConf.setItemId(itemId);
            itemWordConf.setProcessDefinitionId(processDefinitionId);
            itemWordConf.setTaskDefKey(StringUtils.isBlank(taskDefKey) ? "" : taskDefKey);
        }
        itemWordConf.setWordType(wordType);
        itemWordConf.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
        itemWordConfRepository.save(itemWordConf);
    }
}
