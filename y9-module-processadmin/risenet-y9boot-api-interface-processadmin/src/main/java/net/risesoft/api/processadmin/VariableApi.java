package net.risesoft.api.processadmin;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import net.risesoft.pojo.Y9Result;

/**
 * 正在运行变量相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface VariableApi {

    /**
     * 删除流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/deleteVariable")
    Y9Result<Object> deleteVariable(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("key") String key);

    /***
     * 删除任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/deleteVariableLocal")
    Y9Result<Object> deleteVariableLocal(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("key") String key);

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @GetMapping("/getVariable")
    Y9Result<String> getVariable(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("key") String key);

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @GetMapping("/getVariableByProcessInstanceId")
    Y9Result<String> getVariableByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("key") String key);

    /**
     * 获取任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是任务变量
     * @since 9.6.6
     */
    @GetMapping("/getVariableLocal")
    Y9Result<String> getVariableLocal(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("key") String key);

    /**
     * 获取多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是流程变量
     * @since 9.6.6
     */
    @GetMapping("/getVariables")
    Y9Result<Map<String, Object>> getVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量keys
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是指定的流程变量
     * @since 9.6.6
     */
    @RequestMapping(value = "/getVariablesByProcessInstanceId", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Map<String, Object>> getVariablesByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestBody Collection<String> keys);

    /**
     * 获取所有任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是任务变量
     * @since 9.6.6
     */
    @GetMapping("/getVariablesLocal")
    Y9Result<Map<String, Object>> getVariablesLocal(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariable", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariable(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("key") String key, @RequestBody Map<String, Object> map);

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariableByProcessInstanceId", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariableByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("key") String key,
        @RequestBody Map<String, Object> map);

    /**
     * 设置任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param map 变量值
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariableLocal", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariableLocal(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("key") String key, @RequestBody Map<String, Object> map);

    /**
     * 设置多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariables(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestBody Map<String, Object> map);

    /**
     * 设置多个任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariablesLocal", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariablesLocal(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestBody Map<String, Object> map);

}
