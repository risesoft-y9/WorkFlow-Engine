package net.risesoft.service;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomDoingService {

    /**
     * 获取人员的在办流程数量
     *
     * @param userId 用户id
     * @return long
     */
    long getCountByUserId(String userId);

    /**
     * 根据人员id和事项id统计
     *
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     * @return long
     */
    long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 根据人员id和系统标识统计
     *
     * @param userId 用户id
     * @param systemName 系统标识
     * @return long
     */
    long getCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param userId 用户id
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageByUserId(String userId, Integer page, Integer rows);

    /**
     * 根据用户Id获取用户所有的在办流程
     *
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        Integer page, Integer rows);

    /**
     *
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageByUserIdAndSystemName(String userId, String systemName, Integer page,
        Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param userId 用户id
     * @param searchTerm 搜索条件
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageSearchByUserId(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     * @param searchTerm 搜索条件
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageSearchByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param userId 用户id
     * @param systemName 系统名称
     * @param searchTerm 搜索条件
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageSearchByUserIdAndSystemName(String userId, String systemName, String searchTerm,
        Integer page, Integer rows);
}