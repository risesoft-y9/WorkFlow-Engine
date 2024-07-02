package net.risesoft.api.processadmin;

import net.risesoft.pojo.Y9Result;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SpecialOperationApi {

    /**
     * 重定向
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param targetTaskDefineKey 任务key
     * @param users 人员id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * 
     */
    Y9Result<Object> reposition(String tenantId, String userId, String taskId, String targetTaskDefineKey,
        List<String> users, String reason, String sponsorGuid);

    /**
     * 重定向(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * 
     */
    Y9Result<Object> reposition4Position(String tenantId, String positionId, String taskId, String repositionToTaskId,
        List<String> userChoice, String reason, String sponsorGuid);

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * 
     */
    Y9Result<Object> rollBack(String tenantId, String userId, String taskId, String reason);

    /**
     * 退回（岗位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * 
     */
    Y9Result<Object> rollBack4Position(String tenantId, String positionId, String taskId, String reason);

    /**
     * 发回给发送人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * 
     */
    Y9Result<Object> rollbackToSender4Position(String tenantId, String positionId, String taskId);

    /**
     * 返回拟稿人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    Y9Result<Object> rollbackToStartor4Position(String tenantId, String positionId, String taskId, String reason);

    /**
     * 特殊办结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    Y9Result<Object> specialComplete4Position(String tenantId, String positionId, String taskId, String reason);

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    Y9Result<Object> takeBack(String tenantId, String userId, String taskId, String reason);

    /**
     * 收回(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    Y9Result<Object> takeBack4Position(String tenantId, String positionId, String taskId, String reason);
}
