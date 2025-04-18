package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
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
import net.risesoft.entity.ItemWordConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemWordConfRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemWordConfService;
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
    private final SpmApproveItemRepository spmApproveItemRepository;
    private final RepositoryApi repositoryApi;
    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void bindRole(String id, String roleIds) {
        ItemWordConf itemWordConf = itemWordConfRepository.findById(id).orElse(null);
        if (itemWordConf != null) {
            if (StringUtils.isNotBlank(itemWordConf.getRoleIds())) {
                String newroleIds = itemWordConf.getRoleIds();
                for (String rid : roleIds.split(",")) {
                    if (!newroleIds.contains(rid)) {
                        newroleIds = Y9Util.genCustomStr(newroleIds, rid);
                    }
                }
                itemWordConf.setRoleIds(newroleIds);
            } else {
                itemWordConf.setRoleIds(roleIds);
            }
            itemWordConfRepository.save(itemWordConf);
        }
    }

    @Override
    @Transactional
    public void copyWordConf(String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
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
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            List<ItemWordConf> bindList = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                previouspdId, currentTaskDefKey);
            for (ItemWordConf bind : bindList) {
                ItemWordConf oldBind =
                    itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(itemId,
                        latestpdId, currentTaskDefKey, bind.getWordType());
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    ItemWordConf newbind = new ItemWordConf();
                    newbind.setId(newbindId);
                    newbind.setItemId(itemId);
                    newbind.setCreateTime(sdf.format(new Date()));
                    newbind.setWordType(bind.getWordType());
                    newbind.setProcessDefinitionId(latestpdId);
                    newbind.setTaskDefKey(currentTaskDefKey);
                    newbind.setRoleIds(bind.getRoleIds());
                    itemWordConfRepository.save(newbind);
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
            String roleIds = bind.getRoleIds();
            String roleNames = "";
            if (StringUtils.isNotBlank(roleIds)) {
                for (String roleId : roleIds.split(",")) {
                    Role r = roleApi.getRole(roleId).getData();
                    if (StringUtils.isEmpty(roleNames)) {
                        roleNames = null == r ? "角色不存在" : r.getName();
                    } else {
                        roleNames += "、" + (null == r ? "角色不存在" : r.getName());
                    }
                }
            }
            bind.setRoleNames(roleNames);
        }
        return bindList;
    }

    @Override
    @Transactional
    public void save(String wordType, String itemId, String processDefinitionId, String taskDefKey) {
        ItemWordConf itemWordConf = itemWordConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(
            itemId, processDefinitionId, taskDefKey, wordType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (itemWordConf == null) {
            itemWordConf = new ItemWordConf();
            itemWordConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            itemWordConf.setItemId(itemId);
            itemWordConf.setProcessDefinitionId(processDefinitionId);
            itemWordConf.setTaskDefKey(StringUtils.isBlank(taskDefKey) ? "" : taskDefKey);
        }
        itemWordConf.setWordType(wordType);
        itemWordConf.setCreateTime(sdf.format(new Date()));
        itemWordConfRepository.save(itemWordConf);
    }
}
