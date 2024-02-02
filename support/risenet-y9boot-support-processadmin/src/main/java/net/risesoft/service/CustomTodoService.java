package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomTodoService {

    /**
     * 获取人员的待办任务数量
     *
     * @param userId
     * @return
     */
    long getCountByUserId(String userId);

    /**
     * 根据人员id，或岗位id和事项id统计
     *
     * @param userId
     * @param processDefinitionKey
     * @return
     */
    long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 根据人员Id,系统标识统计
     *
     * @param userId
     * @param systemName
     * @return
     */
    long getCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 根据人员Id获取用户的待办任务(分页)
     * 
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserId(String userId, Integer page, Integer rows);

    /**
     * Description: 根据人员Id，查找人员所有的待办任务(包含流程变量和任务变量)(itemId为空则查询全部个人待办，不为空则查询当前事项应用待办)
     * 
     * @param userId
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, Integer page,
        Integer rows);

    /**
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getListByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param userId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm,
        Integer page, Integer rows);
}
