package net.risesoft.util;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class FlowableModelConvertUtil {

    public static List<HistoricActivityInstanceModel>
        historicActivityInstanceList2Model(List<HistoricActivityInstance> haiList) {
        List<HistoricActivityInstanceModel> htiModelList = new ArrayList<>();
        for (HistoricActivityInstance hai : haiList) {
            HistoricActivityInstanceModel historicActivityInstanceModel = new HistoricActivityInstanceModel();
            Y9BeanUtil.copyProperties(hai, historicActivityInstanceModel);
            htiModelList.add(historicActivityInstanceModel);
        }
        return htiModelList;
    }

    public static List<HistoricProcessInstanceModel>
        historicProcessInstanceList2ModelList(List<HistoricProcessInstance> hpiList) {
        List<HistoricProcessInstanceModel> pdiModelList = new ArrayList<>();
        for (HistoricProcessInstance hpi : hpiList) {
            HistoricProcessInstanceModel historicProcessInstanceModel = new HistoricProcessInstanceModel();
            Y9BeanUtil.copyProperties(hpi, historicProcessInstanceModel);
            pdiModelList.add(historicProcessInstanceModel);
        }
        return pdiModelList;
    }

    public static List<HistoricTaskInstanceModel>
        historicTaskInstanceList2ModelList(List<HistoricTaskInstance> htiList) {
        List<HistoricTaskInstanceModel> htiModelList = new ArrayList<>();
        for (HistoricTaskInstance hti : htiList) {
            HistoricTaskInstanceModel historicTaskInstanceModel = new HistoricTaskInstanceModel();
            Y9BeanUtil.copyProperties(hti, historicTaskInstanceModel);
            htiModelList.add(historicTaskInstanceModel);
        }
        return htiModelList;
    }

    public static List<HistoricVariableInstanceModel>
        historicVariableInstanceList2ModelList(List<HistoricVariableInstance> hviList) {
        List<HistoricVariableInstanceModel> htiModelList = new ArrayList<>();
        for (HistoricVariableInstance hvi : hviList) {
            HistoricVariableInstanceModel hviModel = new HistoricVariableInstanceModel();
            Y9BeanUtil.copyProperties(hvi, hviModel);
            htiModelList.add(hviModel);
        }
        return htiModelList;
    }

    public static List<IdentityLinkModel> identityLinkList2ModelList(List<IdentityLink> ilList) {
        List<IdentityLinkModel> ilModelList = new ArrayList<>();
        for (IdentityLink il : ilList) {
            IdentityLinkModel ilModel = new IdentityLinkModel();
            Y9BeanUtil.copyProperties(il, ilModel);
            ilModelList.add(ilModel);
        }
        return ilModelList;
    }

    public static List<ProcessDefinitionModel> processDefinitionList2ModelList(List<ProcessDefinition> pdList) {
        List<ProcessDefinitionModel> pdModelList = new ArrayList<>();
        for (ProcessDefinition pd : pdList) {
            ProcessDefinitionModel model = new ProcessDefinitionModel();
            Y9BeanUtil.copyProperties(pd, model);
            pdModelList.add(model);
        }
        return pdModelList;
    }

    public static List<ProcessInstanceModel> processInstanceList2ModelList(List<ProcessInstance> piList) {
        List<ProcessInstanceModel> piModelList = new ArrayList<>();
        for (ProcessInstance pi : piList) {
            ProcessInstanceModel piModel = new ProcessInstanceModel();
            Y9BeanUtil.copyProperties(pi, piModel);
            piModelList.add(piModel);
        }
        return piModelList;
    }

    public static List<TaskModel> taskList2TaskModelList(List<Task> taskList) {
        List<TaskModel> taskModelList = new ArrayList<>();
        for (Task task : taskList) {
            TaskModel model = new TaskModel();
            Y9BeanUtil.copyProperties(task, model);
            taskModelList.add(model);
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
