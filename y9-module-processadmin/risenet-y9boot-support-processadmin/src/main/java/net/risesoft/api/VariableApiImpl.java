package net.risesoft.api;

import java.util.Collection;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomVariableService;
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
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteVariable(@RequestParam String taskId, @RequestParam String key) {
        customVariableService.deleteVariable(taskId, key);
        return Y9Result.success();
    }

    /***
     * 删除任务变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteVariableLocal(@RequestParam String taskId, @RequestParam String key) {
        customVariableService.removeVariableLocal(taskId, key);
        return Y9Result.success();
    }

    /**
     * 根据任务id获取流程变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getVariable(@RequestParam String taskId, @RequestParam String key) {
        Object o = customVariableService.getVariable(taskId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
    }

    /**
     * 根据流程实例id获取流程变量
     *
     * @param processInstanceId 流程id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getVariableByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestParam String key) {
        Object o = runtimeService.getVariable(processInstanceId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
    }

    /**
     * 获取任务变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是任务变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getVariableLocal(@RequestParam String taskId, @RequestParam String key) {
        Object o = taskService.getVariableLocal(taskId, key);
        return Y9Result.success(o != null ? Y9JsonUtil.writeValueAsString(o) : null);
    }

    /**
     * 获取多个流程变量
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getVariables(@RequestParam String taskId) {
        return Y9Result.success(customVariableService.getVariables(taskId));
    }

    /**
     * 获取指定的流程变量
     *
     * @param processInstanceId 流程实例id
     * @param keys 变量keys
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是指定的流程变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getVariablesByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestBody Collection<String> keys) {
        return Y9Result.success(runtimeService.getVariables(processInstanceId, keys));
    }

    /**
     * 获取所有任务变量
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是任务变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getVariablesLocal(@RequestParam String taskId) {
        return Y9Result.success(customVariableService.getVariables(taskId));
    }

    /**
     * 根据任务id设置流程变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariable(@RequestParam String taskId, @RequestParam String key,
        @RequestBody Map<String, Object> map) {
        customVariableService.setVariable(taskId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariableByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestParam String key, @RequestBody Map<String, Object> map) {
        runtimeService.setVariable(processInstanceId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 设置任务变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariableLocal(@RequestParam String taskId, @RequestParam String key,
        @RequestBody Map<String, Object> map) {
        customVariableService.setVariableLocal(taskId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 设置多个流程变量
     *
     * @param taskId 任务id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariables(@RequestParam String taskId, @RequestBody Map<String, Object> map) {
        customVariableService.setVariables(taskId, map);
        return Y9Result.success();
    }

    /**
     * 设置多个任务变量
     *
     * @param taskId 任务id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariablesLocal(@RequestParam String taskId, @RequestBody Map<String, Object> map) {
        customVariableService.setVariablesLocal(taskId, map);
        return Y9Result.success();
    }
}
