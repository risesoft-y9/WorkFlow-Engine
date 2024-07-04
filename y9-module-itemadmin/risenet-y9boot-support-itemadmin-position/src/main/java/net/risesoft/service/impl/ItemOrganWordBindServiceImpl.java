package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.ItemOrganWordRole;
import net.risesoft.entity.OrganWord;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemOrganWordBindRepository;
import net.risesoft.repository.jpa.OrganWordRepository;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.ItemOrganWordRoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemOrganWordBindServiceImpl implements ItemOrganWordBindService {

    private final ItemOrganWordBindRepository itemOrganWordBindRepository;

    private final ItemOrganWordRoleService itemOrganWordRoleService;

    private final OrganWordRepository organWordRepository;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    private final ProcessDefinitionApi processDefinitionManager;

    private final RoleApi roleManager;

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
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
        List<TargetModel> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false).getData();
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
                    List<ItemOrganWordRole> roleList = itemOrganWordRoleService.findByItemOrganWordBindId(oldbindId);
                    for (ItemOrganWordRole role : roleList) {
                        itemOrganWordRoleService.saveOrUpdate(newbindId, role.getRoleId());
                    }
                }
            }
        }
    }

    @Override
    public ItemOrganWordBind findById(String id) {
        return itemOrganWordBindRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemOrganWordBind> findByItemId(String itemId) {
        return itemOrganWordBindRepository.findByItemId(itemId);
    }

    @Override
    public List<ItemOrganWordBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        List<ItemOrganWordBind> owtrbList =
            itemOrganWordBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        OrganWord organWord;
        for (ItemOrganWordBind bind : owtrbList) {
            String custom = bind.getOrganWordCustom();
            organWord = organWordRepository.findByCustom(custom);
            bind.setOrganWordName(organWord == null ? custom + "对应的编号不存在" : organWord.getName());

            List<ItemOrganWordRole> roleList =
                itemOrganWordRoleService.findByItemOrganWordBindIdContainRoleName(bind.getId());
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
    public List<ItemOrganWordBind> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemOrganWordBind> owtrbList = itemOrganWordBindRepository
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        OrganWord organWord;
        for (ItemOrganWordBind bind : owtrbList) {
            String custom = bind.getOrganWordCustom();
            organWord = organWordRepository.findByCustom(custom);
            bind.setOrganWordName(organWord == null ? custom + "对应的编号不存在" : organWord.getName());
            List<ItemOrganWordRole> roleList = itemOrganWordRoleService.findByItemOrganWordBindId(bind.getId());
            List<String> roleIds = new ArrayList<>();
            String roleNames = "";
            for (ItemOrganWordRole role : roleList) {
                roleIds.add(role.getId());
                Role r = roleManager.getRole(role.getRoleId()).getData();
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
    public ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId,
        String processDefinitionId, String taskDefKey, String custom) {
        ItemOrganWordBind bind =
            itemOrganWordBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(itemId,
                processDefinitionId, taskDefKey, custom);
        if (null != bind) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOrganWordRole> roleList = itemOrganWordRoleService.findByItemOrganWordBindId(bind.getId());
            for (ItemOrganWordRole role : roleList) {
                roleIds.add(role.getRoleId());
            }
            bind.setRoleIds(roleIds);
        }
        return bind;
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
