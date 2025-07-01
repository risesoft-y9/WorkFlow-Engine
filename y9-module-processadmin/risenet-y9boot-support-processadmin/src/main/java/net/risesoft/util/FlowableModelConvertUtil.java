package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.variable.api.history.HistoricVariableInstance;

import net.risesoft.model.processadmin.*;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class FlowableModelConvertUtil {

    public static TaskModel delegateTask2TaskModel(DelegateTask task) {
        TaskModel tm = new TaskModel();
        tm.setId(task.getId());
        tm.setName(task.getName());
        tm.setDescription(task.getDescription());
        tm.setOwner(task.getOwner());
        tm.setAssignee(task.getAssignee());
        tm.setProcessInstanceId(task.getProcessInstanceId());
        tm.setExecutionId(task.getExecutionId());
        tm.setProcessDefinitionId(task.getProcessDefinitionId());
        tm.setCreateTime(task.getCreateTime());
        tm.setTaskDefinitionKey(task.getTaskDefinitionKey());
        tm.setDueDate(task.getDueDate());
        tm.setDelegationState(task.getDelegationState());
        tm.setVariables(task.getVariables());
        tm.setLocalVariables(task.getVariablesLocal());
        return tm;
    }

    public static ExecutionModel execution2Model(Execution execution) {
        ExecutionModel eModel = new ExecutionModel();
        eModel.setId(execution.getId());
        eModel.setActivityId(execution.getActivityId());
        eModel.setDescription(execution.getDescription());
        eModel.setEnded(execution.isEnded());
        eModel.setName(execution.getName());
        eModel.setParentId(execution.getParentId());
        eModel.setProcessInstanceId(execution.getProcessInstanceId());
        eModel.setRootProcessInstanceId(execution.getRootProcessInstanceId());
        eModel.setSuperExecutionId(execution.getSuperExecutionId());
        eModel.setSuspended(execution.isSuspended());
        eModel.setTenantId(execution.getTenantId());
        return eModel;
    }

    public static List<HistoricActivityInstanceModel>
        historicActivityInstanceList2Model(List<HistoricActivityInstance> haiList) {
        List<HistoricActivityInstanceModel> htiModelList = new ArrayList<HistoricActivityInstanceModel>();
        for (HistoricActivityInstance hai : haiList) {
            htiModelList.add(historicActivityInstanceModel2Model(hai));
        }
        return htiModelList;
    }

    public static HistoricActivityInstanceModel historicActivityInstanceModel2Model(HistoricActivityInstance hai) {
        HistoricActivityInstanceModel haiModel = new HistoricActivityInstanceModel();
        haiModel.setId(hai.getId());
        haiModel.setActivityId(hai.getActivityId());
        haiModel.setActivityName(hai.getActivityName());
        haiModel.setActivityType(hai.getActivityType());
        haiModel.setProcessDefinitionId(hai.getProcessDefinitionId());
        haiModel.setProcessInstanceId(hai.getProcessInstanceId());
        haiModel.setExecutionId(hai.getExecutionId());
        haiModel.setTaskId(hai.getTaskId());
        haiModel.setAssignee(hai.getAssignee());
        haiModel.setCalledProcessInstanceId(hai.getCalledProcessInstanceId());
        haiModel.setStartTime(hai.getStartTime());
        haiModel.setEndTime(hai.getEndTime());
        haiModel.setDurationInMillis(hai.getDurationInMillis());
        haiModel.setDeleteReason(hai.getDeleteReason());
        haiModel.setTenantId(hai.getTenantId());
        return haiModel;
    }

    public static HistoricProcessInstanceModel historicProcessInstance2Model(HistoricProcessInstance hpi) {
        HistoricProcessInstanceModel hpiModel = null;
        if (hpi != null) {
            hpiModel = new HistoricProcessInstanceModel();
            hpiModel.setDeleteReason(hpi.getDeleteReason());
            hpiModel.setDeploymentId(hpi.getDeploymentId());
            hpiModel.setDescription(hpi.getDescription());
            hpiModel.setDurationInMillis(hpi.getDurationInMillis());
            hpiModel.setEndActivityId(hpi.getEndActivityId());
            hpiModel.setEndTime(hpi.getEndTime());
            hpiModel.setId(hpi.getId());
            hpiModel.setName(hpi.getName());
            hpiModel.setProcessDefinitionId(hpi.getProcessDefinitionId());
            hpiModel.setProcessDefinitionKey(hpi.getProcessDefinitionKey());
            hpiModel.setProcessDefinitionName(hpi.getProcessDefinitionName());
            hpiModel.setProcessDefinitionVersion(hpi.getProcessDefinitionVersion());
            hpiModel.setStartActivityId(hpi.getStartActivityId());
            hpiModel.setStartTime(hpi.getStartTime());
            hpiModel.setStartUserId(hpi.getStartUserId());
            hpiModel.setSuperProcessInstanceId(hpi.getSuperProcessInstanceId());
            hpiModel.setVariables(hpi.getProcessVariables());
        }
        return hpiModel;
    }

    public static List<HistoricProcessInstanceModel>
        historicProcessInstanceList2ModelList(List<HistoricProcessInstance> hpiList) {
        List<HistoricProcessInstanceModel> pdiModelList = new ArrayList<HistoricProcessInstanceModel>();
        for (HistoricProcessInstance hpi : hpiList) {
            pdiModelList.add(historicProcessInstance2Model(hpi));
        }
        return pdiModelList;
    }

    public static HistoricTaskInstanceModel historicTaskInstance2Model(HistoricTaskInstance hti) {
        HistoricTaskInstanceModel histim = null;
        if (hti != null) {
            histim = new HistoricTaskInstanceModel();
            histim.setId(hti.getId());
            histim.setName(hti.getName());
            histim.setAssignee(hti.getAssignee());
            histim.setOwner(hti.getOwner());
            histim.setClaimTime(hti.getClaimTime());
            histim.setDeleteReason(hti.getDeleteReason());
            histim.setDescription(hti.getDescription());
            histim.setDueDate(hti.getDueDate());
            histim.setStartTime(hti.getCreateTime());
            histim.setEndTime(hti.getEndTime());
            histim.setExecutionId(hti.getExecutionId());
            histim.setParentTaskId(hti.getParentTaskId());
            histim.setProcessDefinitionId(hti.getProcessDefinitionId());
            histim.setProcessInstanceId(hti.getProcessInstanceId());
            histim.setTaskDefinitionKey(hti.getTaskDefinitionKey());
            histim.setTenantId(hti.getTenantId());
            histim.setScopeType(hti.getScopeType());
        }
        return histim;
    }

    public static List<HistoricTaskInstanceModel>
        historicTaskInstanceList2ModelList(List<HistoricTaskInstance> htiList) {
        List<HistoricTaskInstanceModel> htiModelList = new ArrayList<>();
        for (HistoricTaskInstance hti : htiList) {
            htiModelList.add(historicTaskInstance2Model(hti));
        }
        return htiModelList;
    }

    public static HistoricVariableInstanceModel historicVariableInstance2Model(HistoricVariableInstance hvi) {
        HistoricVariableInstanceModel hviModel = new HistoricVariableInstanceModel();
        hviModel.setId(hvi.getId());
        hviModel.setCreateTime(hvi.getCreateTime());
        hviModel.setLastUpdatedTime(hvi.getLastUpdatedTime());
        hviModel.setProcessInstanceId(hvi.getProcessInstanceId());
        hviModel.setTaskId(hvi.getTaskId());
        hviModel.setValue(hvi.getValue());
        hviModel.setVariableName(hvi.getVariableName());
        hviModel.setVariableTypeName(hvi.getVariableTypeName());

        return hviModel;
    }

    public static List<HistoricVariableInstanceModel>
        historicVariableInstanceList2ModelList(List<HistoricVariableInstance> hviList) {
        List<HistoricVariableInstanceModel> htiModelList = new ArrayList<HistoricVariableInstanceModel>();
        for (HistoricVariableInstance hvi : hviList) {
            htiModelList.add(historicVariableInstance2Model(hvi));
        }
        return htiModelList;
    }

    public static IdentityLinkModel identityLink2Model(IdentityLink il) {
        IdentityLinkModel ilModel = new IdentityLinkModel();
        ilModel.setGroupId(il.getGroupId());
        ilModel.setProcessDefinitionId(il.getProcessDefinitionId());
        ilModel.setProcessInstanceId(il.getProcessInstanceId());
        ilModel.setTaskId(il.getTaskId());
        ilModel.setType(il.getType());
        ilModel.setUserId(il.getUserId());

        return ilModel;
    }

    public static List<IdentityLinkModel> identityLinkList2ModelList(List<IdentityLink> ilList) {
        List<IdentityLinkModel> ilModelList = new ArrayList<>();
        for (IdentityLink il : ilList) {
            ilModelList.add(identityLink2Model(il));
        }
        return ilModelList;
    }

    public static ProcessDefinitionModel processDefinition2Model(ProcessDefinition pd) {
        ProcessDefinitionModel pdModel = new ProcessDefinitionModel();
        pdModel.setCategory(pd.getCategory());
        pdModel.setDeploymentId(pd.getDeploymentId());
        pdModel.setDescription(pd.getDescription());
        pdModel.setDiagramResourceName(pd.getDiagramResourceName());
        pdModel.setEngineVersion(pd.getEngineVersion());
        pdModel.setId(pd.getId());
        pdModel.setKey(pd.getKey());
        pdModel.setName(pd.getName());
        pdModel.setResourceName(pd.getResourceName());
        pdModel.setSuspended(pd.isSuspended());
        pdModel.setVersion(pd.getVersion());

        return pdModel;
    }

    public static List<ProcessDefinitionModel> processDefinitionList2ModelList(List<ProcessDefinition> pdList) {
        List<ProcessDefinitionModel> pdModelList = new ArrayList<ProcessDefinitionModel>();
        for (ProcessDefinition pd : pdList) {
            pdModelList.add(processDefinition2Model(pd));
        }
        return pdModelList;
    }

    public static ProcessInstanceModel processInstance2Model(ProcessInstance pi) {
        ProcessInstanceModel pim = new ProcessInstanceModel();
        pim.setId(pi.getId());
        pim.setDeploymentId(pi.getDeploymentId());
        pim.setDescription(pi.getDescription());
        pim.setEnded(pi.isEnded());
        pim.setName(pi.getName());
        pim.setProcessDefinitionId(pi.getProcessDefinitionId());
        pim.setProcessDefinitionKey(pi.getProcessDefinitionKey());
        pim.setProcessDefinitionName(pi.getProcessDefinitionName());
        pim.setProcessDefinitionVersion(pi.getProcessDefinitionVersion());
        pim.setStartTime(pi.getStartTime());
        pim.setStartUserId(pi.getStartUserId());
        pim.setSuspended(pi.isSuspended());
        pim.setVariables(pi.getProcessVariables());
        return pim;
    }

    public static List<ProcessInstanceModel> processInstanceList2ModelList(List<ProcessInstance> piList) {
        List<ProcessInstanceModel> piModelList = new ArrayList<ProcessInstanceModel>();
        for (ProcessInstance pi : piList) {
            piModelList.add(processInstance2Model(pi));
        }
        return piModelList;
    }

    public static TaskModel task2TaskModel(Task task) {
        TaskModel tm = null;
        if (task != null) {
            tm = new TaskModel();
            tm.setId(task.getId());
            tm.setName(task.getName());
            tm.setDescription(task.getDescription());
            tm.setPriority(task.getPriority());
            tm.setOwner(task.getOwner());
            tm.setAssignee(task.getAssignee());
            tm.setProcessInstanceId(task.getProcessInstanceId());
            tm.setExecutionId(task.getExecutionId());
            tm.setProcessDefinitionId(task.getProcessDefinitionId());
            tm.setCreateTime(task.getCreateTime());
            tm.setTaskDefinitionKey(task.getTaskDefinitionKey());
            tm.setDueDate(task.getDueDate());
            tm.setClaimTime(task.getClaimTime());
            tm.setDelegationState(task.getDelegationState());
            tm.setVariables(task.getProcessVariables());
            tm.setLocalVariables(task.getTaskLocalVariables());
            tm.setFormKey(task.getFormKey());
        }
        return tm;
    }

    public static List<TaskModel> taskList2TaskModelList(List<Task> taskList) {
        List<TaskModel> taskModelList = new ArrayList<TaskModel>();
        for (Task task : taskList) {
            taskModelList.add(task2TaskModel(task));
        }
        return taskModelList;
    }

    public static Task taskModel2Task(TaskModel taskModel, Task task) {
        task.setName(taskModel.getName());
        task.setDescription(taskModel.getDescription());
        task.setPriority(taskModel.getPriority());
        task.setOwner(taskModel.getOwner());
        task.setAssignee(taskModel.getAssignee());
        task.setDueDate(taskModel.getDueDate());
        task.setFormKey(taskModel.getFormKey());
        return task;
    }
}
