package net.risesoft.api.processadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DoingApi {

    /**
     * 根据人员id获取在办件统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map&lt;String, Object&gt;
     */
    long getCountByUserId(String tenantId, String userId);

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserId(String tenantId, String userId, Integer page, Integer rows) throws Exception;

    /**
     * 根据人员Id,事项ID获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey, Integer page, Integer rows);

    /**
     * 获取已办件列表，按办理的时间排序
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义key
     * @param page 页码
     * @param rows 行数
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKeyOrderBySendTime(String tenantId, String userId, String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据人员Id,系统标识获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchListByUserId(String tenantId, String userId, String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchListByUserIdAndSystemName(String tenantId, String userId, String systemName, String searchTerm, Integer page, Integer rows) throws Exception;
}
