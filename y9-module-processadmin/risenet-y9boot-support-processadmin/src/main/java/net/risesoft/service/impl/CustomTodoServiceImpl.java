package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.CustomTodoService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service(value = "customTodoService")
public class CustomTodoServiceImpl implements CustomTodoService {

    private final TaskService taskService;

    @Override
    public long getCountByUserId(String userId) {
        return taskService.createTaskQuery().taskAssignee(userId).active().count();
    }

    @Override
    public long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey) {
        return taskService.createTaskQuery().taskInvolvedUser(userId).active()
            .processDefinitionKey(processDefinitionKey).count();
    }

    @Override
    public long getCountByUserIdAndSystemName(String userId, String systemName) {
        return taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName).count();
    }

    @Override
    public Y9Page<TaskModel> pageByUserId(String userId, Integer page, Integer rows) {
        long totalCount = this.getCountByUserId(userId);
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).active().orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);

        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        int totalPages = (int)(totalCount + rows - 1) / rows;
        return Y9Page.success(page, totalPages, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> pageByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        Integer page, Integer rows) {
        long totalCount = this.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        List<Task> taskList =
            taskService.createTaskQuery().taskInvolvedUser(userId).active().processDefinitionKey(processDefinitionKey)
                .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        int totalPages = (int)(totalCount + rows - 1) / rows;
        return Y9Page.success(page, totalPages, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> pageByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows) {
        long totalCount = this.getCountByUserIdAndSystemName(userId, systemName);
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> pageByUserIdAndSystemName4xxx(String userId, String systemName,
        String processDefinitionKey, String target, Integer page, Integer rows) {
        long totalCount;
        List<Task> taskList;
        if (StringUtils.isNotBlank(processDefinitionKey) && StringUtils.isNotBlank(target)
            && StringUtils.isNotBlank(systemName)) {
            totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                .processDefinitionKey(processDefinitionKey).processVariableValueEquals("target", target).count();
            taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                .processDefinitionKey(processDefinitionKey).processVariableValueEquals("target", target)
                .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        } else {
            if (StringUtils.isNotBlank(processDefinitionKey)) {
                totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                    .processDefinitionKey(processDefinitionKey).count();
                taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                    .processDefinitionKey(processDefinitionKey).orderByTaskPriority().desc().orderByTaskCreateTime()
                    .desc().listPage((page - 1) * rows, rows);
            } else if (StringUtils.isNotBlank(target)) {
                totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                    .processVariableValueEquals("target", target).count();
                taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                    .processVariableValueEquals("target", target).orderByTaskPriority().desc().orderByTaskCreateTime()
                    .desc().listPage((page - 1) * rows, rows);
            } else if (StringUtils.isNotBlank(systemName)) {
                totalCount =
                    taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName).count();
                taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
                    .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
            } else {
                totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().count();
                taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().orderByTaskPriority().desc()
                    .orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
            }
        }
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows) {
        long totalCount = taskService.createTaskQuery().taskAssignee(userId).active()
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).active()
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        int totalPages = (int)(totalCount + rows - 1) / rows;
        return Y9Page.success(page, totalPages, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        long totalCount =
            taskService.createTaskQuery().taskInvolvedUser(userId).active().processDefinitionKey(processDefinitionKey)
                .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active()
            .processDefinitionKey(processDefinitionKey).processVariableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, taskModelList);
    }

    @Override
    public Y9Page<TaskModel> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm,
        Integer page, Integer rows) {
        long totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, taskModelList);
    }
}
