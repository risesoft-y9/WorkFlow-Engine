package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.Task4ActRuDetailService;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 */
@Slf4j
@Service
public class Task4ActRuDetailServiceImpl implements Task4ActRuDetailService {

    private final ActRuDetailApi actRuDetailApi;
    private final OrgUnitApi orgUnitApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public Task4ActRuDetailServiceImpl(ActRuDetailApi actRuDetailApi, OrgUnitApi orgUnitApi) {
        this.actRuDetailApi = actRuDetailApi;
        this.orgUnitApi = orgUnitApi;
    }

    @Override
    public void todo2doing(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                actRuDetailApi.todo2doing(tenantId, taskEntity.getId(), taskEntity.getAssignee());
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################Task4ActRuDetailService:todo2doing待办-->在办失败-taskId:{}##########################",
                taskEntity.getId(), e);
        }
    }

    @Override
    public void createTodo(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);
                actRuDetailApi.saveOrUpdate(tenantId,
                    getModel(tenantId, processSerialNumber, taskEntity, taskEntity.getAssignee()));
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################Task4ActRuDetaillService:createTodo保存待办信息失败-taskId:{}##########################",
                taskEntity.getId(), e);
        }
    }

    private ActRuDetailModel getModel(String tenantId, String processSerialNumber, DelegateTask taskEntity,
        String assignee) {
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
        ActRuDetailModel model = new ActRuDetailModel();
        model.setCreateTime(taskEntity.getCreateTime());
        model.setAssignee(assignee);
        model.setAssigneeName(orgUnit.getName());
        model.setDeptId(orgUnit.getParentId());
        model.setLastTime(new Date());
        model.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
        model.setTaskDefKey(taskEntity.getTaskDefinitionKey());
        model.setTaskDefName(taskEntity.getName());
        model.setExecutionId(taskEntity.getExecutionId());
        model.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
        model.setProcessInstanceId(taskEntity.getProcessInstanceId());
        model.setProcessSerialNumber(processSerialNumber);
        model.setStatus(ActRuDetailStatusEnum.TODO);
        model.setTaskId(taskEntity.getId());
        model.setStarted(true);
        model.setEnded(false);
        model.setDeleted(false);
        model.setStartTime(getStartTime(taskEntity.getProcessInstanceId()));
        model.setSignStatus(ActRuDetailSignStatusEnum.NONE);

        String taskSenderId = (String)taskEntity.getVariable(SysVariables.TASK_SENDER_ID);
        OrgUnit sendUser = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, taskSenderId).getData();
        model.setSendUserId(taskSenderId);
        model.setSendUserName(sendUser.getName());
        model.setSendDeptId(sendUser.getParentId());
        return model;
    }

    private String getStartTime(String processInstanceId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql0 =
            "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM  ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = '"
                + processInstanceId + "'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);
        return list.isEmpty() ? sdf.format(new Date()) : list.get(0).get("START_TIME_").toString();
    }

    @Override
    public void claim(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                actRuDetailApi.claim(tenantId, taskEntity.getId(), taskEntity.getAssignee());
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################saveOrUpdate4DoSign抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId(), e);
        }
    }

    @Override
    public void todo2doing4Jump(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                for (IdentityLink link : linkSet) {
                    actRuDetailApi.todo2doing(tenantId, taskEntity.getId(), link.getUserId());
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################saveOrUpdate4Reposition抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId());
        }
    }

    @Override
    public void unClaim(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                actRuDetailApi.unClaim(tenantId, taskEntity.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("##########################unClaim抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId());
        }
    }

    @Override
    public void createTodo4Claim(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);
                StringBuffer names = new StringBuffer();
                taskEntity.getCandidates().forEach(link -> {
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, link.getUserId()).getData();
                    if (StringUtils.isBlank(names)) {
                        names.append(orgUnit.getName());
                    } else {
                        names.append("、").append(orgUnit.getName());
                    }
                });
                taskEntity.getCandidates().forEach(link -> {
                    ActRuDetailModel model = getModel(tenantId, processSerialNumber, taskEntity, link.getUserId());
                    model.setSignStatus(ActRuDetailSignStatusEnum.TODO);
                    model.setAssigneeName(names.toString());
                    actRuDetailApi.saveOrUpdate(tenantId, model);
                });
            }
        } catch (Exception e) {
            LOGGER.error("########createTodo4Claim抢占式节点-保存待办失败-taskId:{}########", taskEntity.getId());
            LOGGER.error(e.getMessage());
        }
    }
}