package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.organword.ItemOrganWordBind;
import net.risesoft.entity.organword.ItemOrganWordRole;
import net.risesoft.entity.organword.OrganWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.repository.organword.ItemOrganWordBindRepository;
import net.risesoft.repository.organword.OrganWordRepository;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.service.config.ItemOrganWordRoleService;
import net.risesoft.util.Y9DateTimeUtils;
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
public class ItemOrganWordBindServiceImpl implements ItemOrganWordBindService {

    private final ItemOrganWordBindRepository itemOrganWordBindRepository;

    private final ItemOrganWordRoleService itemOrganWordRoleService;

    private final OrganWordRepository organWordRepository;

    private final ItemRepository itemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final RoleApi roleApi;

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = person.getPersonId();
        String userName = person.getName();
        Item item = itemRepository.findById(itemId).orElse(null);
        assert item != null : "不存在itemId=" + itemId + "事项";
        // 获取最新和前一版本的流程定义
        ProcessDefinitionModel latestProcessDefinition = getLatestProcessDefinition(tenantId, item);
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        String previousProcessDefinitionId =
            getPreviousProcessDefinitionId(tenantId, processDefinitionId, latestProcessDefinition);
        // 获取流程节点并复制绑定信息
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyOrganWordBindingsForNode(itemId, userId, userName, latestProcessDefinitionId,
                previousProcessDefinitionId, currentTaskDefKey);
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
     * 为指定节点复制编号绑定信息
     */
    private void copyOrganWordBindingsForNode(String itemId, String userId, String userName,
        String latestProcessDefinitionId, String previousProcessDefinitionId, String currentTaskDefKey) {
        List<ItemOrganWordBind> bindList = itemOrganWordBindRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, previousProcessDefinitionId, currentTaskDefKey);
        for (ItemOrganWordBind bind : bindList) {
            ItemOrganWordBind existingBind =
                itemOrganWordBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                    latestProcessDefinitionId, currentTaskDefKey, bind.getOrganWordCustom());
            // 如果不存在，则创建新的绑定
            if (null == existingBind) {
                createNewOrganWordBinding(itemId, userId, userName, latestProcessDefinitionId, currentTaskDefKey, bind);
            }
        }
    }

    /**
     * 创建新的编号绑定
     */
    private void createNewOrganWordBinding(String itemId, String userId, String userName, String processDefinitionId,
        String taskDefKey, ItemOrganWordBind sourceBind) {

        String newBindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        String sourceBindId = sourceBind.getId();

        // 创建新的编号绑定
        ItemOrganWordBind newBind =
            createOrganWordBind(newBindId, itemId, userId, userName, processDefinitionId, taskDefKey, sourceBind);
        itemOrganWordBindRepository.save(newBind);

        // 复制编号授权信息
        copyOrganWordRoles(sourceBindId, newBindId);
    }

    /**
     * 创建编号绑定对象
     */
    private ItemOrganWordBind createOrganWordBind(String bindId, String itemId, String userId, String userName,
        String processDefinitionId, String taskDefKey, ItemOrganWordBind sourceBind) {

        ItemOrganWordBind newBind = new ItemOrganWordBind();
        newBind.setId(bindId);
        newBind.setItemId(itemId);
        newBind.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
        newBind.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
        newBind.setOrganWordCustom(sourceBind.getOrganWordCustom());
        newBind.setProcessDefinitionId(processDefinitionId);
        newBind.setTaskDefKey(taskDefKey);
        newBind.setUserId(userId);
        newBind.setUserName(userName);

        return newBind;
    }

    /**
     * 复制编号角色授权信息
     */
    private void copyOrganWordRoles(String sourceBindId, String targetBindId) {
        List<ItemOrganWordRole> roleList = itemOrganWordRoleService.listByItemOrganWordBindId(sourceBindId);
        for (ItemOrganWordRole role : roleList) {
            itemOrganWordRoleService.saveOrUpdate(targetBindId, role.getRoleId());
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        List<ItemOrganWordBind> bindList =
            itemOrganWordBindRepository.findByItemIdAndProcessDefinitionId(itemId, lastVersionPid);
        for (ItemOrganWordBind bind : bindList) {
            ItemOrganWordBind itemOrganWordBind = new ItemOrganWordBind();
            itemOrganWordBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            itemOrganWordBind.setItemId(newItemId);
            itemOrganWordBind.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
            itemOrganWordBind.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            itemOrganWordBind.setOrganWordCustom(bind.getOrganWordCustom());
            itemOrganWordBind.setProcessDefinitionId(lastVersionPid);
            itemOrganWordBind.setTaskDefKey(bind.getTaskDefKey());
            itemOrganWordBind.setUserId(userId);
            itemOrganWordBind.setUserName(userName);
            itemOrganWordBindRepository.save(itemOrganWordBind);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            List<ItemOrganWordBind> bindList = itemOrganWordBindRepository.findByItemId(itemId);
            if (null != bindList && !bindList.isEmpty()) {
                for (ItemOrganWordBind bind : bindList) {
                    itemOrganWordRoleService.removeByItemOrganWordBindId(bind.getId());
                    itemOrganWordBindRepository.deleteById(bind.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("删除编号绑定关系失败", e);
        }
    }

    @Override
    public ItemOrganWordBind findById(String id) {
        return itemOrganWordBindRepository.findById(id).orElse(null);
    }

    @Override
    public ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId,
        String processDefinitionId, String taskDefKey, String custom) {
        ItemOrganWordBind bind =
            itemOrganWordBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                processDefinitionId, taskDefKey, custom);
        if (null != bind) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOrganWordRole> roleList = itemOrganWordRoleService.listByItemOrganWordBindId(bind.getId());
            for (ItemOrganWordRole role : roleList) {
                roleIds.add(role.getRoleId());
            }
            bind.setRoleIds(roleIds);
        }
        return bind;
    }

    @Override
    public List<ItemOrganWordBind> listByItemId(String itemId) {
        return itemOrganWordBindRepository.findByItemId(itemId);
    }

    @Override
    public List<ItemOrganWordBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        List<ItemOrganWordBind> owtrbList =
            itemOrganWordBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        OrganWord organWord;
        for (ItemOrganWordBind bind : owtrbList) {
            String custom = bind.getOrganWordCustom();
            organWord = organWordRepository.findByCustom(custom);
            bind.setOrganWordName(organWord == null ? custom + "对应的编号不存在" : organWord.getName());

            List<ItemOrganWordRole> roleList =
                itemOrganWordRoleService.listByItemOrganWordBindIdContainRoleName(bind.getId());
            List<String> roleIds = new ArrayList<>();
            String roleNames = "";
            for (ItemOrganWordRole role : roleList) {
                roleIds.add(role.getRoleId());
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getRoleName();
                } else {
                    roleNames += "、" + role.getRoleName();
                }
            }
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);

        }
        return owtrbList;
    }

    @Override
    public List<ItemOrganWordBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemOrganWordBind> result = itemOrganWordBindRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        for (ItemOrganWordBind bind : result) {
            // 设置编号名称
            String custom = bind.getOrganWordCustom();
            OrganWord organWord = organWordRepository.findByCustom(custom);
            bind.setOrganWordName(organWord == null ? custom + "对应的编号不存在" : organWord.getName());
            // 设置角色信息
            RoleInfo roleInfo = getRoleInfo(bind.getId());
            bind.setRoleIds(roleInfo.getRoleIds());
            bind.setRoleNames(roleInfo.getRoleNames());
        }
        return result;
    }

    /**
     * 获取编号绑定的角色信息
     */
    private RoleInfo getRoleInfo(String itemOrganWordBindId) {
        List<ItemOrganWordRole> roleList = itemOrganWordRoleService.listByItemOrganWordBindId(itemOrganWordBindId);
        List<String> roleIds = new ArrayList<>();
        StringBuilder roleNamesBuilder = new StringBuilder();
        for (int i = 0; i < roleList.size(); i++) {
            ItemOrganWordRole role = roleList.get(i);
            roleIds.add(role.getId());
            Role r = roleApi.getRole(role.getRoleId()).getData();
            String roleName = (r == null) ? "角色不存在" : r.getName();
            if (i == 0) {
                roleNamesBuilder.append(roleName);
            } else {
                roleNamesBuilder.append("、").append(roleName);
            }
        }

        return new RoleInfo(roleIds, roleNamesBuilder.toString());
    }

    @Override
    @Transactional
    public void remove(String id) {
        itemOrganWordRoleService.removeByItemOrganWordBindId(id);
        itemOrganWordBindRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            itemOrganWordRoleService.removeByItemOrganWordBindId(id);
            itemOrganWordBindRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void save(ItemOrganWordBind taskRoleBind) {
        itemOrganWordBindRepository.save(taskRoleBind);
    }

    @Override
    @Transactional
    public void save(String id, String name, String custom) {
        ItemOrganWordBind taskRoleBind = this.findById(id);
        taskRoleBind.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
        taskRoleBind.setOrganWordCustom(custom);
        itemOrganWordBindRepository.save(taskRoleBind);
    }

    @Override
    @Transactional
    public void save(String custom, String itemId, String processDefinitionId, String taskDefKey) {
        ItemOrganWordBind bind = this.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
            processDefinitionId, taskDefKey, custom);
        if (null == bind) {
            ItemOrganWordBind taskRoleBind = new ItemOrganWordBind();
            taskRoleBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            taskRoleBind.setItemId(itemId);
            taskRoleBind.setOrganWordCustom(custom);
            taskRoleBind.setProcessDefinitionId(processDefinitionId);
            taskRoleBind.setTaskDefKey(taskDefKey);
            taskRoleBind.setUserId(Y9LoginUserHolder.getPersonId());
            taskRoleBind.setUserName(Y9LoginUserHolder.getUserInfo().getName());
            taskRoleBind.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
            taskRoleBind.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            itemOrganWordBindRepository.save(taskRoleBind);
        } else {
            bind.setOrganWordCustom(custom);
            bind.setProcessDefinitionId(processDefinitionId);
            bind.setTaskDefKey(taskDefKey);
            bind.setUserId(Y9LoginUserHolder.getPersonId());
            bind.setUserName(Y9LoginUserHolder.getUserInfo().getName());
            bind.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            itemOrganWordBindRepository.save(bind);
        }
    }

    /**
     * 角色信息封装类
     */
    @Getter
    private static class RoleInfo {
        private final List<String> roleIds;
        private final String roleNames;

        public RoleInfo(List<String> roleIds, String roleNames) {
            this.roleIds = roleIds;
            this.roleNames = roleNames;
        }

    }
}
