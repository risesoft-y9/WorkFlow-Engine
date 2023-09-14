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
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.command.JumpCommand;
import net.risesoft.command.JumpCommand4Position;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.CustomVariableService;
import net.risesoft.service.OperationService;
import net.risesoft.service.Process4CompleteUtilService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "operationService")
public class OperationServiceImpl implements OperationService {

    @Autowired
    protected ManagementService managementService;

    @Autowired
    private CustomHistoricTaskService customHistoricTaskService;

    @Autowired
    private CustomTaskService customTaskService;

    @Autowired
    private CustomVariableService customVariableService;

    @Autowired
    private CustomHistoricVariableService customHistoricVariableService;

    @Autowired
    private CustomProcessDefinitionService customProcessDefinitionService;

    @Autowired
    private CustomRuntimeService customRuntimeService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private Process4CompleteUtilService process4CompleteUtilService;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ProcessTrackApi processTrackManager;

    @Autowired
    private ProcessTrack4PositionApi processTrack4PositionApi;

    @Override
    @Transactional(readOnly = false)
    public void reposition(String taskId, String targetTaskDefineKey, List<String> users, String reason, String sponsorGuid) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userName = userInfo.getName();
        Task currentTask = customTaskService.findById(taskId);
        String processInstanceId = currentTask.getProcessInstanceId();
        String reason0 = "该任务已由" + userName + "重定向" + (StringUtils.isNotBlank(reason) ? ":" + reason : "");
        managementService.executeCommand(new JumpCommand(taskId, targetTaskDefineKey, users, reason0));

        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        String multiInstance = customProcessDefinitionService.getNodeType(currentTask.getProcessDefinitionId(), targetTaskDefineKey);
        // 更新自定义历程结束时间
        List<ProcessTrackModel> ptModelList = processTrackManager.findByTaskId(Y9LoginUserHolder.getTenantId(), taskId);
        for (ProcessTrackModel ptModel : ptModelList) {
            if (StringUtils.isBlank(ptModel.getEndTime())) {
                try {
                    ptModel.setDescribed(reason0);
                    processTrackManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), ptModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Task task : taskList) {
            Map<String, Object> vars = new HashMap<>(16);
            vars.put(SysVariables.REPOSITION, true);
            if (SysVariables.PARALLEL.equals(multiInstance)) {
                // String ownerId = entrustManager.getEntrustOwnerId(Y9LoginUserHolder.getTenantId(), task.getId());
                // if (StringUtils.isBlank(ownerId)) {
                if (task.getAssignee().equals(sponsorGuid)) {
                    vars.put(SysVariables.PARALLELSPONSOR, sponsorGuid);
                    ProcessParamModel processParam = processParamManager.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                    processParam.setSponsorGuid(sponsorGuid);
                    processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParam);
                }
                // }
                // else {
                // // 出差委托更换主办人
                // if (ownerId.contains(sponsorGuid)) {
                // vars.put(SysVariables.PARALLELSPONSOR, task.getAssignee().split(SysVariables.COLON)[0]);
                // ProcessParamModel processParam = processParamManager
                // .findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                // processParam.setSponsorGuid(task.getAssignee().split(SysVariables.COLON)[0]);
                // processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParam);
                // }
                // }
            }
            customVariableService.setVariablesLocal(task.getId(), vars);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void reposition4Position(String taskId, String targetTaskDefineKey, List<String> users, String reason, String sponsorGuid) {
        String userName = Y9LoginUserHolder.getPosition().getName();
        Task currentTask = customTaskService.findById(taskId);
        String processInstanceId = currentTask.getProcessInstanceId();
        String reason0 = "该任务已由" + userName + "重定向" + (StringUtils.isNotBlank(reason) ? ":" + reason : "");
        managementService.executeCommand(new JumpCommand4Position(taskId, targetTaskDefineKey, users, reason0));

        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        String multiInstance = customProcessDefinitionService.getNodeType(currentTask.getProcessDefinitionId(), targetTaskDefineKey);
        // 更新自定义历程结束时间
        List<ProcessTrackModel> ptModelList = processTrack4PositionApi.findByTaskId(Y9LoginUserHolder.getTenantId(), taskId);
        for (ProcessTrackModel ptModel : ptModelList) {
            if (StringUtils.isBlank(ptModel.getEndTime())) {
                try {
                    ptModel.setDescribed(reason0);
                    processTrack4PositionApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), ptModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Task task : taskList) {
            Map<String, Object> vars = new HashMap<>(16);
            vars.put(SysVariables.REPOSITION, true);
            if (SysVariables.PARALLEL.equals(multiInstance)) {
                // String ownerId = entrust4PositionApi.getEntrustOwnerId(Y9LoginUserHolder.getTenantId(),
                // task.getId());
                // if (StringUtils.isBlank(ownerId)) {
                if (task.getAssignee().equals(sponsorGuid)) {
                    vars.put(SysVariables.PARALLELSPONSOR, sponsorGuid);
                    ProcessParamModel processParam = processParamManager.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                    processParam.setSponsorGuid(sponsorGuid);
                    processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParam);
                }
                // } else {
                // // 出差委托更换主办人
                // if (ownerId.contains(sponsorGuid)) {
                // vars.put(SysVariables.PARALLELSPONSOR, task.getAssignee().split(SysVariables.COLON)[0]);
                // ProcessParamModel processParam =
                // processParamManager.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                // processParam.setSponsorGuid(task.getAssignee().split(SysVariables.COLON)[0]);
                // processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParam);
                // }
                // }
            }
            customVariableService.setVariablesLocal(task.getId(), vars);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void rollBack(String taskId, String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userName = userInfo.getName();
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey(), processInstanceId = thePreviousTask.getProcessInstanceId();

        // Object taskSenderIdObject = customVariableService.getVariableLocal(taskId,
        // SysVariables.TASKSENDERID);
        // String user = (String) taskSenderIdObject;
        HistoricVariableInstance taskSenderIdObject = customHistoricVariableService.getByTaskIdAndVariableName(taskId, SysVariables.TASKSENDERID, "");
        String user = taskSenderIdObject != null ? taskSenderIdObject.getValue().toString() : "";
        List<String> users = new ArrayList<>();
        users.add(user);

        managementService.executeCommand(new JumpCommand(taskId, targetTaskDefineKey, users, "该任务由" + userName + "退回:" + reason));

        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        for (Task task : taskList) {
            customVariableService.setVariableLocal(task.getId(), SysVariables.ROLLBACK, true);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void rollBack4Position(String taskId, String reason) {
        String userName = Y9LoginUserHolder.getPosition().getName();
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey(), processInstanceId = thePreviousTask.getProcessInstanceId();
        /**
         * 设置任务的完成动作
         */
        customVariableService.setVariableLocal(taskId, SysVariables.ACTIONNAME, SysVariables.ROLLBACK);
        /**
         * 把taskId对应的任务的发送岗位作为接受的岗位
         */
        HistoricVariableInstance taskSenderIdObject = customHistoricVariableService.getByTaskIdAndVariableName(taskId, SysVariables.TASKSENDERID, "");
        String user = taskSenderIdObject != null ? taskSenderIdObject.getValue().toString() : "";
        List<String> users = new ArrayList<>();
        users.add(user);
        managementService.executeCommand(new JumpCommand4Position(taskId, targetTaskDefineKey, users, "该任务由" + userName + "驳回：" + reason));
        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        for (Task task : taskList) {
            customVariableService.setVariableLocal(task.getId(), SysVariables.ROLLBACK, true);
        }
    }

    @Override
    public void rollbackToSender(String taskId) {
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey();

        Object taskSenderIdObject = customVariableService.getVariableLocal(taskId, SysVariables.TASKSENDERID);
        String user = (String)taskSenderIdObject;
        List<String> users = new ArrayList<>();
        users.add(user);

        managementService.executeCommand(new JumpCommand(taskId, targetTaskDefineKey, users, ""));
    }

    @Override
    public void rollbackToSender4Position(String taskId) {
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey();

        Object taskSenderIdObject = customVariableService.getVariableLocal(taskId, SysVariables.TASKSENDERID);
        String user = (String)taskSenderIdObject;
        List<String> users = new ArrayList<>();
        users.add(user);

        managementService.executeCommand(new JumpCommand4Position(taskId, targetTaskDefineKey, users, ""));
    }

    @Override
    public void rollbackToStartor(String taskId, String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userName = userInfo.getName();
        Task currentTask = customTaskService.findById(taskId);
        String processInstanceId = currentTask.getProcessInstanceId();
        /**
         * 获取第一个任务
         */
        List<HistoricTaskInstance> hisTaskList = customHistoricTaskService.getByProcessInstanceId(processInstanceId, "");
        String startActivityId = hisTaskList.get(0).getTaskDefinitionKey();
        /**
         * 获取流程的启东人
         */
        ProcessInstance processInstance = customRuntimeService.getProcessInstance(processInstanceId);
        List<String> users = new ArrayList<>();
        users.add(processInstance.getStartUserId().split(":")[0]);
        managementService.executeCommand(new JumpCommand(taskId, startActivityId, users, "该任务已由" + userName + "返回至起草节点"));
    }

    @Override
    public void rollbackToStartor4Position(String taskId, String reason) {
        String userName = Y9LoginUserHolder.getPosition().getName();
        Task currentTask = customTaskService.findById(taskId);
        String processInstanceId = currentTask.getProcessInstanceId();
        /**
         * 获取第一个任务
         */
        List<HistoricTaskInstance> hisTaskList = customHistoricTaskService.getByProcessInstanceId(processInstanceId, "");
        String startActivityId = hisTaskList.get(0).getTaskDefinitionKey();
        /**
         * 获取流程的启东人
         */
        ProcessInstance processInstance = customRuntimeService.getProcessInstance(processInstanceId);
        List<String> users = new ArrayList<>();
        users.add(processInstance.getStartUserId().split(":")[0]);
        managementService.executeCommand(new JumpCommand4Position(taskId, startActivityId, users, "该任务已由" + userName + "返回至起草节点"));
    }

    @Override
    @Transactional(readOnly = false)
    public void specialComplete(String taskId, String reason) throws Exception {
        String processInstanceId = "";
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userName = userInfo.getName();
            String endKey = customProcessDefinitionService.getTaskDefKey4EndEvent(taskId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Task task = customTaskService.findById(taskId);
            processInstanceId = task.getProcessInstanceId();
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String year = sdf.format(historicProcessInstance.getStartTime());
            /**
             * 1-备份正在运行的执行实例数据，回复待办的时候会用到，只记录最后一个任务办结前的数据
             */
            String sql0 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list0 = runtimeService.createNativeExecutionQuery().sql(sql0).parameter("PROC_INST_ID_", processInstanceId).list();
            // 备份数据已有，则先删除再重新插入备份
            if (list0.size() > 0) {
                String sql2 = "DELETE FROM FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
                runtimeService.createNativeExecutionQuery().sql(sql2).parameter("PROC_INST_ID_", processInstanceId).list();
            }
            String sql = "INSERT INTO FF_ACT_RU_EXECUTION_" + year
                + " (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ from ACT_RU_EXECUTION T WHERE T.PROC_INST_ID_ = #{PROC_INST_ID_}";
            runtimeService.createNativeExecutionQuery().sql(sql).parameter("PROC_INST_ID_", processInstanceId).list();

            /**
             * 2-办结流程
             */
            String sql3 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list1 = runtimeService.createNativeExecutionQuery().sql(sql3).parameter("PROC_INST_ID_", processInstanceId).list();
            // 成功备份数据才特殊办结
            if (list1.size() > 0) {
                managementService.executeCommand(new JumpCommand(taskId, endKey, new ArrayList<>(), "该任务由" + userName + "特殊办结:" + reason));
                // 保存到数据中心
                process4CompleteUtilService.saveToDataCenter(Y9LoginUserHolder.getTenantId(), year, userInfo.getPersonId(), processInstanceId, userName);
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
            errorLogModel.setExtendField("特殊办结失败");
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
            throw new Exception("OperationServiceImpl specialComplete error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void specialComplete4Position(String taskId, String reason) throws Exception {
        String processInstanceId = "";
        try {
            String userName = Y9LoginUserHolder.getPosition().getName();
            String endKey = customProcessDefinitionService.getTaskDefKey4EndEvent(taskId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Task task = customTaskService.findById(taskId);
            processInstanceId = task.getProcessInstanceId();
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            String year = sdf.format(historicProcessInstance.getStartTime());
            /**
             * 1-备份正在运行的执行实例数据，回复待办的时候会用到，只记录最后一个任务办结前的数据
             */
            String sql0 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list0 = runtimeService.createNativeExecutionQuery().sql(sql0).parameter("PROC_INST_ID_", processInstanceId).list();
            // 备份数据已有，则先删除再重新插入备份
            if (list0.size() > 0) {
                String sql2 = "DELETE FROM FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
                runtimeService.createNativeExecutionQuery().sql(sql2).parameter("PROC_INST_ID_", processInstanceId).list();
            }
            String sql = "INSERT INTO FF_ACT_RU_EXECUTION_" + year
                + " (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ from ACT_RU_EXECUTION T WHERE T.PROC_INST_ID_ = #{PROC_INST_ID_}";
            runtimeService.createNativeExecutionQuery().sql(sql).parameter("PROC_INST_ID_", processInstanceId).list();

            /**
             * 2-办结流程
             */
            String sql3 = "SELECT * from FF_ACT_RU_EXECUTION_" + year + " WHERE PROC_INST_ID_ = #{PROC_INST_ID_}";
            List<Execution> list1 = runtimeService.createNativeExecutionQuery().sql(sql3).parameter("PROC_INST_ID_", processInstanceId).list();
            // 成功备份数据才特殊办结
            if (list1.size() > 0) {
                managementService.executeCommand(new JumpCommand4Position(taskId, endKey, new ArrayList<>(), "该任务由" + userName + "特殊办结:" + reason));
                // 保存到数据中心
                process4CompleteUtilService.saveToDataCenter(Y9LoginUserHolder.getTenantId(), year, Y9LoginUserHolder.getPositionId(), processInstanceId, userName);
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
            errorLogModel.setExtendField("特殊办结失败");
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
            throw new Exception("OperationServiceImpl specialComplete4Position error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void takeBack(String taskId, String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userName = userInfo.getName(), userId = userInfo.getPersonId();
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey(), processInstanceId = thePreviousTask.getProcessInstanceId();

        List<String> users = new ArrayList<>();
        users.add(userId);

        managementService.executeCommand(new JumpCommand(taskId, targetTaskDefineKey, users, "该任务由" + userName + "收回:" + reason));

        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        for (Task task : taskList) {
            customVariableService.setVariableLocal(task.getId(), SysVariables.TAKEBACK, true);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void takeBack4Position(String taskId, String reason) {
        String userName = Y9LoginUserHolder.getPosition().getName();
        HistoricTaskInstance thePreviousTask = customHistoricTaskService.getThePreviousTask(taskId);
        String targetTaskDefineKey = thePreviousTask.getTaskDefinitionKey(), processInstanceId = thePreviousTask.getProcessInstanceId();
        /**
         * 设置任务的完成动作
         */
        customVariableService.setVariableLocal(taskId, SysVariables.ACTIONNAME, SysVariables.TAKEBACK);
        String user = Y9LoginUserHolder.getPositionId();
        List<String> users = new ArrayList<>();
        users.add(user);
        managementService.executeCommand(new JumpCommand4Position(taskId, targetTaskDefineKey, users, "该任务由" + userName + "撤回：" + reason));
        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        for (Task task : taskList) {
            customVariableService.setVariableLocal(task.getId(), SysVariables.TAKEBACK, true);
        }
    }
}
