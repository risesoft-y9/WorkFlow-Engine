package net.risesoft.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.flowable.task.api.Task;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomTaskService {

    /**
     * 签收任务
     *
     * @param taskId
     * @param userId
     */
    void claim(String taskId, String userId);

    /**
     * 完成任务（不设置流程变量）
     *
     * @param taskId
     */
    void complete(String taskId);

    /**
     * Description: 办结流程
     *
     * @param processInstanceId
     * @param taskId
     * @throws Exception
     */
    void complete(String processInstanceId, String taskId) throws Exception;

    /**
     * 岗位使用
     *
     * @param processInstanceId
     */
    void completeTaskWithoutAssignee(String processInstanceId);

    /**
     * 完成任务（设置流程变量）
     *
     * @param taskId
     * @param map
     */
    void completeWithVariables(String taskId, Map<String, Object> map);

    /**
     * 创建变量
     *
     * @param orgUnitId
     * @param vars
     * @param routeToTaskId
     * @param orgUnitIdList
     * @return
     */
    TaskModel createWithVariables(String orgUnitId, Map<String, Object> vars, String routeToTaskId,
        List<String> orgUnitIdList);

    /**
     * 设置任务代理
     *
     * @param taskId
     * @param userId
     */
    void delegateTask(String taskId, String userId);

    /**
     * 删除任务的候选人
     *
     * @param taskId
     * @param userId
     */
    void deleteCandidateUser(String taskId, String userId);

    /**
     * 查找所有的任务实例
     *
     * @return
     */
    List<Task> findAll();

    /**
     * 根据任务id查找任务
     *
     * @param taskId
     * @return
     */
    Task findById(String taskId);

    /**
     * 获取当前任务所在并行节点的多个任务的已完成的数量
     *
     * @param taskId
     * @return
     */
    Integer getCompleteTaskCount4Parallel(String taskId);

    /**
     * 根据流程实例Id查找任务
     *
     * @param processInstanceId
     * @return
     */
    List<Task> listByProcessInstanceId(String processInstanceId);

    /**
     * Description: 根据流程实例Id查找任务
     *
     * @param processInstanceId
     * @param active
     * @return
     */
    List<Task> listByProcessInstanceIdAndActive(String processInstanceId, boolean active);

    /**
     * Description: 根据流程实例人员Id获取待办任务(分页)
     *
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<TaskModel> pageByProcessInstanceId(String processInstanceId, Integer page, Integer rows);

    /**
     * 完成按钮的任务完结
     *
     * @param taskId
     */
    void resolveTask(String taskId);

    /**
     * Description: 保存任务
     *
     * @param task
     */
    void saveTask(Task task);

    /**
     * 设置任务委托人
     *
     * @param taskId
     * @param userId
     */
    void setAssignee(String taskId, String userId);

    /**
     * 设置任务的过期时间
     *
     * @param taskId
     * @param date
     */
    void setDueDate(String taskId, Date date);

    /**
     * 设置任务的优先级
     *
     * @param taskId
     * @param priority
     */
    void setPriority(String taskId, Integer priority);

    /**
     * 撤销签收任务
     *
     * @param taskId
     */
    void unclaim(String taskId);
}
