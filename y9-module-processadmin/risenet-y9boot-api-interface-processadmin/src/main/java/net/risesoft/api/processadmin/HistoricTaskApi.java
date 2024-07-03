package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricTaskApi {

    /**
     * 根据流程实例id,获取任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return Y9Result<List<HistoricTaskInstanceModel>>
     */
    @GetMapping("/findTaskByProcessInstanceIdOrByEndTimeAsc")
    Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrByEndTimeAsc(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据开始时间升序获取
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return Y9Result<List<HistoricTaskInstanceModel>>
     */
    @GetMapping("/findTaskByProcessInstanceIdOrderByStartTimeAsc")
    Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrderByStartTimeAsc(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据任务Id获取任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<HistoricTaskInstanceModel>
     */
    @GetMapping("/getById")
    Y9Result<HistoricTaskInstanceModel> getById(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据流程实例获取所有历史任务实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return Y9Result<List<HistoricTaskInstanceModel>>
     */
    @GetMapping("/getByProcessInstanceId")
    Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据流程实例获取所有历史任务实例-按照办结时间倒序
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return Y9Result<List<HistoricTaskInstanceModel>>
     */
    @GetMapping("/getByProcessInstanceIdOrderByEndTimeDesc")
    Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceIdOrderByEndTimeDesc(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return Y9Result<Long> 任务数量
     */
    @GetMapping("/getFinishedCountByExecutionId")
    Y9Result<Long> getFinishedCountByExecutionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<HistoricTaskInstanceModel>
     */
    @GetMapping("/getThePreviousTask")
    Y9Result<HistoricTaskInstanceModel> getThePreviousTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<List<HistoricTaskInstanceModel>> 任务实例列表
     */
    @GetMapping("/getThePreviousTasks")
    Y9Result<List<HistoricTaskInstanceModel>> getThePreviousTasks(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 设置历史任务TENANT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<Object>
     */
    @PostMapping("/setTenantId")
    Y9Result<Object> setTenantId(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

}
