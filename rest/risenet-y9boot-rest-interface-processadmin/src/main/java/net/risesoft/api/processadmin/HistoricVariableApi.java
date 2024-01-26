package net.risesoft.api.processadmin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.risesoft.model.processadmin.HistoricVariableInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricVariableApi {

    /**
     * 根据流程实例Id,获取历史流程变量集合
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;HistoricVariableInstanceModel&gt;
     */
    List<HistoricVariableInstanceModel> getByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param variableName 变量名
     * @param year 年份
     * @return HistoricVariableInstanceModel
     */
    HistoricVariableInstanceModel getByProcessInstanceIdAndVariableName(String tenantId, String processInstanceId,
        String variableName, String year);

    /**
     * 根据流程实例Id,获取历史任务变量的值集合
     * 
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;HistoricVariableInstanceModel&gt;
     */
    List<HistoricVariableInstanceModel> getByTaskId(String tenantId, String taskId);

    /**
     * 根据流程实例Id和流程变量的Key,获取历史任务变量的值
     * 
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param variableName 变量名
     * @param year 年份
     * @return HistoricVariableInstanceModel
     */
    HistoricVariableInstanceModel getByTaskIdAndVariableName(String tenantId, String taskId, String variableName,
        String year);

    /**
     * 根据流程实例Id,获取指定的流程变量
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量集合
     * @return Map
     */
    Map<String, Object> getVariables(String tenantId, String processInstanceId, Collection<String> keys);
}
