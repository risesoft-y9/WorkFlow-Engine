package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.TaskTimeConf;
import net.risesoft.enums.ItemAdminAuditLogEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.config.TaskTimeConfService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "asyncUtilService")
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
@Slf4j
public class AsyncUtilServiceImpl implements AsyncUtilService {

    private final ErrorLogService errorLogService;

    private final PositionApi positionApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final DocumentService documentService;

    private final VariableApi variableApi;

    private final TaskTimeConfService taskTimeConfService;

    private final AsyncUtilService self;

    private final HistoricTaskApi historicTaskApi;

    private final ProcessParamService processParamService;

    private final ItemService itemService;

    private final RepositoryApi repositoryApi;

    private final HistoricProcessApi historicProcessApi;

    public AsyncUtilServiceImpl(
        ErrorLogService errorLogService,
        OrgUnitApi orgUnitApi,
        PositionApi positionApi,
        TaskApi taskApi,
        ProcessDefinitionApi processDefinitionApi,
        @Lazy DocumentService documentService,
        VariableApi variableApi,
        TaskTimeConfService taskTimeConfService,
        @Lazy AsyncUtilService self,
        HistoricTaskApi historicTaskApi,
        ProcessParamService processParamService,
        ItemService itemService,
        RepositoryApi repositoryApi,
        HistoricProcessApi historicProcessApi) {
        this.errorLogService = errorLogService;
        this.positionApi = positionApi;
        this.taskApi = taskApi;
        this.processDefinitionApi = processDefinitionApi;
        this.documentService = documentService;
        this.variableApi = variableApi;
        this.taskTimeConfService = taskTimeConfService;
        this.self = self;
        this.historicTaskApi = historicTaskApi;
        this.processParamService = processParamService;
        this.itemService = itemService;
        this.repositoryApi = repositoryApi;
        this.historicProcessApi = historicProcessApi;
    }

    /**
     * 创建错误日志
     */
    private ErrorLog createErrorLog(String errorFlag, String extendField, String processInstanceId, String taskId,
        String text) {
        String time = Y9DateTimeUtils.formatCurrentDateTime();
        ErrorLog errorLog = new ErrorLog();
        errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        errorLog.setErrorFlag(errorFlag);
        errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
        errorLog.setExtendField(extendField);
        errorLog.setProcessInstanceId(processInstanceId);
        errorLog.setTaskId(taskId);
        errorLog.setText(text);
        return errorLog;
    }

    @Override
    public void deleteAssociatedFileAuditLog(String processInstanceIds) {
        try {
            String[] processInstanceIdArray = processInstanceIds.split(SysVariables.COMMA);
            for (String processInstanceId : processInstanceIdArray) {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.ASSOCIATED_FILE_DELETE.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.ASSOCIATED_FILE_DELETE.getDescription(),
                        processParam.getTitle()))
                    .objectId(processInstanceId)
                    .oldObject(processParam)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("保存关联文件审计日志失败 processInstanceIds：" + processInstanceIds, e);
        }
    }

    /**
     * 延时执行
     */
    private void executeWithDelay(String itemId, String taskDefinitionKey, String processDefinitionId)
        throws Exception {
        TaskTimeConf taskTimeConf = taskTimeConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefinitionKey);
        if (taskTimeConf != null && taskTimeConf.getLeastTime() != null && taskTimeConf.getLeastTime() > 0) {
            Thread.sleep(taskTimeConf.getLeastTime());
        } else {
            Thread.sleep(5000); // 默认延时5秒执行
        }
    }

    /**
     * 获取当前任务信息
     */
    private TaskInfo getCurrentTaskInfo(String processInstanceId) {
        List<TaskModel> taskModelList =
            taskApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
        if (taskModelList != null && !taskModelList.isEmpty()) {
            TaskModel taskModel = taskModelList.get(0);
            return new TaskInfo(taskModel.getTaskDefinitionKey(), taskModel.getId(),
                taskModel.getProcessDefinitionId());
        }
        return null;
    }

    /**
     * 处理自动跳过逻辑
     */
    private void handleAutoSkip(String orgUnitId, String itemId, String processInstanceId, String taskDefinitionKey,
        String taskId, String processDefinitionId) {
        String stopProcess = variableApi.getVariableByProcessInstanceId(processInstanceId, "stopProcess").getData();
        if (taskDefinitionKey.contains("skip_") && (stopProcess == null || "false".equals(stopProcess))) {
            List<TargetModel> targetModelList = processDefinitionApi
                .getTargetNodes(Y9LoginUserHolder.getTenantId(), processDefinitionId, taskDefinitionKey)
                .getData();
            if (targetModelList != null && !targetModelList.isEmpty()) {
                TargetModel targetModel = targetModelList.get(0);
                processTargetNode(orgUnitId, itemId, processInstanceId, taskId, targetModel);
            }
        }
    }

    /**
     * 处理一般异常
     */
    private void handleGeneralException(Exception e, String processInstanceId, String taskDefinitionKey,
        String taskId) {
        LOGGER.info("*****loopSending循环发送发生异常*****");
        final Writer result = new StringWriter();
        final PrintWriter print = new PrintWriter(result);
        e.printStackTrace(print);
        String msg = result.toString();
        ErrorLog errorLog = createErrorLog("loopSending循环发送发生异常", "loopSending循环发送发生异常:" + taskDefinitionKey,
            processInstanceId, taskId, msg);
        errorLogService.saveErrorLog(errorLog);
    }

    /**
     * 处理中断异常
     */
    private void handleInterruptedException(String processInstanceId, String taskDefinitionKey, String taskId) {
        Thread.currentThread().interrupt();
        LOGGER.info("*****loopSending循环发送被中断*****");
        ErrorLog errorLog = createErrorLog("loopSending循环发送被中断", "loopSending循环发送被中断:" + taskDefinitionKey,
            processInstanceId, taskId, "Thread was interrupted during execution");
        errorLogService.saveErrorLog(errorLog);
    }

    /**
     * 处理子流程用户列表
     */
    private void handleSubProcessUsers(String processInstanceId, String multiInstance, List<String> userChoiceList) {
        String subProcessStr = variableApi.getVariableByProcessInstanceId(processInstanceId, "subProcessNum").getData();
        if (subProcessStr != null && SysVariables.PARALLEL.equals(multiInstance)) {
            String userChoice = userChoiceList.get(0);
            int subProcessNum = Integer.parseInt(subProcessStr);
            if (subProcessNum > 1 && userChoiceList.size() == 1) {
                for (int i = 1; i < subProcessNum; i++) {
                    userChoiceList.add(userChoice);
                }
            }
        }
    }

    /**
     * 异步循环发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     */
    @Async
    @Override
    public void loopSending(final String tenantId, final String orgUnitId, final String itemId,
        final String processInstanceId) {
        String taskDefinitionKey = "";
        String taskId = "";
        try {
            // 设置登录用户上下文
            setupUserContext(tenantId, orgUnitId);
            // 获取当前任务信息
            TaskInfo taskInfo = getCurrentTaskInfo(processInstanceId);
            if (taskInfo == null) {
                return;
            }
            taskDefinitionKey = taskInfo.getTaskDefinitionKey();
            taskId = taskInfo.getTaskId();
            String processDefinitionId = taskInfo.getProcessDefinitionId();
            // 延时执行
            executeWithDelay(itemId, taskDefinitionKey, processDefinitionId);
            // 检查是否需要自动跳过并处理
            handleAutoSkip(orgUnitId, itemId, processInstanceId, taskDefinitionKey, taskId, processDefinitionId);
        } catch (InterruptedException e) {
            handleInterruptedException(processInstanceId, taskDefinitionKey, taskId);
        } catch (Exception e) {
            handleGeneralException(e, processInstanceId, taskDefinitionKey, taskId);
        }
    }

    /**
     * 处理目标节点
     */
    private void processTargetNode(String orgUnitId, String itemId, String processInstanceId, String taskId,
        TargetModel targetModel) {
        String routeToTaskId = targetModel.getTaskDefKey();
        String multiInstance = targetModel.getMultiInstance();
        List<String> userChoiceList =
            documentService
                .parserUser(itemId, targetModel.getProcessDefinitionId(), routeToTaskId, targetModel.getTaskDefName(),
                    processInstanceId, multiInstance)
                .getData();
        if (userChoiceList != null && !userChoiceList.isEmpty()) {
            handleSubProcessUsers(processInstanceId, multiInstance, userChoiceList);
            Y9Result<String> result = documentService.start4Forwarding(taskId, routeToTaskId, "", userChoiceList);
            if (result.isSuccess()) {
                // 异步循环发送
                self.loopSending(Y9LoginUserHolder.getTenantId(), orgUnitId, itemId, processInstanceId);
            }
        }
    }

    @Async
    @Override
    @Transactional
    public void quickSendAuditLog(String tenantId, String orgUnitId, String itemId, String taskKey, String assignee,
        String optType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Item item = itemService.findById(itemId);
            ProcessDefinitionModel processDefinitionModel =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, item.getWorkflowGuid()).getData();
            List<TargetModel> targetModelList =
                processDefinitionApi.getNodes(tenantId, processDefinitionModel.getId()).getData();
            TargetModel targetModel = targetModelList.stream()
                .filter(model -> taskKey.equals(model.getTaskDefKey()))
                .findFirst()
                .orElse(null);
            StringBuilder sendPersons = new StringBuilder();
            String[] ids = assignee.split(SysVariables.COMMA);
            for (String id : ids) {
                String orgId = id.split(SysVariables.COLON)[1];
                Position position = positionApi.get(tenantId, orgId).getData();
                sendPersons.append(position.getName()).append(SysVariables.COMMA);
            }
            String action = ItemAdminAuditLogEnum.QUICK_SEND_ADD.getAction();
            String description = Y9StringUtil.format(ItemAdminAuditLogEnum.QUICK_SEND_ADD.getDescription(),
                item.getName(), targetModel.getTaskDefName(), sendPersons.toString());
            if (optType.equals(ItemAdminAuditLogEnum.OPTTYPE_UPDATE.getAction())) {
                action = ItemAdminAuditLogEnum.QUICK_SEND_UPDATE.getAction();
                description = Y9StringUtil.format(ItemAdminAuditLogEnum.QUICK_SEND_UPDATE.getDescription(),
                    item.getName(), targetModel.getTaskDefName(), sendPersons.toString());
            }

            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(itemId)
                .oldObject(item)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存快捷发送审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void remindMsgAuditLog(String tenantId, String orgUnitId, String taskIds, String processInstanceId,
        Boolean process, String arriveTaskKey, String completeTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String[] ids = taskIds.split(",");
            for (String id : ids) {
                TaskModel taskModel = taskApi.findById(tenantId, id).getData();
                if (StringUtils.isNotBlank(taskIds)) {
                    AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                        .action(ItemAdminAuditLogEnum.REMINDERMSG_SET_TASKCOMPLETE.getAction())
                        .description(Y9StringUtil.format(
                            ItemAdminAuditLogEnum.REMINDERMSG_SET_TASKCOMPLETE.getDescription(), taskModel.getName()))
                        .objectId(id)
                        .oldObject(taskModel)
                        .currentObject(null)
                        .build();
                    Y9Context.publishEvent(auditLogEvent);
                }

                if (Boolean.TRUE.equals(process)) {
                    AuditLogEvent auditLogEvent =
                        AuditLogEvent.builder()
                            .action(ItemAdminAuditLogEnum.REMINDERMSG_SET_PROCESSCOMPLETE.getAction())
                            .description(Y9StringUtil.format(
                                ItemAdminAuditLogEnum.REMINDERMSG_SET_PROCESSCOMPLETE.getDescription(),
                                taskModel.getName()))
                            .objectId(id)
                            .oldObject(taskModel)
                            .currentObject(null)
                            .build();
                    Y9Context.publishEvent(auditLogEvent);
                }

            }
            if (StringUtils.isNotBlank(arriveTaskKey)) {
                String[] arriveTaskKeys = arriveTaskKey.split(",");
                for (String arriveKey : arriveTaskKeys) {
                    String processNodeKey = arriveKey.split(SysVariables.COLON)[0];
                    String processNodeName = arriveKey.split(SysVariables.COLON)[1];
                    AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                        .action(ItemAdminAuditLogEnum.REMINDERMSG_SET_NODEARRIVE.getAction())
                        .description(Y9StringUtil
                            .format(ItemAdminAuditLogEnum.REMINDERMSG_SET_NODEARRIVE.getDescription(), processNodeName))
                        .objectId(processNodeKey)
                        .oldObject(processNodeName)
                        .currentObject(null)
                        .build();
                    Y9Context.publishEvent(auditLogEvent);
                }
            }
            if (StringUtils.isNotBlank(completeTaskKey)) {
                String[] completeTaskKeys = completeTaskKey.split(",");
                for (String completeKey : completeTaskKeys) {
                    String processNodeKey = completeKey.split(SysVariables.COLON)[0];
                    String processNodeName = completeKey.split(SysVariables.COLON)[1];
                    AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                        .action(ItemAdminAuditLogEnum.REMINDERMSG_SET_NODECOMPLETE.getAction())
                        .description(Y9StringUtil.format(
                            ItemAdminAuditLogEnum.REMINDERMSG_SET_NODECOMPLETE.getDescription(), processNodeName))
                        .objectId(processNodeKey)
                        .oldObject(processNodeName)
                        .currentObject(null)
                        .build();
                    Y9Context.publishEvent(auditLogEvent);
                }
            }
        } catch (Exception e) {
            LOGGER.error("保存消息提醒审计日志失败", e);
        }

    }

    @Async
    @Transactional
    @Override
    public void saveAssociatedFileAuditLog(String tenantId, String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String[] processInstanceIdArray = processInstanceIds.split(SysVariables.COMMA);
            for (String processInstanceId : processInstanceIdArray) {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.ASSOCIATED_FILE_SAVE.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.ASSOCIATED_FILE_SAVE.getDescription(),
                        processParam.getTitle()))
                    .objectId(processInstanceId)
                    .oldObject(processParam)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("保存关联文件审计日志失败 processInstanceIds：" + processInstanceIds, e);
        }
    }

    @Async
    @Override
    @Transactional
    public void sendAuditLog(String tenantId, String title, String userChoice) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String choice : userChoices) {
                String[] parts = choice.split(SysVariables.COLON);
                String userId = choice;
                if (parts.length == 2) {
                    userId = parts[1];
                }
                Position position = positionApi.get(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SEND.getDescription(), title,
                        position.getName()))
                    .objectId(userId)
                    .oldObject(position)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("保存发送审计日志失败", e);
        }
    }

    @Async
    @Transactional
    @Override
    public void sendAuditLog(String tenantId, String title, List<String> userIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            for (String userId : userIdList) {
                Position position = positionApi.get(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SEND.getDescription(), title,
                        position.getName()))
                    .objectId(userId)
                    .oldObject(position)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("保存发送审计日志失败 userIdList", e);
        }
    }

    /**
     * 设置用户上下文
     */
    private void setupUserContext(String tenantId, String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, orgUnitId).getData();
        Y9FlowableHolder.setPosition(position);
    }

    @Async
    @Transactional
    @Override
    public void submitSendAuditLog(String tenantId, String title, List<String> userIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            for (String userId : userIdList) {
                Position position = positionApi.get(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SUBMIT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SUBMIT_SEND.getDescription(), title,
                        position.getName()))
                    .objectId(userId)
                    .oldObject(position)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("保存办件提交发送审计日志失败 userIdList", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void takeBackTwoTaskDefKeyAuditLog(String tenantId, String orgUnitId, String taskId, String targetTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(taskId).getData();
            List<TargetModel> targetModelList =
                processDefinitionApi.getNodes(tenantId, historicTaskInstanceModel.getProcessDefinitionId()).getData();
            TargetModel targetModel = targetModelList.stream()
                .filter(model -> targetTaskKey.equals(model.getTaskDefKey()))
                .findFirst()
                .orElse(null);
            ProcessParam processParam =
                processParamService.findByProcessInstanceId(historicTaskInstanceModel.getProcessInstanceId());
            Position position = positionApi.get(tenantId, orgUnitId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(ItemAdminAuditLogEnum.BUTTON_TAKEBACK_TASK_DEF_KEY.getAction())
                .description(Y9StringUtil.format(ItemAdminAuditLogEnum.BUTTON_TAKEBACK_TASK_DEF_KEY.getDescription(),
                    position.getName(), historicTaskInstanceModel.getName(), processParam.getTitle(),
                    targetModel.getTaskDefName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存指定任务收回审计日志失败", e);
        }
    }

    /**
     * 任务信息内部类
     */
    @Getter
    private static class TaskInfo {
        private final String taskDefinitionKey;
        private final String taskId;
        private final String processDefinitionId;

        public TaskInfo(String taskDefinitionKey, String taskId, String processDefinitionId) {
            this.taskDefinitionKey = taskDefinitionKey;
            this.taskId = taskId;
            this.processDefinitionId = processDefinitionId;
        }
    }
}