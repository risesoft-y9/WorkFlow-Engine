package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomMonitorService {

    /**
     * 监控在办件列表统计
     *
     * @param processDefinitionKey
     * @return
     */
    long getDoingCountByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 监控在办件列表统计
     *
     * @param systemName
     * @return
     */
    long getDoingCountBySystemName(String systemName);

    /**
     * 查询监控在办件列表
     *
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getDoingListByProcessDefinitionKey(String processDefinitionKey, Integer page, Integer rows);

    /**
     * 查询监控在办件列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getDoingListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 监控办结件列表统计
     *
     * @param systemName
     * @return
     */
    long getDoneCountByProcessDefinitionKey(String systemName);

    /**
     * 监控办结件列表统计
     *
     * @param systemName
     * @return
     */
    long getDoneCountBySystemName(String systemName);

    /**
     * 查询监控办结件列表
     *
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getDoneListByProcessDefinitionKey(String processDefinitionKey, Integer page, Integer rows);

    /**
     * 查询监控办结件列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getDoneListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchDoingListByProcessDefinitionKey(String processDefinitionKey, String searchTerm,
        Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchDoingListBySystemName(String systemName, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchDoneListByProcessDefinitionKey(String processDefinitionKey, String searchTerm,
        Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchDoneListBySystemName(String systemName, String searchTerm, Integer page, Integer rows);
}
