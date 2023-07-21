package net.risesoft.api;

import java.util.Collection;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.service.CustomVariableService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/variable")
public class VariableApiImpl implements VariableApi {

    @Autowired
    private CustomVariableService customVariableService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    /**
     * 删除流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     */
    @Override
    @PostMapping(value = "/deleteVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteVariable(String tenantId, String taskId, String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.deleteVariable(taskId, key);
    }

    /***
     * 删除任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     */
    @Override
    @PostMapping(value = "/deleteVariableLocal", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteVariableLocal(String tenantId, String taskId, String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.removeVariableLocal(taskId, key);
    }

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return String
     */
    @Override
    @GetMapping(value = "/getVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getVariable(String tenantId, String taskId, String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = customVariableService.getVariable(taskId, key);
        return o != null ? Y9JsonUtil.writeValueAsString(o) : null;
    }

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程id
     * @param key 变量key
     * @return String
     */
    @Override
    @GetMapping(value = "/getVariableByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getVariableByProcessInstanceId(String tenantId, String processInstanceId, String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = runtimeService.getVariable(processInstanceId, key);
        return o != null ? Y9JsonUtil.writeValueAsString(o) : null;
    }

    /**
     * 获取任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return String
     */
    @Override
    @GetMapping(value = "/getVariableLocal", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getVariableLocal(String tenantId, String taskId, String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = taskService.getVariableLocal(taskId, key);
        return o != null ? Y9JsonUtil.writeValueAsString(o) : null;
    }

    /**
     * 获取多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getVariables", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getVariables(String tenantId, String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customVariableService.getVariables(taskId);
        return map;
    }

    /**
     * 获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量keys
     * @return Map
     */
    @Override
    @RequestMapping(value = "/getVariablesByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getVariablesByProcessInstanceId(String tenantId, String processInstanceId, @RequestBody Collection<String> keys) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return runtimeService.getVariables(processInstanceId, keys);
    }

    /**
     * 获取所有任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Map
     */
    @Override
    @GetMapping(value = "/getVariablesLocal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getVariablesLocal(String tenantId, String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customVariableService.getVariables(taskId);
        return map;
    }

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param val 变量值
     */
    @Override
    @PostMapping(value = "/setVariable", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariable(String tenantId, String taskId, String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariable(taskId, key, map.get("val"));
    }

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param val 变量值
     */
    @Override
    @PostMapping(value = "/setVariableByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariableByProcessInstanceId(String tenantId, String processInstanceId, String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        runtimeService.setVariable(processInstanceId, key, map.get("val"));
    }

    /**
     * 设置任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param val 变量值
     */
    @Override
    @PostMapping(value = "/setVariableLocal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariableLocal(String tenantId, String taskId, String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariableLocal(taskId, key, map.get("val"));
    }

    /**
     * 设置多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariables", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariables(String tenantId, String taskId, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariables(taskId, map);
    }

    /**
     * 设置多个任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariablesLocal", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariablesLocal(String tenantId, String taskId, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariablesLocal(taskId, map);
    }
}
