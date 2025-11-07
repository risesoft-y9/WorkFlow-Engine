package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.TaskTimeConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.config.TaskTimeConfService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final DocumentService documentService;

    private final VariableApi variableApi;

    private final TaskTimeConfService taskTimeConfService;

    private final AsyncUtilService self;

    public AsyncUtilServiceImpl(
        ErrorLogService errorLogService,
        OrgUnitApi orgUnitApi,
        TaskApi taskApi,
        ProcessDefinitionApi processDefinitionApi,
        DocumentService documentService,
        VariableApi variableApi,
        TaskTimeConfService taskTimeConfService,
        @Lazy AsyncUtilService self) {
        this.errorLogService = errorLogService;
        this.orgUnitApi = orgUnitApi;
        this.taskApi = taskApi;
        this.processDefinitionApi = processDefinitionApi;
        this.documentService = documentService;
        this.variableApi = variableApi;
        this.taskTimeConfService = taskTimeConfService;
        this.self = self;
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
            TaskInfo taskInfo = getCurrentTaskInfo(tenantId, processInstanceId);
            if (taskInfo == null) {
                return;
            }
            taskDefinitionKey = taskInfo.getTaskDefinitionKey();
            taskId = taskInfo.getTaskId();
            String processDefinitionId = taskInfo.getProcessDefinitionId();
            // 延时执行
            executeWithDelay(itemId, taskDefinitionKey, processDefinitionId);
            // 检查是否需要自动跳过并处理
            handleAutoSkip(tenantId, orgUnitId, itemId, processInstanceId, taskDefinitionKey, taskId,
                processDefinitionId);
        } catch (InterruptedException e) {
            handleInterruptedException(processInstanceId, taskDefinitionKey, taskId);
        } catch (Exception e) {
            handleGeneralException(e, processInstanceId, taskDefinitionKey, taskId);
        }
    }

    /**
     * 设置用户上下文
     */
    private void setupUserContext(String tenantId, String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9FlowableHolder.setOrgUnit(orgUnit);
    }

    /**
     * 获取当前任务信息
     */
    private TaskInfo getCurrentTaskInfo(String tenantId, String processInstanceId) {
        List<TaskModel> taskModelList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (taskModelList != null && !taskModelList.isEmpty()) {
            TaskModel taskModel = taskModelList.get(0);
            return new TaskInfo(taskModel.getTaskDefinitionKey(), taskModel.getId(),
                taskModel.getProcessDefinitionId());
        }
        return null;
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
     * 处理自动跳过逻辑
     */
    private void handleAutoSkip(String tenantId, String orgUnitId, String itemId, String processInstanceId,
        String taskDefinitionKey, String taskId, String processDefinitionId) {
        String stopProcess =
            variableApi.getVariableByProcessInstanceId(tenantId, processInstanceId, "stopProcess").getData();
        if (taskDefinitionKey.contains("skip_") && (stopProcess == null || "false".equals(stopProcess))) {
            List<TargetModel> targetModelList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefinitionKey).getData();
            if (targetModelList != null && !targetModelList.isEmpty()) {
                TargetModel targetModel = targetModelList.get(0);
                processTargetNode(tenantId, orgUnitId, itemId, processInstanceId, taskId, targetModel);
            }
        }
    }

    /**
     * 处理目标节点
     */
    private void processTargetNode(String tenantId, String orgUnitId, String itemId, String processInstanceId,
        String taskId, TargetModel targetModel) {
        String routeToTaskId = targetModel.getTaskDefKey();
        String multiInstance = targetModel.getMultiInstance();
        List<String> userChoiceList =
            documentService
                .parserUser(itemId, targetModel.getProcessDefinitionId(), routeToTaskId, targetModel.getTaskDefName(),
                    processInstanceId, multiInstance)
                .getData();
        if (userChoiceList != null && !userChoiceList.isEmpty()) {
            handleSubProcessUsers(tenantId, processInstanceId, multiInstance, userChoiceList);
            Y9Result<String> result = documentService.start4Forwarding(taskId, routeToTaskId, "", userChoiceList);
            if (result.isSuccess()) {
                // 异步循环发送
                self.loopSending(tenantId, orgUnitId, itemId, processInstanceId);
            }
        }
    }

    /**
     * 处理子流程用户列表
     */
    private void handleSubProcessUsers(String tenantId, String processInstanceId, String multiInstance,
        List<String> userChoiceList) {
        String subProcessStr =
            variableApi.getVariableByProcessInstanceId(tenantId, processInstanceId, "subProcessNum").getData();
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