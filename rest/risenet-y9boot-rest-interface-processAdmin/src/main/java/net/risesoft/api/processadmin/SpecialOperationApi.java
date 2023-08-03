package net.risesoft.api.processadmin;

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
     * @throws Exception Exception
     */
    void reposition(String tenantId, String userId, String taskId, String targetTaskDefineKey, List<String> users,
        String reason, String sponsorGuid) throws Exception;

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
     * @throws Exception Exception
     */
    void reposition4Position(String tenantId, String positionId, String taskId, String repositionToTaskId,
        List<String> userChoice, String reason, String sponsorGuid) throws Exception;

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @throws Exception Exception
     */
    void rollBack(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 退回（岗位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @throws Exception Exception
     */
    void rollBack4Position(String tenantId, String positionId, String taskId, String reason) throws Exception;

    /**
     * 发回给发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    void rollbackToSender(String tenantId, String userId, String taskId) throws Exception;

    /**
     * 发回给发送人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    void rollbackToSender4Position(String tenantId, String positionId, String taskId) throws Exception;

    /**
     * 返回拟稿人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    void rollbackToStartor(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 返回拟稿人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    void rollbackToStartor4Position(String tenantId, String positionId, String taskId, String reason) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    void specialComplete(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 特殊办结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception Exception
     */
    void specialComplete4Position(String tenantId, String positionId, String taskId, String reason) throws Exception;

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @throws Exception Exception
     */
    void takeBack(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 收回(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @throws Exception Exception
     */
    void takeBack4Position(String tenantId, String positionId, String taskId, String reason) throws Exception;
}
