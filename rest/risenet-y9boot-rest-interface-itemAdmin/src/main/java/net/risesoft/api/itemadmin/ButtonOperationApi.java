package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ButtonOperationApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param activityId activityId
     * @param parentExecutionId parentExecutionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    void addMultiInstanceExecution(String tenantId, String userId, String activityId, String parentExecutionId,
        String taskId, String elementUser) throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    void deleteMultiInstanceExecution(String tenantId, String userId, String executionId, String taskId,
        String elementUser) throws Exception;

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    boolean directSend(String tenantId, String userId, String taskId, String routeToTask, String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @return Map
     */
    Map<String, Object> refuseClaimRollback(String tenantId, String userId, String taskId);

    /**
     * 重定位
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 人员id集合
     * @param reason 原因
     * @param sponsorGuid 主办人id
     * @throws Exception 异常
     */
    void reposition(String tenantId, String userId, String taskId, String repositionToTaskId, List<String> userChoice,
        String reason, String sponsorGuid) throws Exception;

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    void rollBack(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @throws Exception exception
     */
    void rollbackToSender(String tenantId, String userId, String taskId) throws Exception;

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param resson 原因
     * @throws Exception exception
     */
    void rollbackToStartor(String tenantId, String userId, String taskId, String resson) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    void specialComplete(String tenantId, String userId, String taskId, String reason) throws Exception;

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    void takeback(String tenantId, String userId, String taskId, String reason) throws Exception;
}
