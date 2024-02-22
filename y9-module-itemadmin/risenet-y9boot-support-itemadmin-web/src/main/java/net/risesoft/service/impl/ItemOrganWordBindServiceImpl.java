package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.ItemOrganWordRole;
import net.risesoft.entity.OrganWord;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemOrganWordBindRepository;
import net.risesoft.repository.jpa.OrganWordRepository;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.ItemOrganWordRoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.ProcessDefinitionApiClient;
import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "itemOrganWordBindService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemOrganWordBindServiceImpl implements ItemOrganWordBindService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemOrganWordBindRepository itemOrganWordBindRepository;

    @Autowired
    private ItemOrganWordRoleService itemOrganWordRoleService;

    @Autowired
    private OrganWordRepository organWordRepository;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApiClient repositoryManager;

    @Autowired
    private ProcessDefinitionApiClient processDefinitionManager;

    @Autowired
    private RoleApi roleManager;

    @Override
    @Transactional(readOnly = false)
    public void copyBind(String itemId, String processDefinitionId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId(),
            userName = userInfo.getName();
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
        List<Map<String, Object>> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false);
        for (Map<String, Object> map : nodes) {
            String currentTaskDefKey = (String)map.get("taskDefKey");
            List<ItemOrganWordBind> bindList = itemOrganWordBindRepository
                .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, previouspdId, currentTaskDefKey);
            for (ItemOrganWordBind bind : bindList) {
                ItemOrganWordBind oldBind =
                    itemOrganWordBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(
                        itemId, latestpdId, currentTaskDefKey, bind.getOrganWordCustom());
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE), oldbindId = bind.getId();
                    /**
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
                    /**
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
        OrganWord organWord = null;
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
        OrganWord organWord = null;
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
    @Transactional(readOnly = false)
    public void remove(String id) {
        itemOrganWordRoleService.removeByItemOrganWordBindId(id);
        itemOrganWordBindRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(String[] ids) {
        for (String id : ids) {
            this.remove(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void save(ItemOrganWordBind taskRoleBind) {
        itemOrganWordBindRepository.save(taskRoleBind);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(String id, String name, String custom) {
        ItemOrganWordBind taskRoleBind = this.findById(id);
        taskRoleBind.setModifyDate(sdf.format(new Date()));
        taskRoleBind.setOrganWordCustom(custom);
        itemOrganWordBindRepository.save(taskRoleBind);
    }

    @Override
    @Transactional(readOnly = false)
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
