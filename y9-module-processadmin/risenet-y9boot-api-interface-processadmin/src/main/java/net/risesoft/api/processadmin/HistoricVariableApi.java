package net.risesoft.api.processadmin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * 历史变量相关接口
 *
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
     * @return {@code Y9Result<List<HistoricVariableInstanceModel>>} 通用请求返回对象 - data 历史任务变量的值列表
     * @since 9.6.6
     */
    @GetMapping("/getByProcessInstanceId")
    Y9Result<List<HistoricVariableInstanceModel>> getByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param variableName 变量名
     * @param year 年份
     * @return {@code Y9Result<HistoricVariableInstanceModel>} 通用请求返回对象 - data 历史任务变量的值
     * @since 9.6.6
     */
    @GetMapping("/getByProcessInstanceIdAndVariableName")
    Y9Result<HistoricVariableInstanceModel> getByProcessInstanceIdAndVariableName(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("variableName") String variableName, @RequestParam(value = "year", required = false) String year);

    /**
     * 根据任务Id,获取历史任务变量的值集合
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<HistoricVariableInstanceModel>>} 通用请求返回对象 - data 历史任务变量的值列表
     * @since 9.6.6
     */
    @GetMapping("/getByTaskId")
    Y9Result<List<HistoricVariableInstanceModel>> getByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据任务Id和变量Key,获取历史任务变量的值
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param variableName 变量名
     * @param year 年份
     * @return {@code Y9Result<HistoricVariableInstanceModel>} 通用请求返回对象 - data 历史任务变量的值
     * @since 9.6.6
     */
    @GetMapping("/getByTaskIdAndVariableName")
    Y9Result<HistoricVariableInstanceModel> getByTaskIdAndVariableName(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("variableName") String variableName,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量集合
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 流程变量
     * @since 9.6.6
     */
    @GetMapping(value = "/getVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Map<String, Object>> getVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestBody Collection<String> keys);

}
