package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.permission.RoleApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemOpinionFrameBindRepository;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.ItemOpinionFrameRoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.ProcessDefinitionApiClient;
import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemOpinionFrameBindService")
public class ItemOpinionFrameBindServiceImpl implements ItemOpinionFrameBindService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemOpinionFrameBindRepository itemOpinionFrameBindRepository;

    @Autowired
    private ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApiClient repositoryManager;

    @Autowired
    private ProcessDefinitionApiClient processDefinitionManager;

    @Override
    @Transactional(readOnly = false)
    public void changeSignOpinion(String id, Boolean signOpinion) {
        ItemOpinionFrameBind itemOpinionFrameBind = this.findOne(id);
        if (null != itemOpinionFrameBind) {
            itemOpinionFrameBind.setSignOpinion(signOpinion);
            this.save(itemOpinionFrameBind);
        }
    }

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
            List<ItemOpinionFrameBind> bindList =
                itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(
                    itemId, previouspdId, currentTaskDefKey);
            for (ItemOpinionFrameBind bind : bindList) {
                ItemOpinionFrameBind oldBind =
                    itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                        itemId, latestpdId, currentTaskDefKey, bind.getOpinionFrameMark());
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE), oldbindId = bind.getId();
                    /**
                     * 保存意见框绑定
                     */
                    ItemOpinionFrameBind newbind = new ItemOpinionFrameBind();
                    newbind.setId(newbindId);
                    newbind.setItemId(itemId);
                    newbind.setCreateDate(sdf.format(new Date()));
                    newbind.setModifyDate(sdf.format(new Date()));
                    newbind.setOpinionFrameMark(bind.getOpinionFrameMark());
                    newbind.setOpinionFrameName(bind.getOpinionFrameName());
                    newbind.setProcessDefinitionId(latestpdId);
                    newbind.setTaskDefKey(currentTaskDefKey);
                    newbind.setTenantId(tenantId);
                    newbind.setUserId(userId);
                    newbind.setUserName(userName);

                    itemOpinionFrameBindRepository.save(newbind);
                    /**
                     * 保存意见框的授权
                     */
                    List<ItemOpinionFrameRole> roleList =
                        itemOpinionFrameRoleService.findByItemOpinionFrameId(oldbindId);
                    for (ItemOpinionFrameRole role : roleList) {
                        itemOpinionFrameRoleService.saveOrUpdate(newbindId, role.getRoleId());
                    }
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        itemOpinionFrameRoleService.removeByItemOpinionFrameId(id);
        itemOpinionFrameBindRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        for (String id : ids) {
            this.delete(id);
        }
    }

    @Override
    public Page<ItemOpinionFrameBind> findAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return itemOpinionFrameBindRepository.findAll(pageable);
    }

    @Override
    public List<ItemOpinionFrameBind> findByItemId(String itemId) {
        return itemOpinionFrameBindRepository.findByItemId(itemId);
    }

    @Override
    public List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdOrderByCreateDateAsc(itemId,
            processDefinitionId);
    }

    @Override
    public List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(
            itemId, processDefinitionId, taskDefKey);
    }

    @Override
    public ItemOpinionFrameBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(String itemId,
        String processDefinitionId, String taskDefKey, String opinionFrameMark) {
        ItemOpinionFrameBind bind =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                processDefinitionId, taskDefKey, opinionFrameMark);
        if (null != bind) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOpinionFrameRole> roleList = itemOpinionFrameRoleService.findByItemOpinionFrameId(bind.getId());
            for (ItemOpinionFrameRole role : roleList) {
                roleIds.add(role.getRoleId());
            }
            bind.setRoleIds(roleIds);
        } else {// 任务没有绑定意见框，获取流程绑定的意见框
            bind =
                itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndOpinionFrameMarkAndTaskDefKeyIsNull(
                    itemId, processDefinitionId, opinionFrameMark);
            if (null != bind) {
                List<String> roleIds = new ArrayList<>();
                List<ItemOpinionFrameRole> roleList =
                    itemOpinionFrameRoleService.findByItemOpinionFrameId(bind.getId());
                for (ItemOpinionFrameRole role : roleList) {
                    roleIds.add(role.getRoleId());
                }
                bind.setRoleIds(roleIds);
            }
        }
        return bind;
    }

    @Override
    public List<ItemOpinionFrameBind> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemOpinionFrameBind> resList = new ArrayList<>();
        List<ItemOpinionFrameBind> bindList =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(itemId,
                processDefinitionId, taskDefKey);
        for (ItemOpinionFrameBind bind : bindList) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOpinionFrameRole> roleList = itemOpinionFrameRoleService.findByItemOpinionFrameId(bind.getId());
            String roleNames = "";
            for (ItemOpinionFrameRole role : roleList) {
                roleIds.add(role.getId());
                Role r = roleManager.getRole(role.getRoleId());
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = null == r ? "角色不存在" : r.getName();
                } else {
                    roleNames += "、" + (null == r ? "角色不存在" : r.getName());
                }
            }
            bind.setRoleIds(roleIds);
            bind.setRoleNames(roleNames);
            resList.add(bind);
        }
        return resList;
    }

    @Override
    public List<ItemOpinionFrameBind> findByMark(String mark) {
        return StringUtils.isNotEmpty(mark)
            ? itemOpinionFrameBindRepository.findByOpinionFrameMarkOrderByItemIdDescModifyDateDesc(mark) : null;
    }

    @Override
    public ItemOpinionFrameBind findOne(String id) {
        return itemOpinionFrameBindRepository.findById(id).orElse(null);
    }

    @Override
    public List<String> getBindOpinionFrame(String itemId, String processDefinitionId) {
        return itemOpinionFrameBindRepository.getBindOpinionFrame(itemId, processDefinitionId);
    }

    @Override
    @Transactional(readOnly = false)
    public ItemOpinionFrameBind save(ItemOpinionFrameBind opinionFrameTaskRoleBind) {
        return itemOpinionFrameBindRepository.save(opinionFrameTaskRoleBind);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(String opinionFrameNameAndMarks, String itemId, String processDefinitionId, String taskDefKey) {
        List<ItemOpinionFrameBind> resList = new ArrayList<ItemOpinionFrameBind>();
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotEmpty(opinionFrameNameAndMarks)) {
            if (!opinionFrameNameAndMarks.contains(PunctuationConsts.SEMICOLON)) {
                String[] opinionFrameNameAndMark = opinionFrameNameAndMarks.split(":");
                String name = opinionFrameNameAndMark[0];
                String mark = opinionFrameNameAndMark[1];

                ItemOpinionFrameBind newoftrb = new ItemOpinionFrameBind();
                newoftrb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newoftrb.setCreateDate(sdf.format(new Date()));
                newoftrb.setModifyDate(sdf.format(new Date()));
                newoftrb.setOpinionFrameMark(mark);
                newoftrb.setOpinionFrameName(name);
                newoftrb.setItemId(itemId);
                newoftrb.setTaskDefKey(taskDefKey);
                newoftrb.setTenantId(tenantId);
                newoftrb.setUserId(userInfo.getPersonId());
                newoftrb.setUserName(userInfo.getName());
                newoftrb.setProcessDefinitionId(processDefinitionId);
                newoftrb.setSignOpinion(true);

                this.save(newoftrb);
                resList.add(newoftrb);
            } else {
                String[] strings = opinionFrameNameAndMarks.split(";");
                for (String string : strings) {
                    String[] opinionFrameNameAndMark = string.split(":");
                    String name = opinionFrameNameAndMark[0];
                    String mark = opinionFrameNameAndMark[1];

                    ItemOpinionFrameBind newoftrb = new ItemOpinionFrameBind();
                    newoftrb.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newoftrb.setCreateDate(sdf.format(new Date()));
                    newoftrb.setModifyDate(sdf.format(new Date()));
                    newoftrb.setOpinionFrameMark(mark);
                    newoftrb.setOpinionFrameName(name);
                    newoftrb.setItemId(itemId);
                    newoftrb.setTaskDefKey(taskDefKey);
                    newoftrb.setTenantId(tenantId);
                    newoftrb.setUserId(userInfo.getPersonId());
                    newoftrb.setUserName(userInfo.getName());
                    newoftrb.setProcessDefinitionId(processDefinitionId);
                    newoftrb.setSignOpinion(true);

                    this.save(newoftrb);
                    resList.add(newoftrb);
                }
            }
        }
    }
}
