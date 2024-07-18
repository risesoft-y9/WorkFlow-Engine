package net.risesoft.service;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;

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
     * 查询监控在办件列表
     *
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> pageDoingListByProcessDefinitionKey(String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 查询监控在办件列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> pageDoingListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 查询监控办结件列表
     *
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> pageDoneListByProcessDefinitionKey(String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 查询监控办结件列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> pageDoneListBySystemName(String systemName, Integer page, Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(String processDefinitionKey,
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
    Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(String systemName, String searchTerm, Integer page,
        Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Y9Page<HistoricProcessInstanceModel> searchDoneListByProcessDefinitionKey(String processDefinitionKey,
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
    Y9Page<HistoricProcessInstanceModel> searchDoneListBySystemName(String systemName, String searchTerm, Integer page,
        Integer rows);
}
