package net.risesoft.service;

import java.util.Map;

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
     * @param tenantId 租户Id
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
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByProcessDefinitionKey(String processDefinitionKey, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param processDefinitionKey 事项id
     * @param systemName 系统英文名称
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, Integer page, Integer rows);

    /**
     * 根据人员id获取回收站列表
     *
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param page 当前页
     * @param rows 总条数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getRecycleListByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchRecycleListByProcessDefinitionKey(String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchRecycleListBySystemName(String systemName, String searchTerm, Integer page, Integer rows);

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
    Map<String, Object> searchRecycleListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows);

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
    Map<String, Object> searchRecycleListByUserIdAndSystemName(String userId, String systemName, String searchTerm, Integer page, Integer rows);
}
