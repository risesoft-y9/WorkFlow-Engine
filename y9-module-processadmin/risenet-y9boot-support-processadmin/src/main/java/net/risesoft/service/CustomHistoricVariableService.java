package net.risesoft.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.flowable.variable.api.history.HistoricVariableInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomHistoricVariableService {

    /**
     * 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     *
     * @param processInstanceId 流程实例Id
     * @param variableName 变量名称
     * @param year 年份
     * @return HistoricVariableInstance
     */
    HistoricVariableInstance getByProcessInstanceIdAndVariableName(String processInstanceId, String variableName,
        String year);

    /**
     * 根据流程实例Id和流程变量的Key,获取历史任务变量的值
     *
     * @param taskId 任务Id
     * @param variableName 变量名称
     * @param year 年份
     * @return HistoricVariableInstance
     */
    HistoricVariableInstance getByTaskIdAndVariableName(String taskId, String variableName, String year);

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @param keys 流程变量key集合
     * @return Map<String, Object>
     */
    Map<String, Object> getVariables(String tenantId, String processInstanceId, Collection<String> keys);

    /**
     * 根据流程实例Id,获取历史流程变量集合
     *
     * @param processInstanceId 流程实例Id
     * @return List<HistoricVariableInstance>
     */
    List<HistoricVariableInstance> listByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例Id,获取历史任务变量的值集合
     *
     * @param taskId 任务Id
     * @return List<HistoricVariableInstance>
     */
    List<HistoricVariableInstance> listByTaskId(String taskId);
}
