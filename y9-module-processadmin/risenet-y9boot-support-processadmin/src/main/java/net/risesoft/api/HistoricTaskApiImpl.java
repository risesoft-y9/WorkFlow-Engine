package net.risesoft.api;

import java.util.List;

import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 历史任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicTask")
public class HistoricTaskApiImpl implements HistoricTaskApi {

    private final CustomHistoricTaskService customHistoricTaskService;

    /**
     * 根据流程实例id获取历史任务（结束时间升序）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrByEndTimeAsc(
        @RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list =
            customHistoricTaskService.listByProcessInstanceIdOrderByEndTimeAsc(processInstanceId, year);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list));
    }

    /**
     * 根据流程实例id获取历史任务（开始时间升序）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrderByStartTimeAsc(
        @RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam(required = false) String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list =
            customHistoricTaskService.listByProcessInstanceIdOrderByStartTimeAsc(processInstanceId, year);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list));
    }

    /**
     * 根据任务Id获取任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<HistoricTaskInstanceModel>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricTaskInstanceModel> getById(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricTaskInstance hti = customHistoricTaskService.getById(taskId);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstance2Model(hti));
    }

    /**
     * 根据流程实例获取所有历史任务实例（开始时间正序）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam(required = false) String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list = customHistoricTaskService.listByProcessInstanceId(processInstanceId, year);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list));
    }

    /**
     * 根据流程实例获取所有历史任务实例（办结时间倒序）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceIdOrderByEndTimeDesc(
        @RequestParam String tenantId, @RequestParam String processInstanceId, String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list =
            customHistoricTaskService.listByProcessInstanceIdOrderByEndTimeDesc(processInstanceId, year);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list));
    }

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 任务数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getFinishedCountByExecutionId(@RequestParam String tenantId,
        @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(customHistoricTaskService.getFinishedCountByExecutionId(executionId));
    }

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<HistoricTaskInstanceModel>} 通用请求返回对象 - data 任务实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricTaskInstanceModel> getThePreviousTask(@RequestParam String tenantId,
        @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricTaskInstance hti = customHistoricTaskService.getThePreviousTask(taskId);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstance2Model(hti));
    }

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>> } 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricTaskInstanceModel>> getThePreviousTasks(@RequestParam String tenantId,
        @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> htiList = customHistoricTaskService.listThePreviousTasksByTaskId(taskId);
        return Y9Result.success(FlowableModelConvertUtil.historicTaskInstanceList2ModelList(htiList));
    }

    /**
     * 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setTenantId(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customHistoricTaskService.setTenantId(taskId);
        return Y9Result.success();
    }
}
