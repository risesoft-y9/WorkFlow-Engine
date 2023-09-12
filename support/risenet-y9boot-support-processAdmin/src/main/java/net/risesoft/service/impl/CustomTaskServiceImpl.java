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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.command.JumpSubProcessCommand;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.Process4CompleteUtilService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customTaskService")
public class CustomTaskServiceImpl implements CustomTaskService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private CustomProcessDefinitionService customProcessDefinitionService;

    @Autowired
    private CustomHistoricTaskService customHistoricTaskService;

    @Autowired
    protected ManagementService managementService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    private Process4CompleteUtilService process4CompleteUtilService;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Override
    @Transactional(readOnly = false)
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    @Override
    @Transactional(readOnly = false)
    public void complete(String taskId) {
        taskService.complete(taskId);
    }

    @Override
    @Transactional(readOnly = false)
    public void complete(String processInstanceId, String taskId) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String personName = userInfo.getName();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String nodeType =
                customProcessDefinitionService.getNodeType(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (nodeType.equals(SysVariables.PARALLEL)) {
                List<Task> taskList = this.findByProcessInstanceId(processInstanceId);
                for (Task tTemp : taskList) {
                    if (!taskId.equals(tTemp.getId())) {
                        this.complete(tTemp.getId());
                        // 设置强制办理任务标识
                        customHistoricTaskService.setTenantId(tTemp.getId());
                    }
                }
            }
            String year = sdf.format(historicProcessInstance.getStartTime());
            /**
             * 1-备份正在运行的执行实例数据，回复待办的时候会用到，只记录最后一个任务办结前的数据
             */
            String sql0 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list0 = runtimeService.createNativeExecutionQuery().sql(sql0)
                .parameter("PROC_INST_ID_", processInstanceId).list();
            if (list0.size() > 0) {
                // 备份数据已有，则先删除再重新插入备份
                String sql2 = "DELETE FROM FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
                runtimeService.createNativeExecutionQuery().sql(sql2).parameter("PROC_INST_ID_", processInstanceId)
                    .list();
            }
            String sql = "INSERT INTO FF_ACT_RU_EXECUTION_" + year
                + " (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ from ACT_RU_EXECUTION T WHERE T.PROC_INST_ID_ = #{PROC_INST_ID_}";
            runtimeService.createNativeExecutionQuery().sql(sql).parameter("PROC_INST_ID_", processInstanceId).list();

            /**
             * 2-办结流程
             */
            String sql3 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list1 = runtimeService.createNativeExecutionQuery().sql(sql3)
                .parameter("PROC_INST_ID_", processInstanceId).list();
            if (list1.size() > 0) {
                // 成功备份数据才办结
                String endNodeKey = customProcessDefinitionService.getEndNodeKeyByTaskId(taskId);
                Map<String, Object> vars = new HashMap<>(16);
                vars.put(SysVariables.ROUTETOTASKID, endNodeKey);
                this.completeWithVariables(taskId, vars);
                // 保存到数据中心
                process4CompleteUtilService.saveToDataCenter(Y9LoginUserHolder.getTenantId(), year,
                    userInfo.getPersonId(), processInstanceId, personName);
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
                errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new Exception("CustomTaskServiceImpl complete error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void complete4Position(String processInstanceId, String taskId) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Position position = Y9LoginUserHolder.getPosition();
            String personName = position.getName();
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            String nodeType =
                customProcessDefinitionService.getNodeType(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (nodeType.equals(SysVariables.PARALLEL)) {
                List<Task> taskList = this.findByProcessInstanceId(processInstanceId);
                for (Task tTemp : taskList) {
                    if (!taskId.equals(tTemp.getId())) {
                        this.complete(tTemp.getId());
                        // 设置强制办理任务标识
                        customHistoricTaskService.setTenantId(tTemp.getId());
                    }
                }
            }
            String year = sdf.format(historicProcessInstance.getStartTime());
            /**
             * 1-备份正在运行的执行实例数据，回复待办的时候会用到，只记录最后一个任务办结前的数据
             */
            String sql0 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list0 = runtimeService.createNativeExecutionQuery().sql(sql0)
                .parameter("PROC_INST_ID_", processInstanceId).list();
            if (list0.size() > 0) {
                // 备份数据已有，则先删除再重新插入备份
                String sql2 = "DELETE FROM FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
                runtimeService.createNativeExecutionQuery().sql(sql2).parameter("PROC_INST_ID_", processInstanceId)
                    .list();
            }
            String sql = "INSERT INTO FF_ACT_RU_EXECUTION_" + year
                + " (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ from ACT_RU_EXECUTION T WHERE T.PROC_INST_ID_ = #{PROC_INST_ID_}";
            runtimeService.createNativeExecutionQuery().sql(sql).parameter("PROC_INST_ID_", processInstanceId).list();

            /**
             * 2-办结流程
             */
            String sql3 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list1 = runtimeService.createNativeExecutionQuery().sql(sql3)
                .parameter("PROC_INST_ID_", processInstanceId).list();
            if (list1.size() > 0) {
                // 成功备份数据才办结
                String endNodeKey = customProcessDefinitionService.getEndNodeKeyByTaskId(taskId);
                Map<String, Object> vars = new HashMap<String, Object>(16);
                vars.put(SysVariables.ROUTETOTASKID, endNodeKey);
                this.completeWithVariables(taskId, vars);
                // 保存到数据中心
                process4CompleteUtilService.saveToDataCenter(Y9LoginUserHolder.getTenantId(), year, position.getId(),
                    processInstanceId, personName);
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
                errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new Exception("CustomTaskServiceImpl complete error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void completeTaskWithoutAssignee(String processInstanceId) {
        List<Task> taskList = this.findByProcessInstanceId(processInstanceId);
        Task task = taskList.get(0);
        List<Map<String, String>> parallelGatewayList = customProcessDefinitionService
            .getParallelGatewayList(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        String routeToTaskId = parallelGatewayList.get(0).get(SysVariables.TASKDEFKEY);
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
    @Transactional(readOnly = false)
    public void completeWithVariables(String taskId, Map<String, Object> map) {
        taskService.complete(taskId, map, false);
    }

    @Override
    public TaskModel createWithVariables(Map<String, Object> vars, String routeToTaskId, List<String> userIdList) {
        String parentTaskId = (String)vars.get("parentTaskId");
        managementService.executeCommand(
            new JumpSubProcessCommand(parentTaskId, Y9LoginUserHolder.getPersonId(), vars, routeToTaskId, userIdList));
        TaskModel taskModel = null;
        return taskModel;
    }

    @Override
    public TaskModel createWithVariables(String positionId, Map<String, Object> vars, String routeToTaskId,
        List<String> positionIdList) {
        String parentTaskId = (String)vars.get("parentTaskId");
        managementService
            .executeCommand(new JumpSubProcessCommand(parentTaskId, positionId, vars, routeToTaskId, positionIdList));
        TaskModel taskModel = null;
        return taskModel;
    }

    @Override
    @Transactional(readOnly = false)
    public void delegateTask(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteCandidateUser(String taskId, String userId) {
        taskService.deleteCandidateUser(taskId, userId);
    }

    @Override
    public List<Task> findAll() {
        List<Task> list = taskService.createTaskQuery().orderByTaskCreateTime().desc().list();
        return list;
    }

    @Override
    public Task findById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    @Override
    public List<Task> findByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
    }

    @Override
    public List<Task> findByProcessInstanceId(String processInstanceId, boolean active) {
        if (active) {
            return taskService.createTaskQuery().processInstanceId(processInstanceId).active().orderByTaskCreateTime()
                .asc().list();
        } else {
            return taskService.createTaskQuery().processInstanceId(processInstanceId).suspended()
                .orderByTaskCreateTime().asc().list();
        }
    }

    @Override
    public Map<String, Object> findListByProcessInstanceId(String processInstanceId, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = taskService.createTaskQuery().processInstanceId(processInstanceId).active().count();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).active()
            .orderByTaskCreateTime().asc().listPage((page - 1) * rows, rows);

        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Integer getCompleteTaskCount4Parallel(String taskId) {
        List<HistoricTaskInstance> list = new ArrayList<HistoricTaskInstance>();
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

            if (list.size() > 0) {
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
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    @Transactional(readOnly = false)
    public void resolveTask(String taskId) {
        taskService.resolveTask(taskId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTask(Task task) {
        taskService.saveTask(task);
    }

    @Override
    @Transactional(readOnly = false)
    public void setAssignee(String taskId, String userId) {
        taskService.setAssignee(taskId, userId);
    }

    @Override
    @Transactional(readOnly = false)
    public void setDueDate(String taskId, Date date) {
        taskService.setDueDate(taskId, date);
    }

    @Override
    @Transactional(readOnly = false)
    public void setPriority(String taskId, Integer priority) {
        taskService.setPriority(taskId, priority);
    }

    @Override
    @Transactional(readOnly = false)
    public void unclaim(String taskId) {
        taskService.unclaim(taskId);
    }

}
