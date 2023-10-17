package net.risesoft.api.processadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTodoApi {

    /**
     * 根据人员idd获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id，事项id获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getCountByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserId(String tenantId, String userId, Integer page, Integer rows) throws Exception;

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey, Integer page, Integer rows) throws Exception;

    /**
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page, Integer rows) throws Exception;

    /**
     * 根据岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processDefinitionKey 流程定义key
     * @return long 待办数量
     */
    long getTodoCountByPositionIdAndProcessDefinitionKey(String tenantId, String positionId, String processDefinitionKey);

    /**
     * 根据人员id，事项id获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return int 待办数量
     */
    long getTodoCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id,事项id获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义key
     * @return int 待办数量
     */
    long getTodoCountByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return int 待办数量
     */
    long getTodoCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchListByUserId(String tenantId, String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchListByUserIdAndSystemName(String tenantId, String userId, String systemName, String searchTerm, Integer page, Integer rows);
}
