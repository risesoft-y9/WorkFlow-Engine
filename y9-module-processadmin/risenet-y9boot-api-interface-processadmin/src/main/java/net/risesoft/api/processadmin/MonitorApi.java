package net.risesoft.api.processadmin;

import java.util.Map;

/**
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
     * @return Integer
     */
    long getDoingCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控在办件数量
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getDoingCountBySystemName(String tenantId, String systemName);

    /**
     * 根据事项id获取监控在办件
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoingListByProcessDefinitionKey(String tenantId, String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 根据系统名称获取监控在办件
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoingListBySystemName(String tenantId, String systemName, Integer page, Integer rows);

    /**
     * 获取监控办结件统计
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    long getDoneCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控办结件统计
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getDoneCountBySystemName(String tenantId, String systemName);

    /**
     * 根据事项id获取监控办结件
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListByProcessDefinitionKey(String tenantId, String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 根据事项id获取监控办结件
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListBySystemName(String tenantId, String systemName, Integer page, Integer rows);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    long getRecycleCountByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getRecycleCountBySystemName(String tenantId, String systemName);

    /**
     * 获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    long getRecycleCountByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey);

    /**
     * 根据人员id获取监控回收站统计
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    long getRecycleCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByProcessDefinitionKey(String tenantId, String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListBySystemName(String tenantId, String systemName, Integer page, Integer rows);

    /**
     * 获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据人员id获取回收站列表
     * 
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param page 当前页
     * @param rows 总条数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        Integer page, Integer rows);

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchDoingListByProcessDefinitionKey(String tenantId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchDoingListBySystemName(String tenantId, String systemName, String searchTerm, Integer page,
        Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchDoneListByProcessDefinitionKey(String tenantId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchDoneListBySystemName(String tenantId, String systemName, String searchTerm, Integer page,
        Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchRecycleListByProcessDefinitionKey(String tenantId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchRecycleListBySystemName(String tenantId, String systemName, String searchTerm,
        Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchRecycleListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     * 
     * @param tenantId 租户id
     * @param userId 用户Id
     * @param systemName 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchRecycleListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String searchTerm, Integer page, Integer rows) throws Exception;
}
