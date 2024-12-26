package net.risesoft.service;

import java.util.List;

import org.flowable.task.api.history.HistoricTaskInstance;

import net.risesoft.model.processadmin.IdentityLinkModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomHistoricTaskService {

    /**
     * 根据任务Id获取任务实例（在办）
     *
     * @param taskId
     * @return
     */
    HistoricTaskInstance getById(String taskId);

    /**
     * 根据任务Id获取任务实例
     *
     * @param taskId
     * @param year
     * @return
     */
    HistoricTaskInstance getByIdAndYear(String taskId, String year);

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param executionId
     * @return
     */
    long getFinishedCountByExecutionId(String executionId);

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param taskId
     * @return
     */
    HistoricTaskInstance getThePreviousTask(String taskId);

    /**
     * Description: 根据流程实例获取所有历史任务实例
     *
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricTaskInstance> listByProcessInstanceId(String processInstanceId, String year);

    /**
     * Description: 根据流程实例获取所有历史任务实例
     *
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeAsc(String processInstanceId, String year);

    /**
     * Description: 根据流程实例获取所有历史任务实例
     *
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeDesc(String processInstanceId, String year);

    /**
     * Description: 根据流程实例,开始时间升序获取所有历史任务实例
     *
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricTaskInstance> listByProcessInstanceIdOrderByStartTimeAsc(String processInstanceId, String year);

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param taskId
     * @return
     */
    List<HistoricTaskInstance> listThePreviousTasksByTaskId(String taskId);

    /**
     * Description: 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param taskId
     */
    void setTenantId(String taskId);

    /**
     * 获取任务的用户信息
     *
     * @param taskId
     * @return
     */
    List<IdentityLinkModel> listIdentityLinksForTaskByTaskId(String taskId);
}
