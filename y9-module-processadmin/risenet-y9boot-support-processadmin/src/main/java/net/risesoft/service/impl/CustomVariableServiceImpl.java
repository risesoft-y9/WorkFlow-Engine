package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.service.CustomVariableService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Service(value = "customVariableService")
public class CustomVariableServiceImpl implements CustomVariableService {

    private final TaskService taskService;

    @Override
    public void deleteVariable(String taskId, String key) {
        taskService.removeVariable(taskId, key);
    }

    @Override
    public Object getVariable(String taskId, String key) {
        return taskService.getVariable(taskId, key);
    }

    @Override
    public Object getVariableLocal(String taskId, String key) {
        return taskService.getVariableLocal(taskId, key);
    }

    @Override
    public Map<String, Object> getVariables(String taskId) {
        return taskService.getVariables(taskId);
    }

    @Override
    public Map<String, Object> getVariablesLocal(String taskId) {
        return taskService.getVariablesLocal(taskId);
    }

    @Override
    public void removeVariableLocal(String taskId, String key) {
        taskService.removeVariableLocal(taskId, key);
    }

    @Override
    public void setVariable(String taskId, String key, Object val) {
        taskService.setVariable(taskId, key, val);
    }

    @Override
    public void setVariableLocal(String taskId, String key, Object val) {
        taskService.setVariableLocal(taskId, key, val);
    }

    @Override
    public void setVariables(String taskId, Map<String, Object> map) {
        taskService.setVariables(taskId, map);
    }

    @Override
    public void setVariablesLocal(String taskId, Map<String, Object> map) {
        taskService.setVariablesLocal(taskId, map);
    }
}
