package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.command.JumpSubProcessCommand;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service(value = "customTaskService")
public class CustomTaskServiceImpl implements CustomTaskService {

    private final TaskService taskService;

    private final HistoryService historyService;

    private final CustomProcessDefinitionService customProcessDefinitionService;

    private final CustomHistoricTaskService customHistoricTaskService;

    private final ManagementService managementService;

    private final RuntimeService runtimeService;

    private final ErrorLogApi errorLogApi;

    @Override
    @Transactional
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional
    public void complete(String taskId) {
        taskService.complete(taskId);
    }

    @Override
    @Transactional
    public void complete(String processInstanceId, String taskId) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String nodeType =
                    customProcessDefinitionService.getNodeType(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            HistoricProcessInstance historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (nodeType.equals(SysVariables.PARALLEL)) {
                List<Task> taskList = this.listByProcessInstanceId(processInstanceId);
                for (Task tTemp : taskList) {
                    if (!taskId.equals(tTemp.getId())) {
                        this.complete(tTemp.getId());
                        // 设置强制办理任务标识
                        customHistoricTaskService.setTenantId(tTemp.getId());
                    }
                }
            }
            String year = sdf.format(historicProcessInstance.getStartTime());
            /*
             * 1-备份正在运行的执行实例数据，回复待办的时候会用到，只记录最后一个任务办结前的数据
             */
            String sql0 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list0 = runtimeService.createNativeExecutionQuery().sql(sql0)
                    .parameter("PROC_INST_ID_", processInstanceId).list();
            if (!list0.isEmpty()) {
                // 备份数据已有，则先删除再重新插入备份
                String sql2 = "DELETE FROM FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
                runtimeService.createNativeExecutionQuery().sql(sql2).parameter("PROC_INST_ID_", processInstanceId)
                        .list();
            }
            String sql = "INSERT INTO FF_ACT_RU_EXECUTION_" + year
                    + " (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ from ACT_RU_EXECUTION T WHERE T.PROC_INST_ID_ = #{PROC_INST_ID_}";
            runtimeService.createNativeExecutionQuery().sql(sql).parameter("PROC_INST_ID_", processInstanceId).list();
            /*
             * 2-办结流程
             */
            String sql3 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list1 = runtimeService.createNativeExecutionQuery().sql(sql3)
                    .parameter("PROC_INST_ID_", processInstanceId).list();
            if (!list1.isEmpty()) {
                // 成功备份数据才办结
                String endNodeKey = customProcessDefinitionService.getEndNodeKeyByTaskId(taskId);
                Map<String, Object> vars = new HashMap<>(16);
                vars.put(SysVariables.ROUTETOTASKID, endNodeKey);
                this.completeWithVariables(taskId, vars);
                // 保存到数据中心，在流程办结监听执行
                // process4CompleteUtilService.saveToDataCenter(Y9LoginUserHolder.getTenantId(), year, position.getId(),
                // processInstanceId, personName);
            }
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_PROCESS_COMLETE);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("流程办结失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogApi.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                LOGGER.error("保存错误日志失败", e1);
            }
            LOGGER.error("流程办结失败", e);
            throw new Exception("CustomTaskServiceImpl complete error");
        }
    }

    @Override
    @Transactional
    public void completeSub(String taskId, List<String> userList) throws Exception {
        String processInstanceId = "";
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            processInstanceId = task.getProcessInstanceId();
            String nodeType =
                    customProcessDefinitionService.getNodeType(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (nodeType.equals(SysVariables.PARALLEL)) {
                List<Task> taskList = this.listByProcessInstanceId(task.getProcessInstanceId());
                for (Task tTemp : taskList) {
                    if (!taskId.equals(tTemp.getId())) {
                        this.complete(tTemp.getId());
                        // 设置强制办理任务标识
                        customHistoricTaskService.setTenantId(tTemp.getId());
                    }
                }
            }
            Map<String, Object> variables = new HashMap<>();
            String endNodeKey = customProcessDefinitionService.getEndNodeKeyByTaskId(taskId);
            variables.put(SysVariables.ROUTETOTASKID, endNodeKey);
            if (userList.size() == 1) {
                variables.put(SysVariables.USER, userList.stream().findFirst().get());
            }
            variables.put(SysVariables.USERS, userList);
            this.completeWithVariables(taskId, variables);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_PROCESS_COMLETE);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("流程办结失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogApi.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                LOGGER.error("保存错误日志失败", e1);
            }
            LOGGER.error("流程办结失败", e);
            throw new Exception("CustomTaskServiceImpl complete error");
        }
    }

    @Override
    @Transactional
    public void completeTaskWithoutAssignee(String processInstanceId) {
        List<Task> taskList = this.listByProcessInstanceId(processInstanceId);
        Task task = taskList.get(0);
        List<GatewayModel> parallelGatewayList = customProcessDefinitionService
                .listParallelGateway(task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
        String routeToTaskId = parallelGatewayList.get(0).getTaskDefKey();
        Map<String, Object> vars = new HashMap<>(16);
        vars.put(SysVariables.ROUTETOTASKID, routeToTaskId);
        for (Task t : taskList) {
            if (StringUtils.isEmpty(t.getAssignee())) {
                taskService.complete(t.getId(), vars);
            } else {
                taskService.setVariableLocal(t.getId(), SysVariables.ISPARALLEGATEWAYTASK, true);
            }
        }
    }

    @Override
    @Transactional
    public void completeWithVariables(String taskId, Map<String, Object> map) {
        taskService.complete(taskId, map, false);
    }

    @Override
    public TaskModel createWithVariables(String orgUnitId, Map<String, Object> vars, String routeToTaskId,
                                         List<String> orgUnitIdList) {
        String parentTaskId = (String) vars.get("parentTaskId");
        managementService
                .executeCommand(new JumpSubProcessCommand(parentTaskId, orgUnitId, vars, routeToTaskId, orgUnitIdList));
        return null;
    }

    @Override
    @Transactional
    public void delegateTask(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
    }

    @Override
    @Transactional
    public void deleteCandidateUser(String taskId, String userId) {
        taskService.deleteCandidateUser(taskId, userId);
    }

    @Override
    public List<Task> findAll() {
        return taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
    }

    @Override
    public Task findById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public Integer getCompleteTaskCount4Parallel(String taskId) {
        List<HistoricTaskInstance> list = new ArrayList<>();
        try {
            Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
            String multinstance = customProcessDefinitionService.getNodeType(currentTask.getProcessDefinitionId(),
                    currentTask.getTaskDefinitionKey());
            if (multinstance.equals(SysVariables.PARALLEL)) {
                List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(currentTask.getProcessInstanceId()).taskCreatedOn(currentTask.getCreateTime())
                        .list();
                for (HistoricTaskInstance entity : hisTaskList) {
                    // 由于并行任务的创建时间可能会有延迟，所以这里创建时间相差不超过2秒的，即为当前任务的所有并行任务
                    if (entity.getCreateTime().getTime() - currentTask.getCreateTime().getTime() > -2
                            && entity.getCreateTime().getTime() - currentTask.getCreateTime().getTime() < 2) {
                        list.add(entity);
                    }
                }
            }

            if (!list.isEmpty()) {
                int count = 0;
                for (HistoricTaskInstance task : list) {
                    if (task.getEndTime() != null) {
                        count++;
                    }
                }
                if (count > 0) {
                    return 1;
                }
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("获取并行任务完成数量失败", e);
        }
        return -1;
    }

    @Override
    public List<Task> listByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
    }

    @Override
    public List<Task> listByProcessInstanceIdAndActive(String processInstanceId, boolean active) {
        if (active) {
            return taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime()
                    .asc().list();
        } else {
            return taskService.createTaskQuery().processInstanceId(processInstanceId).suspended()
                    .orderByTaskCreateTime().asc().list();
        }
    }

    @Override
    public Y9Page<TaskModel> pageByProcessInstanceId(String processInstanceId, Integer page, Integer rows) {
        long totalCount = taskService.createTaskQuery().processInstanceId(processInstanceId).active().count();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active()
                .orderByTaskCreateTime().asc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        int totalPages = (int) (totalCount + rows - 1) / rows;
        return Y9Page.success(page, totalPages, totalCount, taskModelList);
    }

    @Override
    @Transactional
    public void resolveTask(String taskId) {
        taskService.resolveTask(taskId);
    }

    @Override
    @Transactional
    public void saveTask(Task task) {
        taskService.saveTask(task);
    }

    @Override
    @Transactional
    public void setAssignee(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

    @Override
    @Transactional
    public void setDueDate(String taskId, Date date) {
        taskService.setDueDate(taskId, date);
    }

    @Override
    @Transactional
    public void setPriority(String taskId, Integer priority) {
        taskService.setPriority(taskId, priority);
    }

    @Override
    @Transactional
    public void unclaim(String taskId) {
        taskService.unclaim(taskId);
    }

}
