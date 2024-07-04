package net.risesoft.api.processadmin;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 监控流程实例接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface MonitorApi {

    /**
     * 获取监控在办件统计
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     */
    Y9Result<Long> getDoingCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控在办件数量
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     */
    Y9Result<Long> getDoingCountBySystemName(String tenantId, String systemName);

    /**
     * 根据事项id获取监控在办件
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     */
    Y9Page<HistoricProcessInstanceModel> getDoingListByProcessDefinitionKey(String tenantId,
        String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据系统名称获取监控在办件
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     */
    Y9Page<HistoricProcessInstanceModel> getDoingListBySystemName(String tenantId, String systemName, Integer page,
        Integer rows);

    /**
     * 获取监控办结件统计
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 办结件统计
     */
    Y9Result<Long> getDoneCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 回收站统计
     */
    Y9Result<Long> getRecycleCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 回收站统计
     */
    Y9Result<Long> getRecycleCountBySystemName(String tenantId, String systemName);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 回收站统计
     */
    Y9Result<Long> getRecycleCountByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey);

    /**
     * 根据人员id获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 回收站统计
     */
    Y9Result<Long> getRecycleCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 回收站列表
     */
    Y9Page<HistoricProcessInstanceModel> getRecycleListByProcessDefinitionKey(String tenantId,
        String processDefinitionKey, Integer page, Integer rows);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 回收站列表
     */
    Y9Page<HistoricProcessInstanceModel> getRecycleListBySystemName(String tenantId, String systemName, Integer page,
        Integer rows);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 回收站列表
     */
    Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据人员id获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param page 当前页
     * @param rows 总条数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 回收站列表
     */
    Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndSystemName(String tenantId, String userId,
        String systemName, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(String tenantId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(String tenantId, String systemName,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByProcessDefinitionKey(String tenantId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListBySystemName(String tenantId, String systemName,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndProcessDefinitionKey(String tenantId,
        String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param userId 用户Id
     * @param systemName 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * 
     */
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndSystemName(String tenantId, String userId,
        String systemName, String searchTerm, Integer page, Integer rows);
}
