package net.risesoft.api;

import java.util.Collection;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomVariableService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 正在运行变量相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/variable")
public class VariableApiImpl implements VariableApi {

    private final CustomVariableService customVariableService;

    private final TaskService taskService;

    private final RuntimeService runtimeService;

    /**
     * 删除流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     */
    @Override
    @PostMapping(value = "/deleteVariable", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> deleteVariable(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.deleteVariable(taskId, key);
        return Y9Result.success();
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
    public Y9Result<Object> deleteVariableLocal(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.removeVariableLocal(taskId, key);
        return Y9Result.success();
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
    public Y9Result<String> getVariable(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = customVariableService.getVariable(taskId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
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
    public Y9Result<String> getVariableByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = runtimeService.getVariable(processInstanceId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
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
    public Y9Result<String> getVariableLocal(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Object o = taskService.getVariableLocal(taskId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
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
    public Y9Result<Map<String, Object>> getVariables(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customVariableService.getVariables(taskId));
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
    @RequestMapping(value = "/getVariablesByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Map<String, Object>> getVariablesByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestBody Collection<String> keys) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(runtimeService.getVariables(processInstanceId, keys));
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
    public Y9Result<Map<String, Object>> getVariablesLocal(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customVariableService.getVariables(taskId));
    }

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     */
    @Override
    @PostMapping(value = "/setVariable", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariable(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariable(taskId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量值
     */
    @Override
    @PostMapping(value = "/setVariableByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariableByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        runtimeService.setVariable(processInstanceId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 设置任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     */
    @Override
    @PostMapping(value = "/setVariableLocal", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariableLocal(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariableLocal(taskId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 设置多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariables", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariables(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariables(taskId, map);
        return Y9Result.success();
    }

    /**
     * 设置多个任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariablesLocal", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariablesLocal(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customVariableService.setVariablesLocal(taskId, map);
        return Y9Result.success();
    }
}
