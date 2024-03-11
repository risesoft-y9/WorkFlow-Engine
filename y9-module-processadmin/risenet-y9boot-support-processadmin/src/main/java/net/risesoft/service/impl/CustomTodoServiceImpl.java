package net.risesoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.CustomTodoService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customTodoService")
public class CustomTodoServiceImpl implements CustomTodoService {

    @Autowired
    private TaskService taskService;

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
    public Map<String, Object> getListByUserId(String userId, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserId(userId);
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).active().orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);

        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        List<Task> taskList =
            taskService.createTaskQuery().taskInvolvedUser(userId).active().processDefinitionKey(processDefinitionKey)
                .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getListByUserIdAndSystemName(String userId, String systemName, Integer page,
        Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserIdAndSystemName(userId, systemName);
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .orderByTaskPriority().desc().orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = taskService.createTaskQuery().taskAssignee(userId).active()
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userId).active()
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount =
            taskService.createTaskQuery().taskInvolvedUser(userId).active().processDefinitionKey(processDefinitionKey)
                .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active()
            .processDefinitionKey(processDefinitionKey).processVariableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByTaskCreateTime().desc().listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm,
        Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<Task> taskList = taskService.createTaskQuery().taskInvolvedUser(userId).active().taskCategory(systemName)
            .processVariableValueLike("searchTerm", "%" + searchTerm + "%").orderByTaskCreateTime().desc()
            .listPage((page - 1) * rows, rows);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", taskModelList);
        return returnMap;
    }
}
