package net.risesoft.api.processadmin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 正在运行任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TaskApi {

    /**
     * 签收任务
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/claim")
    Y9Result<Object> claim(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId);

    /**
     * 完成任务（不设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/complete")
    Y9Result<Object> complete(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 完成按钮的任务完结
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/completeTask")
    Y9Result<Object> completeTask(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 完成按钮的任务完结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位Id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/completeTaskWithoutAssignee")
    Y9Result<Object> completeTaskWithoutAssignee(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 完成任务（设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/completeWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> completeWithVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestBody Map<String, Object> map);

    /**
     * 完成任务（设置流程变量）岗位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param vars 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/completeWithVariables4Position", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> completeWithVariables4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestBody Map<String, Object> vars);

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param userIdList 人员ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/createWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> createWithVariables(String tenantId, String personId, String routeToTaskId,
        Map<String, Object> vars, List<String> userIdList);

    /**
     * 创建变量/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param positionIdList 岗位ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/createWithVariables4Position", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> createWithVariables4Position(String tenantId, String positionId, String personId,
        String routeToTaskId, Map<String, Object> vars, List<String> positionIdList);

    /**
     * 设置任务代理
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/delegateTask")
    Y9Result<Object> delegateTask(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("assignee") String assignee);

    /**
     * 删除任务的候选人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/deleteCandidateUser")
    Y9Result<Object> deleteCandidateUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("assignee") String assignee);

    /**
     * 查找所有的任务实例
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @GetMapping("/findAll")
    Y9Result<List<TaskModel>> findAll(@RequestParam("tenantId") String tenantId);

    /**
     * 根据任务id查找任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<TaskModel>} 通用请求返回对象 - data 任务信息
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<TaskModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 根据流程实例Id查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @GetMapping("/findByProcessInstanceId")
    Y9Result<List<TaskModel>> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return {@code Y9Result<List<TaskModel>>} 通用请求返回对象 - data 任务列表
     * @since 9.6.6
     */
    @GetMapping("/findByProcessInstanceId1")
    Y9Result<List<TaskModel>> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("active") boolean active);

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
    @GetMapping("/findListByProcessInstanceId")
    Y9Page<TaskModel> findListByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 保存任务
     *
     * @param tenantId 租户id
     * @param taskModel 任务实体
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/saveTask", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveTask(@RequestParam("tenantId") String tenantId, @RequestBody TaskModel taskModel);

    /**
     * 设置任务委托人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/setAssignee")
    Y9Result<Object> setAssignee(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("assignee") String assignee);

    /**
     * 设置任务的过期时间
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param date 日期
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/setDueDate")
    Y9Result<Object> setDueDate(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("date") Date date);

    /**
     * 设置任务的优先级
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param priority 优先级
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/setPriority")
    Y9Result<Object> setPriority(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("priority") Integer priority);

    /**
     * 撤销签收任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/unClaim")
    Y9Result<Object> unClaim(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

}
