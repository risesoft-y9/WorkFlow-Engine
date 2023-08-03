package net.risesoft.api.processadmin;

import java.util.List;

import net.risesoft.model.processadmin.HistoricTaskInstanceModel;

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
     * @return List&lt;HistoricTaskInstanceModel&gt;
     */
    List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrByEndTimeAsc(String tenantId, String processInstanceId,
        String year);

    /**
     * 根据开始时间升序获取
     *
     * @param tenantId
     * @param processInstanceId
     * @param string
     * @return
     */
    List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrderByStartTimeAsc(String tenantId,
        String processInstanceId, String string);

    /**
     * 根据任务Id获取任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    HistoricTaskInstanceModel getById(String tenantId, String taskId);

    /**
     * 根据流程实例获取所有历史任务实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List&lt;HistoricTaskInstanceModel&gt;
     */
    List<HistoricTaskInstanceModel> getByProcessInstanceId(String tenantId, String processInstanceId, String year);

    /**
     * 根据流程实例获取所有历史任务实例-按照办结时间倒序
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List&lt;HistoricTaskInstanceModel&gt;
     */
    List<HistoricTaskInstanceModel> getByProcessInstanceIdOrderByEndTimeDesc(String tenantId, String processInstanceId,
        String year);

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return long
     */
    long getFinishedCountByExecutionId(String tenantId, String executionId);

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    HistoricTaskInstanceModel getThePreviousTask(String tenantId, String taskId);

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;HistoricTaskInstanceModel&gt;
     */
    List<HistoricTaskInstanceModel> getThePreviousTasks(String tenantId, String taskId);

    /**
     * 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    void setTenantId(String tenantId, String taskId);
}
