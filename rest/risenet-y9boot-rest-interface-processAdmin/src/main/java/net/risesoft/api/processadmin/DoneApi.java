package net.risesoft.api.processadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DoneApi {

    /**
     * 根据人员Id获取用户的办结流程列表(分页,包含流程变量)
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
     * 根据人员Id,事项ID获取用户的办结流程列表(分页,包含流程变量)
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, Integer page, Integer rows) throws Exception;

    /**
     * 根据人员Id,系统标识获取用户的办结流程列表(分页,包含流程变量)
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getListByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page,
        Integer rows) throws Exception;

    /**
     * 条件搜索办结件
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchListByUserId(String tenantId, String userId, String searchTerm, Integer page,
        Integer rows) throws Exception;

    /**
     * 条件搜索办结件
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
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows) throws Exception;

    /**
     * 条件搜索办结件
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> searchListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String searchTerm, Integer page, Integer rows) throws Exception;
}
