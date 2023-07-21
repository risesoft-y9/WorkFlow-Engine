package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomDoneService {

    /**
     * 获取办结件统计
     *
     * @param userId
     * @return
     */
    long getCountByUserId(String userId);

    /**
     * 获取办结件统计
     *
     * @param userId
     * @param processDefinitionKey
     * @return
     */
    long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 获取办结件统计
     *
     * @param userId
     * @param systemName
     * @return
     */
    long getCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 根据人员Id获取用户的办结流程列表(分页,包含流程变量)
     *
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserId(String userId, Integer page, Integer rows);

    /**
     * Description: 根据人员Id获取用户的办结流程列表(分页,包含流程变量)
     * 
     * @param userId
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, Integer page, Integer rows);

    /**
     * Description: 根据人员Id获取用户的办结流程列表(分页,包含流程变量)
     * 
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows);

    /**
     * Description: 条件搜索办结件
     * 
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索办结件
     *
     * @param userId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索办结件
     *
     * @param userId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm, Integer page, Integer rows);
}
