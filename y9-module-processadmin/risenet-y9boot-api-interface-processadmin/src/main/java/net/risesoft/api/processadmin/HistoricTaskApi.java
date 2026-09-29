package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * 历史任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricTaskApi {

    /**
     * 根据流程实例id,获取任务
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/findTaskByProcessInstanceIdOrByEndTimeAsc")
    Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrByEndTimeAsc(
        @RequestParam String processInstanceId, @RequestParam(required = false) String year);

    /**
     * 根据流程实例id,开始时间升序获取任务
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/findTaskByProcessInstanceIdOrderByStartTimeAsc")
    Y9Result<List<HistoricTaskInstanceModel>> findTaskByProcessInstanceIdOrderByStartTimeAsc(
        @RequestParam String processInstanceId, @RequestParam(required = false) String year);

    /**
     * 根据任务Id获取任务实例
     *
     * @param taskId 任务id
     * @return {@code Y9Result<HistoricTaskInstanceModel>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/getById")
    Y9Result<HistoricTaskInstanceModel> getById(@RequestParam String taskId);

    /**
     * 根据任务Id获取任务实例
     *
     * @param taskId 任务id
     * @return {@code Y9Result<HistoricTaskInstanceModel>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/getByIdAndYear")
    Y9Result<HistoricTaskInstanceModel> getByIdAndYear(@RequestParam String taskId,
        @RequestParam(required = false) String year);

    /**
     * 根据流程实例获取所有历史任务实例
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/getByProcessInstanceId")
    Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestParam(required = false) String year);

    /**
     * 根据流程实例获取所有历史任务实例-按照办结时间倒序
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>>} 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/getByProcessInstanceIdOrderByEndTimeDesc")
    Y9Result<List<HistoricTaskInstanceModel>> getByProcessInstanceIdOrderByEndTimeDesc(
        @RequestParam String processInstanceId, @RequestParam(required = false) String year);

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 任务数量
     * @since 9.6.6
     */
    @GetMapping("/getFinishedCountByExecutionId")
    Y9Result<Long> getFinishedCountByExecutionId(@RequestParam String executionId);

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param taskId 任务id
     * @return {@code Y9Result<HistoricTaskInstanceModel>} 通用请求返回对象 - data 任务实例
     * @since 9.6.6
     */
    @GetMapping("/getThePreviousTask")
    Y9Result<HistoricTaskInstanceModel> getThePreviousTask(@RequestParam String taskId);

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param taskId 任务id
     * @return {@code Y9Result<List<HistoricTaskInstanceModel>> } 通用请求返回对象 - data 任务实例列表
     * @since 9.6.6
     */
    @GetMapping("/getThePreviousTasks")
    Y9Result<List<HistoricTaskInstanceModel>> getThePreviousTasks(@RequestParam String taskId);

    /**
     * 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/setTenantId")
    Y9Result<Object> setTenantId(@RequestParam String taskId);

}
