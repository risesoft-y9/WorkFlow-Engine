package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.TaskTimeConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.TaskTimeConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@EnableAsync
@Service(value = "asyncUtilService")
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class AsyncUtilService {

    private final ErrorLogService errorLogService;

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final DocumentService documentService;

    private final VariableApi variableApi;

    private final TaskTimeConfService taskTimeConfService;

    /**
     * 异步循环发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     */
    @Async
    public void loopSending(final String tenantId, final String orgUnitId, final String itemId,
        final String processInstanceId) {
        String taskDefinitionKey = "";
        String taskId = "";
        try {
            // Thread.sleep(5000);// 延时5秒执行
            Y9LoginUserHolder.setTenantId(tenantId);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
            Y9LoginUserHolder.setOrgUnit(orgUnit);
            List<TaskModel> taskModelList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (taskModelList != null && !taskModelList.isEmpty()) {
                TaskModel taskModel = taskModelList.get(0);
                taskDefinitionKey = taskModel.getTaskDefinitionKey();
                String processDefinitionId = taskModel.getProcessDefinitionId();
                taskId = taskModel.getId();
                TaskTimeConf taskTimeConf = taskTimeConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                    processDefinitionId, taskDefinitionKey);
                if (taskTimeConf != null && taskTimeConf.getLeastTime() != null && taskTimeConf.getLeastTime() > 0) {
                    Thread.sleep(taskTimeConf.getLeastTime());// 延时执行
                } else {
                    Thread.sleep(5000);// 默认延时5秒执行
                }
                String stopProcess =
                    variableApi.getVariableByProcessInstanceId(tenantId, processInstanceId, "stopProcess").getData();
                if (taskDefinitionKey.contains("skip_") && (stopProcess == null || "false".equals(stopProcess))) {// 当前任务不停止且自动跳过

                    List<TargetModel> targetModelList =
                        processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefinitionKey).getData();
                    if (targetModelList != null && !targetModelList.isEmpty()) {
                        TargetModel targetModel = targetModelList.get(0);
                        String routeToTaskId = targetModel.getTaskDefKey();
                        List<
                            String> userChoiceList =
                                documentService
                                    .parserUser(itemId, processDefinitionId, routeToTaskId,
                                        targetModel.getTaskDefName(), processInstanceId, targetModel.getMultiInstance())
                                    .getData();
                        if (userChoiceList != null && !userChoiceList.isEmpty()) {
                            String userChoice = "";
                            for (String userId : userChoiceList) {
                                userChoice = Y9Util.genCustomStr(userChoice, "6:" + userId, ";");
                            }
                            Y9Result<String> y9Result1 =
                                documentService.forwarding(taskId, "true", userChoice, routeToTaskId, "");
                            if (y9Result1.isSuccess()) {
                                // 异步循环发送
                                this.loopSending(tenantId, orgUnitId, itemId, processInstanceId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOGGER.info("*****loopSending循环发送发生异常*****");
            String time = sdf.format(new Date());
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            // 保存发送错误日志
            ErrorLog errorLog = new ErrorLog();
            errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLog.setCreateTime(time);
            errorLog.setErrorFlag("loopSending循环发送发生异常");
            errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
            errorLog.setExtendField("loopSending循环发送发生异常:" + taskDefinitionKey);
            errorLog.setProcessInstanceId(processInstanceId);
            errorLog.setTaskId(taskId);
            errorLog.setText(msg);
            errorLog.setUpdateTime(time);
            errorLogService.saveErrorLog(errorLog);
        }
    }

}
