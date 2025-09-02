package net.risesoft.service;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomDoingService {

    /**
     * 根据人员id和事项id统计
     *
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     * @return long
     */
    long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey);

    /**
     * 根据人员id和系统标识统计
     *
     * @param userId 用户id
     * @param systemName 系统标识
     * @return long
     */
    long getCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * 条件搜索在办件
     *
     * @param userId 用户id
     * @param processDefinitionKey 流程定义key
     * @param searchTerm 搜索条件
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<ProcessInstanceModel>
     */
    Y9Page<ProcessInstanceModel> pageSearchByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows);
}