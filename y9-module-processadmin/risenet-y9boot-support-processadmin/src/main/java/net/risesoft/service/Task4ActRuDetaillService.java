package net.risesoft.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 */
@Slf4j
@Service(value = "task4ActRuDetaillService")
public class Task4ActRuDetaillService {

    private final ActRuDetailApi actRuDetailApi;
    private final OrgUnitApi orgUnitApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public Task4ActRuDetaillService(ActRuDetailApi actRuDetailApi, OrgUnitApi orgUnitApi) {
        this.actRuDetailApi = actRuDetailApi;
        this.orgUnitApi = orgUnitApi;
    }

    public void saveOrUpdate(DelegateTask taskEntity, int status) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String sql0 =
                    "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM  ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = '"
                        + taskEntity.getProcessInstanceId() + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                String assignee = taskEntity.getAssignee();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                actRuDetailModel.setCreateTime(taskEntity.getCreateTime());
                actRuDetailModel.setAssignee(assignee);
                actRuDetailModel.setAssigneeName(orgUnit.getName());
                actRuDetailModel.setDeptId(orgUnit.getParentId());
                actRuDetailModel.setLastTime(new Date());
                actRuDetailModel.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
                actRuDetailModel.setTaskDefKey(taskEntity.getTaskDefinitionKey());
                actRuDetailModel.setTaskDefName(taskEntity.getName());
                actRuDetailModel.setExecutionId(taskEntity.getExecutionId());
                actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                actRuDetailModel.setStatus(status);
                actRuDetailModel.setTaskId(taskEntity.getId());
                actRuDetailModel.setStarted(true);
                actRuDetailModel.setEnded(false);
                actRuDetailModel.setDeleted(false);
                actRuDetailModel
                    .setStartTime(list.isEmpty() ? sdf.format(new Date()) : list.get(0).get("START_TIME_").toString());
                actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                /**
                 * 出差委托时，会更改任务的办理人，原办理人变成了owner，所有这里要删除原办理人对应的流程参与详情
                 */
                String owner = taskEntity.getOwner();
                if (StringUtils.isNotBlank(owner)) {
                    actRuDetailApi.removeByProcessSerialNumberAndAssignee(tenantId, processSerialNumber, owner);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################Task4ActRuDetaillService:saveOrUpdate保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId(), e);
        }
    }

    /**
     * 单任务节点签收时，当前任务的办理人的待办保留，其他人的待办改为在办
     *
     * @param taskEntity 任务实体
     */
    public void saveOrUpdate4DoSign(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String sql0 =
                    "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM  ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = '"
                        + taskEntity.getProcessInstanceId() + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);

                String assignee = taskEntity.getAssignee();
                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (IdentityLink link : linkSet) {
                    String userId = link.getUserId();
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    actRuDetailModel.setCreateTime(sdf.parse(sdf.format(taskEntity.getCreateTime())));
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(orgUnit.getName());
                    actRuDetailModel.setDeptId(orgUnit.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setTaskDefKey(taskEntity.getTaskDefinitionKey());
                    actRuDetailModel.setTaskDefName(taskEntity.getName());
                    actRuDetailModel.setExecutionId(taskEntity.getExecutionId());
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    actRuDetailModel.setDeleted(false);
                    actRuDetailModel.setSignStatus(ActRuDetailSignStatusEnum.DONE.getValue());
                    actRuDetailModel.setStartTime(list.get(0).get("START_TIME_").toString());
                    if (assignee.equals(userId)) {
                        actRuDetailModel.setStatus(0);
                    } else {
                        actRuDetailModel.setStatus(1);
                    }
                    actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################saveOrUpdate4DoSign抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId(), e);
        }
    }

    /**
     * <签收节点还没有签收的时候,如果被重定向,则把待签收人都设置为在办>
     *
     * @param taskEntity 任务实体
     */
    public void saveOrUpdate4Reposition(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                String sql0 =
                    "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM  ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = '"
                        + taskEntity.getProcessInstanceId() + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (IdentityLink link : linkSet) {
                    String userId = link.getUserId();
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    actRuDetailModel.setCreateTime(sdf.parse(sdf.format(taskEntity.getCreateTime())));
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(orgUnit.getName());
                    actRuDetailModel.setDeptId(orgUnit.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
                    actRuDetailModel.setTaskDefKey(taskEntity.getTaskDefinitionKey());
                    actRuDetailModel.setTaskDefName(taskEntity.getName());
                    actRuDetailModel.setExecutionId(taskEntity.getExecutionId());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    actRuDetailModel.setDeleted(false);
                    actRuDetailModel.setStatus(1);
                    actRuDetailModel.setStartTime(list.get(0).get("START_TIME_").toString());
                    actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################saveOrUpdate4Reposition抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId());
        }
    }

    /**
     * 单任务节点选择多个人发送时，只会产生create事件，所以这里把所有候选人都生成待办
     *
     * @param taskEntity 任务实体
     * @param status 状态
     */
    public void saveOrUpdate4Sign(DelegateTask taskEntity, int status) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String sql0 =
                    "SELECT SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM  ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = '"
                        + taskEntity.getProcessInstanceId() + "'";
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                StringBuffer names = new StringBuffer();
                List<ActRuDetailModel> ardList = new ArrayList<>();
                taskEntity.getCandidates().forEach(link -> {
                    String userId = link.getUserId();
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    try {
                        actRuDetailModel.setCreateTime(sdf.parse(sdf.format(taskEntity.getCreateTime())));
                    } catch (ParseException e) {
                        LOGGER.error("格式化时间失败！");
                    }
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(orgUnit.getName());
                    actRuDetailModel.setDeptId(orgUnit.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
                    actRuDetailModel.setTaskDefKey(taskEntity.getTaskDefinitionKey());
                    actRuDetailModel.setTaskDefName(taskEntity.getName());
                    actRuDetailModel.setExecutionId(taskEntity.getExecutionId());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setStatus(status);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    actRuDetailModel.setDeleted(false);
                    actRuDetailModel.setStartTime(list.get(0).get("START_TIME_").toString());
                    actRuDetailModel.setSignStatus(ActRuDetailSignStatusEnum.TODO.getValue());
                    if (ardList.isEmpty()) {
                        names.append(orgUnit.getName());
                    } else {
                        names.append("、").append(orgUnit.getName());
                    }
                    ardList.add(actRuDetailModel);
                });
                ardList.forEach(ard -> {
                    ard.setAssigneeName(names.toString());
                    actRuDetailApi.saveOrUpdate(tenantId, ard);
                });
            }
        } catch (Exception e) {
            LOGGER.warn(
                "##########################saveOrUpdate4Sign抢占式节点-保存流程流转信息失败-taskId:{}##########################",
                taskEntity.getId());
        }
    }
}