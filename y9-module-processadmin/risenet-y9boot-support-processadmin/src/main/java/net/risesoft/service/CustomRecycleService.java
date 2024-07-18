package net.risesoft.service;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomRecycleService {

    /**
     * 获取监控回收站统计
     *
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    long getRecycleCountByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 获取监控回收站统计
     *
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getRecycleCountBySystemName(String systemName);

    /**
     * 获取监控回收站统计
     *
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    long getRecycleCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 根据人员id获取监控回收站统计
     *
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getRecycleCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 获取回收站列表
     *
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Y9Page<HistoricProcessInstanceModel>
     */
    Y9Page<HistoricProcessInstanceModel> pageRecycleListByProcessDefinitionKey(String processDefinitionKey,
        Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param systemName 系统英文名称
     * @param page page
     * @param rows rows
     * @return Y9Page<HistoricProcessInstanceModel>
     */
    Y9Page<HistoricProcessInstanceModel> pageRecycleListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Y9Page<HistoricProcessInstanceModel>
     */
    Y9Page<HistoricProcessInstanceModel> pageRecycleListByUserIdAndProcessDefinitionKey(String userId,
        String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据人员id获取回收站列表
     *
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param page 当前页
     * @param rows 总条数
     * @return Y9Page<HistoricProcessInstanceModel>
     */
    Y9Page<HistoricProcessInstanceModel> pageRecycleListByUserIdAndSystemName(String userId, String systemName,
        Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByProcessDefinitionKey(String processDefinitionKey,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListBySystemName(String systemName, String searchTerm,
        Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param userId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndProcessDefinitionKey(String userId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * Description: 条件搜索在办件
     *
     * @param userId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndSystemName(String userId, String systemName,
        String searchTerm, Integer page, Integer rows);
}
