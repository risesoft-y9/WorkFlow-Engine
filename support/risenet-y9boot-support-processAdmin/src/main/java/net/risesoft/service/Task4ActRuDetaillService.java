package net.risesoft.service;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "task4ActRuDetaillService")
@Slf4j
public class Task4ActRuDetaillService {

    @Autowired
    ActRuDetailApi actRuDetailApi;

    @Autowired
    PositionApi positionApi;

    public void saveOrUpdate(DelegateTask taskEntity, int status) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                String assignee = taskEntity.getAssignee();

                Position position = positionApi.getPosition(tenantId, assignee);
                ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                actRuDetailModel.setCreateTime(taskEntity.getCreateTime());
                actRuDetailModel.setAssignee(assignee);
                actRuDetailModel.setAssigneeName(position.getName());
                actRuDetailModel.setDeptId(position.getParentId());
                actRuDetailModel.setLastTime(new Date());
                actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                actRuDetailModel.setStatus(status);
                actRuDetailModel.setTaskId(taskEntity.getId());
                actRuDetailModel.setStarted(true);
                actRuDetailModel.setEnded(false);
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
            LOGGER.warn("##########################Task4ActRuDetaillService:saveOrUpdate保存流程流转信息失败-taskId:{}##########################", taskEntity.getId(), e);
        }
    }

    /**
     * 单任务节点签收时，当前任务的办理人的待办保留，其他人的待办改为在办
     *
     * @param taskEntity
     */
    public void saveOrUpdate4DoSign(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String assignee = taskEntity.getAssignee();
                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                for (IdentityLink link : linkSet) {
                    String userId = link.getUserId();
                    Position position = positionApi.getPosition(tenantId, userId);
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    actRuDetailModel.setCreateTime(taskEntity.getCreateTime());
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(position.getName());
                    actRuDetailModel.setDeptId(position.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    if (assignee.equals(userId)) {
                        actRuDetailModel.setStatus(0);
                    } else {
                        actRuDetailModel.setStatus(1);
                    }
                    actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("##########################saveOrUpdate4DoSign抢占式节点-保存流程流转信息失败-taskId:{}##########################", taskEntity.getId(), e);
        }
    }

    /**
     * <签收节点还没有签收的时候,如果被重定向,则把待签收人都设置为在办>
     *
     * @param taskEntity
     */
    public void saveOrUpdate4Reposition(DelegateTask taskEntity) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                for (IdentityLink link : linkSet) {
                    String userId = link.getUserId();
                    Position position = positionApi.getPosition(tenantId, userId);
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    actRuDetailModel.setCreateTime(taskEntity.getCreateTime());
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(position.getName());
                    actRuDetailModel.setDeptId(position.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    actRuDetailModel.setStatus(1);
                    actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("##########################saveOrUpdate4Reposition抢占式节点-保存流程流转信息失败-taskId:{}##########################", taskEntity.getId());
        }
    }

    /**
     * 单任务节点选择多个人发送时，只会产生create事件，所以这里把所有候选人都生成待办
     *
     * @param taskEntity
     * @param status
     */
    public void saveOrUpdate4Sign(DelegateTask taskEntity, int status) {
        try {
            String tenantId = (String)taskEntity.getVariable(SysVariables.TENANTID);
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);

                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESSSERIALNUMBER);
                Set<IdentityLink> linkSet = taskEntity.getCandidates();
                for (IdentityLink link : linkSet) {
                    String userId = link.getUserId();
                    Position position = positionApi.getPosition(tenantId, userId);
                    ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
                    actRuDetailModel.setCreateTime(taskEntity.getCreateTime());
                    actRuDetailModel.setAssignee(userId);
                    actRuDetailModel.setAssigneeName(position.getName());
                    actRuDetailModel.setDeptId(position.getParentId());
                    actRuDetailModel.setLastTime(new Date());
                    actRuDetailModel.setProcessDefinitionKey(taskEntity.getProcessDefinitionId().split(":")[0]);
                    actRuDetailModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    actRuDetailModel.setProcessSerialNumber(processSerialNumber);
                    actRuDetailModel.setStatus(status);
                    actRuDetailModel.setTaskId(taskEntity.getId());
                    actRuDetailModel.setStarted(true);
                    actRuDetailModel.setEnded(false);
                    actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("##########################saveOrUpdate4Sign抢占式节点-保存流程流转信息失败-taskId:{}##########################", taskEntity.getId());
        }
    }
}