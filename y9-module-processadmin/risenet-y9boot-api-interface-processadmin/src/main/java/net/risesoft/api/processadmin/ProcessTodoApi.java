package net.risesoft.api.processadmin;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTodoApi {

    /**
     * 根据人员id，事项id获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return Y9Result<Y9FlowableCountModel>
     */
    Y9Result<Y9FlowableCountModel> getCountByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return Map&lt;String, Object&gt;
     */
    Y9Result<Y9FlowableCountModel> getCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<TaskModel>
     */
    Y9Page<TaskModel> getListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, Integer page, Integer rows);

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
    Y9Page<TaskModel> getListByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page,
        Integer rows) throws Exception;

    /**
     * 根据岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processDefinitionKey 流程定义key
     * @return Y9Result<Long> 待办数量
     */
    Y9Result<Long> getTodoCountByPositionIdAndProcessDefinitionKey(String tenantId, String positionId,
        String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return int 待办数量
     */
    Y9Result<Long> getTodoCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

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
    Y9Page<TaskModel> searchListByUserIdAndProcessDefinitionKey(String tenantId, String userId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows);

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
    Y9Page<TaskModel> searchListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String searchTerm, Integer page, Integer rows);
}
