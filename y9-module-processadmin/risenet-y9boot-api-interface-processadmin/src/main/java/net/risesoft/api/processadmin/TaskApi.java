package net.risesoft.api.processadmin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
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
     */
    Y9Result<Object> claim(String tenantId, String userId, String taskId);

    /**
     * 完成任务（不设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    Y9Result<Object> complete(String tenantId, String taskId);

    /**
     * 完成按钮的任务完结
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    Y9Result<Object> completeTask(String tenantId, String taskId);

    /**
     * 完成按钮的任务完结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位Id
     * @param processInstanceId 流程实例id
     */
    Y9Result<Object> completeTaskWithoutAssignee(String tenantId, String positionId, String processInstanceId);

    /**
     * 完成任务（设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     */
    Y9Result<Object> completeWithVariables(String tenantId, String taskId, Map<String, Object> map);

    /**
     * 完成任务（设置流程变量）岗位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param vars 变量map
     */
    Y9Result<Object> completeWithVariables4Position(String tenantId, String userId, String positionId, String taskId,
        Map<String, Object> vars);

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param userIdList 人员ids
     * @return TaskModel
     */
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
     * @return TaskModel
     */
    Y9Result<Object> createWithVariables(String tenantId, String positionId, String personId, String routeToTaskId,
        Map<String, Object> vars, List<String> positionIdList);

    /**
     * 设置任务代理
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     */
    Y9Result<Object> delegateTask(String tenantId, String taskId, String assignee);

    /**
     * 删除任务的候选人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     */
    Y9Result<Object> deleteCandidateUser(String tenantId, String taskId, String assignee);

    /**
     * 查找所有的任务实例
     *
     * @param tenantId 租户id
     * @return List&lt;TaskModel&gt;
     */
    List<TaskModel> findAll(String tenantId);

    /**
     * 根据任务id查找任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return TaskModel
     */
    Y9Result<TaskModel> findById(String tenantId, String taskId);

    /**
     * 根据流程实例Id查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;TaskModel&gt;
     */
    Y9Result<List<TaskModel>> findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return List&lt;TaskModel&gt;
     */
    Y9Result<List<TaskModel>> findByProcessInstanceId(String tenantId, String processInstanceId, boolean active);

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Y9Page<TaskModel> findListByProcessInstanceId(String tenantId, String processInstanceId, Integer page,
                                       Integer rows);

    /**
     * 保存任务
     *
     * @param tenantId 租户id
     * @param taskModel 任务实体
     */
    Y9Result<Object> saveTask(String tenantId, TaskModel taskModel);

    /**
     * 设置任务委托人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     */
    Y9Result<Object> setAssignee(String tenantId, String taskId, String assignee);

    /**
     * 设置任务的过期时间
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param date 日期
     */
    Y9Result<Object> setDueDate(String tenantId, String taskId, Date date);

    /**
     * 设置任务的优先级
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param priority 优先级
     */
    Y9Result<Object> setPriority(String tenantId, String taskId, Integer priority);

    /**
     * 撤销签收任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    Y9Result<Object> unClaim(String tenantId, String taskId);

}
