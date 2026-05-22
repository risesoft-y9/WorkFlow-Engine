package net.risesoft.service.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.FlowableUiAuditLogEnum;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9StringUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncUtilServiceImpl implements AsyncUtilService {

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    private final HistoricTaskApi historicTaskApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final VariableApi variableApi;

    private final ActRuDetailApi actRuDetailApi;

    @Async
    @Override
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSongApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void resumeToDoAuditLog(String tenantId, String processInstanceId, String title) {
        try {
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.DOCUMENT_RESUMETODO.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.DOCUMENT_RESUMETODO.getDescription(), title))
                .objectId(processInstanceId)
                .oldObject(processInstanceId)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存恢复待办审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void claimAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存签收任务审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void sendStarterAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存发送拟稿人审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void handleParallelAuditLog(String tenantId, String taskName, String processInstanceId) {
        try {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_HANDLE_PARALLEL.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_HANDLE_PARALLEL.getDescription(),
                    processParamModel.getTitle(), taskName))
                .objectId(processInstanceId)
                .oldObject(processParamModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存并行办理审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void handleSerialAuditLog(String tenantId, String taskName, String processInstanceId) {
        try {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_HANDLE_SERIAL.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_HANDLE_SERIAL.getDescription(),
                    processParamModel.getTitle(), taskName))
                .objectId(processInstanceId)
                .oldObject(processParamModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存串行办理审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void reAssignAuditLog(String tenantId, String taskId, String orgUnitId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_REASSIGN.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_REASSIGN.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存任务委托审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void refuseClaimAuditLog(String tenantId, String taskId, String orgUnitId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_REFUSE_CLAIM.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_REFUSE_CLAIM.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存任务拒签审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void refuseClaimRollbackAuditLog(String tenantId, String taskId, String orgUnitId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_REFUSE_CLAIM_ROLLBACK.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_REFUSE_CLAIM_ROLLBACK.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存最后一人任务拒签审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void repositionAuditLog(String tenantId, String orgUnitId, String taskId, String targetTaskKey,
        List<String> users) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            String userNames = "";
            for (String user : users) {
                OrgUnit orgUser = orgUnitApi.getOrgUnit(tenantId, user).getData();
                userNames += orgUser.getName() + "，";
            }
            List<TargetModel> targetModelList =
                processDefinitionApi.getNodes(tenantId, historicTaskInstanceModel.getProcessDefinitionId()).getData();
            TargetModel targetModel = targetModelList.stream()
                .filter(model -> targetTaskKey.equals(model.getTaskDefKey()))
                .findFirst()
                .orElse(null);
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_REPOSITION.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_REPOSITION.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName(), targetModel.getTaskDefName(), userNames))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存重定位审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void rollBackTwoHistoryAuditLog(String tenantId, String orgUnitId, String taskId, String targetTaskKey,
        List<String> users) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            String userNames = "";
            for (String user : users) {
                OrgUnit orgUser = orgUnitApi.getOrgUnit(tenantId, user).getData();
                userNames += orgUser.getName() + "，";
            }
            List<TargetModel> targetModelList =
                processDefinitionApi.getNodes(tenantId, historicTaskInstanceModel.getProcessDefinitionId()).getData();
            TargetModel targetModel = targetModelList.stream()
                .filter(model -> targetTaskKey.equals(model.getTaskDefKey()))
                .findFirst()
                .orElse(null);
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_ROLLBACKTWO_HISTORY.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_ROLLBACKTWO_HISTORY.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName(), targetModel.getTaskDefName(), userNames))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存多步退回审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void rollbackAuditLog(String tenantId, String orgUnitId, String taskId, String reason) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            HistoricTaskInstanceModel previousTask = historicTaskApi.getThePreviousTask(tenantId, taskId).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_ROLLBACK.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_ROLLBACK.getDescription(),
                    orgUnit.getName(), reason, historicTaskInstanceModel.getName(), previousTask.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存退回上一步审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void rollbackToSenderAuditLog(String tenantId, String orgUnitId, String taskId, String optType) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            HistoricTaskInstanceModel previousTask = historicTaskApi.getThePreviousTask(tenantId, taskId).getData();
            String taskSenderId =
                variableApi.getVariableLocal(tenantId, previousTask.getProcessInstanceId(), SysVariables.TASK_SENDER_ID)
                    .getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, historicTaskInstanceModel.getProcessInstanceId())
                    .getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            OrgUnit orgSender = orgUnitApi.getOrgUnit(tenantId, taskSenderId).getData();
            String action = FlowableUiAuditLogEnum.BUTTON_ROLLBACK_TO_SENDER.getAction();
            String description = Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_ROLLBACK_TO_SENDER.getDescription(),
                orgUnit.getName(), historicTaskInstanceModel.getName(), processParamModel.getTitle(),
                previousTask.getName(), orgSender.getName());
            if (optType.equals("sendback")) {
                action = FlowableUiAuditLogEnum.BUTTON_SEND_TO_SENDER.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_SEND_TO_SENDER.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName(), processParamModel.getTitle(),
                    previousTask.getName(), orgSender.getName());
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存退回发送人审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void rollbackToStartorAuditLog(String tenantId, String orgUnitId, String taskId, String optType) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            List<HistoricTaskInstanceModel> historicTaskInstanceModelList =
                historicTaskApi
                    .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId,
                        historicTaskInstanceModel.getProcessInstanceId(), "")
                    .getData();
            String startorId = historicTaskInstanceModelList.get(0).getAssignee();// 发起人id
            String startTaskName = historicTaskInstanceModelList.get(0).getName();// 发起任务名称
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, startorId).getData();// 操作人信息
            OrgUnit orgStartor = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();// 发起人信息
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, historicTaskInstanceModel.getProcessInstanceId())
                    .getData();
            String action = FlowableUiAuditLogEnum.BUTTON_ROLLBACK_TO_STARTOR.getAction();
            String description = Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_ROLLBACK_TO_STARTOR.getDescription(),
                orgUnit.getName(), historicTaskInstanceModel.getName(), processParamModel.getTitle(), startTaskName,
                orgStartor.getName());
            if (optType.equals("sendback")) {
                action = FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName(), processParamModel.getTitle(), startTaskName,
                    orgStartor.getName());
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存退回发起人审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void specialCompleteAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, historicTaskInstanceModel.getProcessInstanceId())
                    .getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_SPECIAL_COMPLETE.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_SPECIAL_COMPLETE.getDescription(),
                    processParamModel.getTitle(), orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存特殊办结审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void takebackAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, historicTaskInstanceModel.getProcessInstanceId())
                    .getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_TAKEBACK.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_TAKEBACK.getDescription(),
                    processParamModel.getTitle(), orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存任务收回审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void unclaimAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, historicTaskInstanceModel.getProcessInstanceId())
                    .getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_UNCLAIM.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_UNCLAIM.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName(), processParamModel.getTitle()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存撤销签收审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void deleteToDoAuditLog(String tenantId, String orgUnitId, String processSerialNumber) {
        try {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_DELETE_TODOS.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_DELETE_TODOS.getDescription(),
                    orgUnit.getName(), processParamModel.getTitle()))
                .objectId(processSerialNumber)
                .oldObject(processParamModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存删除待办审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void recoveryToDoAuditLog(String tenantId, String orgUnitId, String processSerialNumber) {
        try {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_RECOVERS.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_RECOVERS.getDescription(),
                    orgUnit.getName(), processParamModel.getTitle()))
                .objectId(processSerialNumber)
                .oldObject(processParamModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存恢复待办审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void removeToDoAuditLog(String tenantId, String orgUnitId, String processSerialNumber, String title) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_RECOVERS.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_RECOVERS.getDescription(),
                    orgUnit.getName(), title))
                .objectId(processSerialNumber)
                .oldObject(title)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存恢复待办审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void addMultiInstanceParallelAuditLog(String tenantId, String userName, String title, String taskId,
        String taskName, String users) {
        try {
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, users).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_ADD.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_ADD.getDescription(),
                    title, userName, taskName, orgUnit.getName()))
                .objectId(taskId)
                .oldObject(orgUnit)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存并行任务加签审批日志失败 error", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void addExecutionIdSequentialAuditLog(String tenantId, String userId, String processInstanceId,
        String taskId, List<String> users) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, userId).getData();
            String userNames = "";
            for (String user : users) {
                OrgUnit orgUser = orgUnitApi.getOrgUnit(tenantId, user).getData();
                userNames += orgUser.getName() + "，";
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_ADD.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_ADD.getDescription(),
                    processParamModel.getTitle(), orgUnit.getName(), historicTaskInstanceModel.getName(), userNames))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存串行任务加签审批日志失败 error", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void deleteMultiInstanceParallelAuditLog(String tenantId, String userId, String processInstanceId,
        String taskId, String users) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, userId).getData();
            OrgUnit orgUser = orgUnitApi.getOrgUnit(tenantId, users).getData();

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_REMOVE.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.MULTI_INSTANCE_PARALLEL_REMOVE.getDescription(),
                    processParamModel.getTitle(), orgUnit.getName(), historicTaskInstanceModel.getName(),
                    orgUser.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存并行任务减签审批日志失败 error", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void deleteMultiInstanceSequentialAuditLog(String tenantId, String userId, String processInstanceId,
        String taskId, String users) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            OrgUnit orgUnit = orgUnitApi.getPersonOrPosition(tenantId, userId).getData();
            OrgUnit orgUser = orgUnitApi.getOrgUnit(tenantId, users).getData();

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.MULTI_INSTANCE_SEQUENTIAL_REMOVE.getAction())
                .description(
                    Y9StringUtil.format(FlowableUiAuditLogEnum.MULTI_INSTANCE_SEQUENTIAL_REMOVE.getDescription(),
                        processParamModel.getTitle(), orgUnit.getName(), historicTaskInstanceModel.getName(),
                        orgUser.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存串行任务减签审批日志失败 error", e);
        }
    }

}
