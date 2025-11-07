package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.ItemOpinionFrameRole;
import net.risesoft.entity.opinion.OpinionFrameOneClickSet;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.repository.opinion.ItemOpinionFrameBindRepository;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.config.ItemOpinionFrameRoleService;
import net.risesoft.service.opinion.OpinionFrameOneClickSetService;
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
public class ItemOpinionFrameBindServiceImpl implements ItemOpinionFrameBindService {

    private final ItemOpinionFrameBindRepository itemOpinionFrameBindRepository;

    private final ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    private final RoleApi roleApi;

    private final ItemRepository itemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final OpinionFrameOneClickSetService opinionFrameOneClickSetService;

    @Override
    @Transactional
    public void changeSignOpinion(String id, Boolean signOpinion) {
        ItemOpinionFrameBind itemOpinionFrameBind = this.getById(id);
        if (null != itemOpinionFrameBind) {
            itemOpinionFrameBind.setSignOpinion(signOpinion);
            itemOpinionFrameBindRepository.save(itemOpinionFrameBind);
        }
    }

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
            copyOpinionFrameBindingsForNode(itemId, tenantId, userId, userName, latestProcessDefinitionId,
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
     * 为指定节点复制意见框绑定信息
     */
    private void copyOpinionFrameBindingsForNode(String itemId, String tenantId, String userId, String userName,
        String latestProcessDefinitionId, String previousProcessDefinitionId, String currentTaskDefKey) {
        List<ItemOpinionFrameBind> bindList =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateTimeAsc(itemId,
                previousProcessDefinitionId, currentTaskDefKey);
        for (ItemOpinionFrameBind bind : bindList) {
            ItemOpinionFrameBind existingBind =
                itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                    itemId, latestProcessDefinitionId, currentTaskDefKey, bind.getOpinionFrameMark());
            // 如果不存在，则创建新的绑定
            if (null == existingBind) {
                createNewOpinionFrameBinding(itemId, tenantId, userId, userName, latestProcessDefinitionId,
                    currentTaskDefKey, bind);
            }
        }
    }

    /**
     * 创建新的意见框绑定
     */
    private void createNewOpinionFrameBinding(String itemId, String tenantId, String userId, String userName,
        String processDefinitionId, String taskDefKey, ItemOpinionFrameBind sourceBind) {
        String newBindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        String sourceBindId = sourceBind.getId();
        // 创建新的意见框绑定
        ItemOpinionFrameBind newBind = createOpinionFrameBind(newBindId, itemId, tenantId, userId, userName,
            processDefinitionId, taskDefKey, sourceBind);
        itemOpinionFrameBindRepository.save(newBind);
        // 复制意见框授权信息
        copyOpinionFrameRoles(sourceBindId, newBindId);
        // 复制意见框一键设置配置
        copyOpinionFrameOneClickSettings(sourceBindId, newBindId, userId);
    }

    /**
     * 创建意见框绑定对象
     */
    private ItemOpinionFrameBind createOpinionFrameBind(String bindId, String itemId, String tenantId, String userId,
        String userName, String processDefinitionId, String taskDefKey, ItemOpinionFrameBind sourceBind) {
        ItemOpinionFrameBind newBind = new ItemOpinionFrameBind();
        newBind.setId(bindId);
        newBind.setItemId(itemId);
        newBind.setOpinionFrameMark(sourceBind.getOpinionFrameMark());
        newBind.setOpinionFrameName(sourceBind.getOpinionFrameName());
        newBind.setProcessDefinitionId(processDefinitionId);
        newBind.setTaskDefKey(taskDefKey);
        newBind.setTenantId(tenantId);
        newBind.setUserId(userId);
        newBind.setUserName(userName);
        newBind.setSignOpinion(sourceBind.isSignOpinion());
        return newBind;
    }

    /**
     * 复制意见框角色授权信息
     */
    private void copyOpinionFrameRoles(String sourceBindId, String targetBindId) {
        List<ItemOpinionFrameRole> roleList = itemOpinionFrameRoleService.listByItemOpinionFrameId(sourceBindId);
        for (ItemOpinionFrameRole role : roleList) {
            itemOpinionFrameRoleService.saveOrUpdate(targetBindId, role.getRoleId());
        }
    }

    /**
     * 复制意见框一键设置配置
     */
    private void copyOpinionFrameOneClickSettings(String sourceBindId, String targetBindId, String userId) {
        List<OpinionFrameOneClickSet> setList = opinionFrameOneClickSetService.findByBindId(sourceBindId);
        for (OpinionFrameOneClickSet set : setList) {
            OpinionFrameOneClickSet newSet = new OpinionFrameOneClickSet();
            newSet.setBindId(targetBindId);
            newSet.setOneSetType(set.getOneSetType());
            newSet.setOneSetTypeName(set.getOneSetTypeName());
            newSet.setExecuteAction(set.getExecuteAction());
            newSet.setExecuteActionName(set.getExecuteActionName());
            newSet.setUserId(userId);
            opinionFrameOneClickSetService.save(newSet);
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        UserInfo currentUser = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = currentUser.getPersonId();
        String userName = currentUser.getName();
        List<ItemOpinionFrameBind> bindList = itemOpinionFrameBindRepository
            .findByItemIdAndProcessDefinitionIdOrderByCreateTimeAsc(itemId, lastVersionPid);
        if (null != bindList && !bindList.isEmpty()) {
            for (ItemOpinionFrameBind bind : bindList) {
                // 创建新的意见框绑定
                String newBindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                ItemOpinionFrameBind newBind =
                    createOpinionFrameBind(newBindId, newItemId, tenantId, userId, userName, lastVersionPid, bind);
                itemOpinionFrameBindRepository.save(newBind);
                // 复制意见框一键设置的配置
                copyOpinionFrameOneClickSettings(bind.getId(), newBindId, userId);
            }
        }
    }

    /**
     * 创建意见框绑定对象（用于复制场景）
     */
    private ItemOpinionFrameBind createOpinionFrameBind(String bindId, String itemId, String tenantId, String userId,
        String userName, String processDefinitionId, ItemOpinionFrameBind sourceBind) {
        ItemOpinionFrameBind newBind = new ItemOpinionFrameBind();
        newBind.setId(bindId);
        newBind.setItemId(itemId);
        newBind.setOpinionFrameMark(sourceBind.getOpinionFrameMark());
        newBind.setOpinionFrameName(sourceBind.getOpinionFrameName());
        newBind.setProcessDefinitionId(processDefinitionId);
        newBind.setTaskDefKey(sourceBind.getTaskDefKey());
        newBind.setTenantId(tenantId);
        newBind.setUserId(userId);
        newBind.setUserName(userName);
        return newBind;
    }

    @Override
    @Transactional
    public void delete(String id) {
        itemOpinionFrameRoleService.removeByItemOpinionFrameId(id);
        itemOpinionFrameBindRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void delete(String[] ids) {
        for (String id : ids) {
            itemOpinionFrameRoleService.removeByItemOpinionFrameId(id);
            itemOpinionFrameBindRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        List<ItemOpinionFrameBind> list = itemOpinionFrameBindRepository.findByItemId(itemId);
        for (ItemOpinionFrameBind bind : list) {
            itemOpinionFrameRoleService.removeByItemOpinionFrameId(bind.getId());
            itemOpinionFrameBindRepository.deleteById(bind.getId());
        }
    }

    @Override
    public ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(String itemId,
        String processDefinitionId, String taskDefKey, String opinionFrameMark) {
        // 先查找任务级别的意见框绑定
        ItemOpinionFrameBind bind =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                processDefinitionId, taskDefKey, opinionFrameMark);
        // 如果任务级别没有找到，查找流程级别的意见框绑定
        if (null == bind) {
            bind =
                itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndOpinionFrameMarkAndTaskDefKeyIsNull(
                    itemId, processDefinitionId, opinionFrameMark);
        }
        // 如果找到了绑定信息，设置角色ID列表
        if (null != bind) {
            List<String> roleIds = getRoleIds(bind.getId());
            bind.setRoleIds(roleIds);
        }
        return bind;
    }

    /**
     * 根据意见框绑定ID获取角色ID列表
     */
    private List<String> getRoleIds(String itemOpinionFrameBindId) {
        List<String> roleIds = new ArrayList<>();
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleService.listByItemOpinionFrameId(itemOpinionFrameBindId);
        for (ItemOpinionFrameRole role : roleList) {
            roleIds.add(role.getRoleId());
        }
        return roleIds;
    }

    @Override
    public List<String> getBindOpinionFrame(String itemId, String processDefinitionId) {
        return itemOpinionFrameBindRepository.getBindOpinionFrame(itemId, processDefinitionId);
    }

    @Override
    public ItemOpinionFrameBind getById(String id) {
        return itemOpinionFrameBindRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemId(String itemId) {
        return itemOpinionFrameBindRepository.findByItemId(itemId);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdOrderByCreateTimeAsc(itemId,
            processDefinitionId);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateTimeAsc(
            itemId, processDefinitionId, taskDefKey);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemOpinionFrameBind> result = new ArrayList<>();
        List<ItemOpinionFrameBind> bindList =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateTimeAsc(itemId,
                processDefinitionId, taskDefKey);

        for (ItemOpinionFrameBind bind : bindList) {
            RoleInfo roleInfo = getRoleInfo(bind.getId());
            bind.setRoleIds(roleInfo.getRoleIds());
            bind.setRoleNames(roleInfo.getRoleNames());
            result.add(bind);
        }
        return result;
    }

    /**
     * 获取意见框绑定的角色信息
     */
    private RoleInfo getRoleInfo(String itemOpinionFrameBindId) {
        List<ItemOpinionFrameRole> roleList =
            itemOpinionFrameRoleService.listByItemOpinionFrameId(itemOpinionFrameBindId);
        List<String> roleIds = new ArrayList<>();
        StringBuilder roleNamesBuilder = new StringBuilder();
        for (int i = 0; i < roleList.size(); i++) {
            ItemOpinionFrameRole role = roleList.get(i);
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
    public List<ItemOpinionFrameBind> listByMark(String mark) {
        return StringUtils.isNotEmpty(mark)
            ? itemOpinionFrameBindRepository.findByOpinionFrameMarkOrderByItemIdDescUpdateTimeDesc(mark) : null;
    }

    @Override
    public Page<ItemOpinionFrameBind> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return itemOpinionFrameBindRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public ItemOpinionFrameBind save(ItemOpinionFrameBind opinionFrameTaskRoleBind) {
        return itemOpinionFrameBindRepository.save(opinionFrameTaskRoleBind);
    }

    @Override
    @Transactional
    public void save(String opinionFrameNameAndMarks, String itemId, String processDefinitionId, String taskDefKey) {
        if (StringUtils.isEmpty(opinionFrameNameAndMarks)) {
            return;
        }
        UserInfo currentUser = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = currentUser.getPersonId();
        String userName = currentUser.getName();
        // 解析并保存意见框绑定信息
        String[] frameEntries = opinionFrameNameAndMarks.split(";");
        for (String entry : frameEntries) {
            String[] nameAndMark = entry.split(":");
            String name = nameAndMark[0];
            String mark = nameAndMark[1];
            ItemOpinionFrameBind newBind =
                createOpinionFrameBind(name, mark, itemId, processDefinitionId, taskDefKey, tenantId, userId, userName);
            itemOpinionFrameBindRepository.save(newBind);
        }
    }

    /**
     * 创建意见框绑定对象
     */
    private ItemOpinionFrameBind createOpinionFrameBind(String name, String mark, String itemId,
        String processDefinitionId, String taskDefKey, String tenantId, String userId, String userName) {
        ItemOpinionFrameBind newBind = new ItemOpinionFrameBind();
        newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newBind.setOpinionFrameMark(mark);
        newBind.setOpinionFrameName(name);
        newBind.setItemId(itemId);
        newBind.setTaskDefKey(taskDefKey);
        newBind.setTenantId(tenantId);
        newBind.setUserId(userId);
        newBind.setUserName(userName);
        newBind.setProcessDefinitionId(processDefinitionId);
        newBind.setSignOpinion(true);
        return newBind;
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
