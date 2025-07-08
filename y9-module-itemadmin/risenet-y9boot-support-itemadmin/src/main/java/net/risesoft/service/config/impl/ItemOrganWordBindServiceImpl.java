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
import net.risesoft.repository.jpa.ItemOrganWordBindRepository;
import net.risesoft.repository.jpa.OrganWordRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.service.config.ItemOrganWordRoleService;
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

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final RoleApi roleApi;

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        Item item = spmApproveItemRepository.findById(itemId).orElse(null);
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
            List<ItemOrganWordBind> bindList = itemOrganWordBindRepository
                .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, previouspdId, currentTaskDefKey);
            for (ItemOrganWordBind bind : bindList) {
                ItemOrganWordBind oldBind =
                    itemOrganWordBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(
                        itemId, latestpdId, currentTaskDefKey, bind.getOrganWordCustom());
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE), oldbindId = bind.getId();
                    /*
                     * 保存意见框绑定
                     */
                    ItemOrganWordBind newbind = new ItemOrganWordBind();
                    newbind.setId(newbindId);
                    newbind.setItemId(itemId);
                    newbind.setCreateDate(sdf.format(new Date()));
                    newbind.setModifyDate(sdf.format(new Date()));
                    newbind.setOrganWordCustom(bind.getOrganWordCustom());
                    newbind.setProcessDefinitionId(latestpdId);
                    newbind.setTaskDefKey(currentTaskDefKey);
                    newbind.setUserId(userId);
                    newbind.setUserName(userName);

                    itemOrganWordBindRepository.save(newbind);
                    /*
                     * 保存编号的授权
                     */
                    List<ItemOrganWordRole> roleList = itemOrganWordRoleService.listByItemOrganWordBindId(oldbindId);
                    for (ItemOrganWordRole role : roleList) {
                        itemOrganWordRoleService.saveOrUpdate(newbindId, role.getRoleId());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId(), userName = person.getName();
        try {
            List<ItemOrganWordBind> bindList =
                itemOrganWordBindRepository.findByItemIdAndProcessDefinitionId(itemId, lastVersionPid);
            if (null != bindList && !bindList.isEmpty()) {
                for (ItemOrganWordBind bind : bindList) {
                    ItemOrganWordBind newbind = new ItemOrganWordBind();
                    newbind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newbind.setItemId(newItemId);
                    newbind.setCreateDate(sdf.format(new Date()));
                    newbind.setModifyDate(sdf.format(new Date()));
                    newbind.setOrganWordCustom(bind.getOrganWordCustom());
                    newbind.setProcessDefinitionId(lastVersionPid);
                    newbind.setTaskDefKey(bind.getTaskDefKey());
                    newbind.setUserId(userId);
                    newbind.setUserName(userName);
                    itemOrganWordBindRepository.save(newbind);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制编号绑定关系失败", e);
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
        List<ItemOrganWordBind> owtrbList = itemOrganWordBindRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        OrganWord organWord;
        for (ItemOrganWordBind bind : owtrbList) {
            String custom = bind.getOrganWordCustom();
            organWord = organWordRepository.findByCustom(custom);
            bind.setOrganWordName(organWord == null ? custom + "对应的编号不存在" : organWord.getName());
            List<ItemOrganWordRole> roleList = itemOrganWordRoleService.listByItemOrganWordBindId(bind.getId());
            List<String> roleIds = new ArrayList<>();
            String roleNames = "";
            for (ItemOrganWordRole role : roleList) {
                roleIds.add(role.getId());
                Role r = roleApi.getRole(role.getRoleId()).getData();
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = null == r ? "角色不存在" : r.getName();
                } else {
                    roleNames += "、" + (null == r ? "角色不存在" : r.getName());
                }
            }
            // 放绑定关系id，便于删除
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);

        }
        return owtrbList;
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
            this.remove(id);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ItemOrganWordBind taskRoleBind = this.findById(id);
        taskRoleBind.setModifyDate(sdf.format(new Date()));
        taskRoleBind.setOrganWordCustom(custom);
        itemOrganWordBindRepository.save(taskRoleBind);
    }

    @Override
    @Transactional
    public void save(String custom, String itemId, String processDefinitionId, String taskDefKey) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            taskRoleBind.setCreateDate(sdf.format(new Date()));
            taskRoleBind.setModifyDate(sdf.format(new Date()));
            itemOrganWordBindRepository.save(taskRoleBind);
        } else {
            bind.setOrganWordCustom(custom);
            bind.setProcessDefinitionId(processDefinitionId);
            bind.setTaskDefKey(taskDefKey);
            bind.setUserId(Y9LoginUserHolder.getPersonId());
            bind.setUserName(Y9LoginUserHolder.getUserInfo().getName());
            bind.setModifyDate(sdf.format(new Date()));
            itemOrganWordBindRepository.save(bind);
        }
    }
}
