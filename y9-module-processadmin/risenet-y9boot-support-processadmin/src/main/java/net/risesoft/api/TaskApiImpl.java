package net.risesoft.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9FlowableHolder;

/**
 * 正在运行任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/task", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskApiImpl implements TaskApi {

    private final CustomTaskService customTaskService;

    /**
     * 签收任务
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> claim(@RequestParam String taskId) {
        customTaskService.claim(taskId, Y9FlowableHolder.getPositionId());
        return Y9Result.success();
    }

    /**
     * 完成任务（不设置流程变量）
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String taskId) {
        customTaskService.complete(taskId);
        return Y9Result.success();
    }

    /**
     * 完成按钮的任务完结
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeTask(@RequestParam String taskId) {
        Task task = customTaskService.findById(taskId);
        if (DelegationState.PENDING == task.getDelegationState()) {
            String taskOwner = task.getOwner();
            customTaskService.resolveTask(taskId);
            customTaskService.setAssignee(taskId, taskOwner);
        }
        return Y9Result.success();
    }

    /**
     * 完成按钮的任务完结（相关参与人员的人任务办结）
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeTaskWithoutAssignee(@RequestParam String processInstanceId) {
        customTaskService.completeTaskWithoutAssignee(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 完成任务（设置流程变量）
     *
     * @param taskId 任务id
     * @param vars 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeWithVariables(@RequestParam String taskId, @RequestBody Map<String, Object> vars) {
        customTaskService.completeWithVariables(taskId, vars);
        return Y9Result.success();
    }

    /**
     * 创建变量
     *
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param orgUnitIdList 人员、岗位ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> createWithVariables(@RequestParam String routeToTaskId,
        @SpringQueryMap Map<String, Object> vars, @RequestBody List<String> orgUnitIdList) {
        customTaskService.createWithVariables(Y9FlowableHolder.getPositionId(), vars, routeToTaskId, orgUnitIdList);
        return Y9Result.success();
    }

    /**
     * 设置任务代理
     *
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delegateTask(@RequestParam String taskId, @RequestParam String assignee) {
        customTaskService.delegateTask(taskId, assignee);
        return Y9Result.success();
    }

    /**
     * 删除任务的候选人
     *
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteCandidateUser(@RequestParam String taskId, @RequestParam String assignee) {
        customTaskService.deleteCandidateUser(taskId, URLDecoder.decode(assignee, StandardCharsets.UTF_8));
        return Y9Result.success();
    }

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findActiveByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestParam boolean active) {
        List<Task> taskList = customTaskService.listByProcessInstanceIdAndActive(processInstanceId, active);
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 查找所有的任务实例
     *
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findAll() {
        List<Task> taskList = customTaskService.findAll();
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 根据任务id查找任务
     *
     * @param taskId 任务id
     * @return {@code Y9Result<TaskModel>} 通用请求返回对象 - data 任务信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<TaskModel> findById(@RequestParam String taskId) {
        Task task = customTaskService.findById(taskId);
        return Y9Result.success(FlowableModelConvertUtil.task2TaskModel(task));
    }

    /**
     * 根据流程实例Id查找任务
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findByProcessInstanceId(@RequestParam String processInstanceId) {
        List<Task> taskList = customTaskService.listByProcessInstanceId(processInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 根据流程实例id获取用户的待办任务(分页)
     *
     * @param processInstanceId 流程实例Id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用分页请求返回对象 - rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> findListByProcessInstanceId(@RequestParam String processInstanceId,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return customTaskService.pageByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 保存任务
     *
     * @param taskModel 任务实体
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveTask(@RequestBody TaskModel taskModel) {
        Task task = customTaskService.findById(taskModel.getId());
        FlowableModelConvertUtil.taskModel2Task(taskModel, task);
        customTaskService.saveTask(task);
        return Y9Result.success();
    }

    /**
     * 设置任务委托人
     *
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setAssignee(@RequestParam String taskId, @RequestParam String assignee) {
        customTaskService.setAssignee(taskId, URLDecoder.decode(assignee, StandardCharsets.UTF_8));
        return Y9Result.success();
    }

    /**
     * 设置任务的过期时间
     *
     * @param taskId 任务id
     * @param date 日期
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setDueDate(@RequestParam String taskId, @RequestParam Date date) {
        customTaskService.setDueDate(taskId, date);
        return Y9Result.success();
    }

    /**
     * 设置任务的优先级
     *
     * @param taskId 任务id
     * @param priority 优先级
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setPriority(@RequestParam String taskId, @RequestParam Integer priority) {
        customTaskService.setPriority(taskId, priority);
        return Y9Result.success();
    }

    /**
     * 撤销签收任务
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> unClaim(@RequestParam String taskId) {
        customTaskService.unclaim(taskId);
        return Y9Result.success();
    }
}
