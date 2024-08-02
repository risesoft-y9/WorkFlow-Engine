package net.risesoft.service;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomTodoService {

    /**
     * 获取人员、岗位的待办任务数量
     *
     * @param userId
     * @return
     */
    long getCountByUserId(String userId);

    /**
     * 根据人员id，或岗位id和事项id统计
     *
     * @param userId
     * @param processDefinitionKey
     * @return
     */
    long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 根据人员、岗位id,系统标识统计
     *
     * @param userId
     * @param systemName
     * @return
     */
    long getCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 根据人员、岗位id获取用户的待办任务(分页)
     *
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<TaskModel> pageByUserId(String userId, Integer page, Integer rows);

    /**
     * 根据人员、岗位id，事项id获取用户的待办任务(分页)
     *
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<TaskModel>
     */
    Y9Page<TaskModel> pageByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return Y9Page<TaskModel>
     */
    Y9Page<TaskModel> pageByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<TaskModel> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return Y9Page<TaskModel>
     */
    Y9Page<TaskModel> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<TaskModel> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm, Integer page,
        Integer rows);
}
