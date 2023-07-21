package net.risesoft.api.processadmin;

import java.util.Collection;
import java.util.Map;

/**
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
     */
    void deleteVariable(String tenantId, String taskId, String key);

    /***
     * 删除任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     */
    void deleteVariableLocal(String tenantId, String taskId, String key);

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return Object
     */
    String getVariable(String tenantId, String taskId, String key);

    /**
     * 获取流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程id
     * @param key 变量key
     * @return Object
     */
    String getVariableByProcessInstanceId(String tenantId, String processInstanceId, String key);

    /**
     * 获取任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @return Object
     */
    String getVariableLocal(String tenantId, String taskId, String key);

    /**
     * 获取多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Map
     */
    Map<String, Object> getVariables(String tenantId, String taskId);

    /**
     * 获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量keys
     * @return Map
     */
    Map<String, Object> getVariablesByProcessInstanceId(String tenantId, String processInstanceId, Collection<String> keys);

    /**
     * 获取所有任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Map
     */
    Map<String, Object> getVariablesLocal(String tenantId, String taskId);

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param val 变量值
     */
    void setVariable(String tenantId, String taskId, String key, Map<String, Object> map);

    /**
     * 设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param val 变量值
     */
    void setVariableByProcessInstanceId(String tenantId, String processInstanceId, String key, Map<String, Object> map);

    /**
     * 设置任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param key 变量key
     * @param val 变量值
     */
    void setVariableLocal(String tenantId, String taskId, String key, Map<String, Object> map);

    /**
     * 这只多个流程变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    void setVariables(String tenantId, String taskId, Map<String, Object> map);

    /**
     * 设置多个任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    void setVariablesLocal(String tenantId, String taskId, Map<String, Object> map);
}
