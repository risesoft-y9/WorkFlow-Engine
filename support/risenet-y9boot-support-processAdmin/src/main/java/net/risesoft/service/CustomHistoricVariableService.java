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
     * 根据流程实例Id,获取历史流程变量集合
     *
     * @param processInstanceId
     * @return
     */
    List<HistoricVariableInstance> getByProcessInstanceId(String processInstanceId);

    /**
     * Description: 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     * 
     * @param processInstanceId
     * @param variableName
     * @param year
     * @return
     */
    HistoricVariableInstance getByProcessInstanceIdAndVariableName(String processInstanceId, String variableName, String year);

    /**
     * 根据流程实例Id,获取历史任务变量的值集合
     *
     * @param taskId
     * @return
     */
    List<HistoricVariableInstance> getByTaskId(String taskId);

    /**
     * Description: 根据流程实例Id和流程变量的Key,获取历史任务变量的值
     * 
     * @param taskId
     * @param variableName
     * @param year
     * @return
     */
    HistoricVariableInstance getByTaskIdAndVariableName(String taskId, String variableName, String year);

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId
     * @param processInstanceId
     * @param keys
     * @return
     */
    Map<String, Object> getVariables(String tenantId, String processInstanceId, Collection<String> keys);
}
