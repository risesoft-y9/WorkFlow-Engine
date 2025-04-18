package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.entity.OpinionFrameOneClickSet;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ItemOpinionFrameBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.OpinionFrameOneClickSetService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.config.ItemOpinionFrameRoleService;
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

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final OpinionFrameOneClickSetService opinionFrameOneClickSetService;

    @Override
    @Transactional
    public void changeSignOpinion(String id, Boolean signOpinion) {
        ItemOpinionFrameBind itemOpinionFrameBind = this.getById(id);
        if (null != itemOpinionFrameBind) {
            itemOpinionFrameBind.setSignOpinion(signOpinion);
            this.save(itemOpinionFrameBind);
        }
    }

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
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
            List<ItemOpinionFrameBind> bindList =
                itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(
                    itemId, previouspdId, currentTaskDefKey);
            for (ItemOpinionFrameBind bind : bindList) {
                ItemOpinionFrameBind oldBind =
                    itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                        itemId, latestpdId, currentTaskDefKey, bind.getOpinionFrameMark());
                if (null == oldBind) {
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    String oldbindId = bind.getId();
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
                    newbind.setSignOpinion(bind.isSignOpinion());
                    itemOpinionFrameBindRepository.save(newbind);
                    /**
                     * 保存意见框的授权
                     */
                    List<ItemOpinionFrameRole> roleList =
                        itemOpinionFrameRoleService.listByItemOpinionFrameId(oldbindId);
                    for (ItemOpinionFrameRole role : roleList) {
                        itemOpinionFrameRoleService.saveOrUpdate(newbindId, role.getRoleId());
                    }
                    /**
                     * 复制意见框一键设置的配置
                     */
                    List<OpinionFrameOneClickSet> setList = opinionFrameOneClickSetService.findByBindId(oldbindId);
                    for (OpinionFrameOneClickSet set : setList) {
                        OpinionFrameOneClickSet newSet = new OpinionFrameOneClickSet();
                        newSet.setBindId(newbindId);
                        newSet.setCreateDate(sdf.format(new Date()));
                        newSet.setOneSetType(set.getOneSetType());
                        newSet.setOneSetTypeName(set.getOneSetTypeName());
                        newSet.setExecuteAction(set.getExecuteAction());
                        newSet.setExecuteActionName(set.getExecuteActionName());
                        newSet.setUserId(userId);
                        opinionFrameOneClickSetService.save(newSet);
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
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId(), userName = person.getName();
        try {
            List<ItemOpinionFrameBind> bindList = itemOpinionFrameBindRepository
                .findByItemIdAndProcessDefinitionIdOrderByCreateDateAsc(itemId, lastVersionPid);
            if (null != bindList && !bindList.isEmpty()) {
                for (ItemOpinionFrameBind bind : bindList) {
                    ItemOpinionFrameBind newbind = new ItemOpinionFrameBind();
                    String newbindId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
                    newbind.setId(newbindId);
                    newbind.setItemId(newItemId);
                    newbind.setCreateDate(sdf.format(new Date()));
                    newbind.setModifyDate(sdf.format(new Date()));
                    newbind.setOpinionFrameMark(bind.getOpinionFrameMark());
                    newbind.setOpinionFrameName(bind.getOpinionFrameName());
                    newbind.setProcessDefinitionId(lastVersionPid);
                    newbind.setTaskDefKey(bind.getTaskDefKey());
                    newbind.setTenantId(tenantId);
                    newbind.setUserId(userId);
                    newbind.setUserName(userName);
                    itemOpinionFrameBindRepository.save(newbind);

                    // 复制意见框一键设置的配置
                    List<OpinionFrameOneClickSet> setList = opinionFrameOneClickSetService.findByBindId(bind.getId());
                    for (OpinionFrameOneClickSet set : setList) {
                        OpinionFrameOneClickSet newset = new OpinionFrameOneClickSet();
                        newset.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        newset.setBindId(newbindId);
                        newset.setCreateDate(sdf.format(new Date()));
                        newset.setOneSetType(set.getOneSetType());
                        newset.setOneSetTypeName(set.getOneSetTypeName());
                        newset.setExecuteAction(set.getExecuteAction());
                        newset.setExecuteActionName(set.getExecuteActionName());
                        newset.setUserId(userId);
                        opinionFrameOneClickSetService.save(newset);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制意见框绑定信息失败", e);
        }
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
            this.delete(id);
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
        ItemOpinionFrameBind bind =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                processDefinitionId, taskDefKey, opinionFrameMark);
        if (null != bind) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOpinionFrameRole> roleList = itemOpinionFrameRoleService.listByItemOpinionFrameId(bind.getId());
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
                    itemOpinionFrameRoleService.listByItemOpinionFrameId(bind.getId());
                for (ItemOpinionFrameRole role : roleList) {
                    roleIds.add(role.getRoleId());
                }
                bind.setRoleIds(roleIds);
            }
        }
        return bind;
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
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdOrderByCreateDateAsc(itemId,
            processDefinitionId);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId,
        String processDefinitionId, String taskDefKey) {
        return itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(
            itemId, processDefinitionId, taskDefKey);
    }

    @Override
    public List<ItemOpinionFrameBind> listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String itemId,
        String processDefinitionId, String taskDefKey) {
        List<ItemOpinionFrameBind> resList = new ArrayList<>();
        List<ItemOpinionFrameBind> bindList =
            itemOpinionFrameBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyOrderByCreateDateAsc(itemId,
                processDefinitionId, taskDefKey);
        for (ItemOpinionFrameBind bind : bindList) {
            List<String> roleIds = new ArrayList<>();
            List<ItemOpinionFrameRole> roleList = itemOpinionFrameRoleService.listByItemOpinionFrameId(bind.getId());
            String roleNames = "";
            for (ItemOpinionFrameRole role : roleList) {
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
            resList.add(bind);
        }
        return resList;
    }

    @Override
    public List<ItemOpinionFrameBind> listByMark(String mark) {
        return StringUtils.isNotEmpty(mark)
            ? itemOpinionFrameBindRepository.findByOpinionFrameMarkOrderByItemIdDescModifyDateDesc(mark) : null;
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

    /**
     * Description:
     *
     * @param opinionFrameNameAndMarks
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     */
    @Override
    @Transactional
    public void save(String opinionFrameNameAndMarks, String itemId, String processDefinitionId, String taskDefKey) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<ItemOpinionFrameBind> resList = new ArrayList<>();
        UserInfo person = Y9LoginUserHolder.getUserInfo();
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
                newoftrb.setUserId(person.getPersonId());
                newoftrb.setUserName(person.getName());
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
                    newoftrb.setUserId(person.getPersonId());
                    newoftrb.setUserName(person.getName());
                    newoftrb.setProcessDefinitionId(processDefinitionId);
                    newoftrb.setSignOpinion(true);

                    this.save(newoftrb);
                    resList.add(newoftrb);
                }
            }
        }
    }
}
