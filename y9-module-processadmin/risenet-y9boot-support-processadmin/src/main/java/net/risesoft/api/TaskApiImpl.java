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

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final OrgUnitApi orgUnitApi;

    /**
     * 签收任务
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> claim(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.claim(taskId, userId);
        return Y9Result.success();
    }

    /**
     * 完成任务（不设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customTaskService.complete(taskId);
        return Y9Result.success();
    }

    /**
     * 完成按钮的任务完结
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeTask(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
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
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位Id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeTaskWithoutAssignee(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customTaskService.completeTaskWithoutAssignee(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 完成任务（设置流程变量）
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位Id
     * @param taskId 任务id
     * @param vars 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeWithVariables(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String orgUnitId, @RequestBody Map<String, Object> vars) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customTaskService.completeWithVariables(taskId, vars);
        return Y9Result.success();
    }

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位Id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param orgUnitIdList 人员、岗位ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> createWithVariables(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String routeToTaskId, @SpringQueryMap Map<String, Object> vars,
        @RequestBody List<String> orgUnitIdList) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customTaskService.createWithVariables(orgUnitId, vars, routeToTaskId, orgUnitIdList);
        return Y9Result.success();
    }

    /**
     * 设置任务代理
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delegateTask(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.delegateTask(taskId, assignee);
        return Y9Result.success();
    }

    /**
     * 删除任务的候选人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteCandidateUser(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.deleteCandidateUser(taskId, URLDecoder.decode(assignee, StandardCharsets.UTF_8));
        return Y9Result.success();
    }

    /**
     * 查找所有的任务实例
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findAll(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.findAll();
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 根据任务id查找任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<TaskModel>} 通用请求返回对象 - data 任务信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<TaskModel> findById(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Task task = customTaskService.findById(taskId);
        return Y9Result.success(FlowableModelConvertUtil.task2TaskModel(task));
    }

    /**
     * 根据流程实例Id查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.listByProcessInstanceId(processInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TaskModel>> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam boolean active) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.listByProcessInstanceIdAndActive(processInstanceId, active);
        return Y9Result.success(FlowableModelConvertUtil.taskList2TaskModelList(taskList));
    }

    /**
     * 根据流程实例id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用分页请求返回对象 - rows 是待办任务
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> findListByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTaskService.pageByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 保存任务
     *
     * @param tenantId 租户id
     * @param taskModel 任务实体
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveTask(@RequestParam String tenantId, @RequestBody TaskModel taskModel) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Task task = customTaskService.findById(taskModel.getId());
        FlowableModelConvertUtil.taskModel2Task(taskModel, task);
        customTaskService.saveTask(task);
        return Y9Result.success();
    }

    /**
     * 设置任务委托人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setAssignee(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.setAssignee(taskId, URLDecoder.decode(assignee, StandardCharsets.UTF_8));
        return Y9Result.success();
    }

    /**
     * 设置任务的过期时间
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param date 日期
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setDueDate(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam Date date) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.setDueDate(taskId, date);
        return Y9Result.success();
    }

    /**
     * 设置任务的优先级
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param priority 优先级
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setPriority(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam Integer priority) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.setPriority(taskId, priority);
        return Y9Result.success();
    }

    /**
     * 撤销签收任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> unClaim(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.unclaim(taskId);
        return Y9Result.success();
    }
}
