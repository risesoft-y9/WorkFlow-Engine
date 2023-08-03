package net.risesoft.api.itemadmin.position;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ButtonOperation4PositionApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId activityId
     * @param parentExecutionId parentExecutionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    public void addMultiInstanceExecution(String tenantId, String activityId, String parentExecutionId, String taskId,
        String elementUser) throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    public void deleteMultiInstanceExecution(String tenantId, String executionId, String taskId, String elementUser)
        throws Exception;

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    public boolean directSend(String tenantId, String positionId, String taskId, String routeToTask,
        String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return Map
     */
    public Map<String, Object> refuseClaimRollback(String tenantId, String positionId, String taskId);

    /**
     * 重定位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId repositionToTaskId
     * @param userChoice userChoice
     * @param reason reason
     * @param sponsorGuid sponsorGuid
     * @throws Exception exception
     */
    public void reposition(String tenantId, String positionId, String taskId, String repositionToTaskId,
        List<String> userChoice, String reason, String sponsorGuid) throws Exception;

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    public void rollBack(String tenantId, String positionId, String taskId, String reason) throws Exception;

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception exception
     */
    public void rollbackToSender(String tenantId, String positionId, String taskId) throws Exception;

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param resson 原因
     * @throws Exception exception
     */
    public void rollbackToStartor(String tenantId, String positionId, String taskId, String resson) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    public void specialComplete(String tenantId, String positionId, String taskId, String reason) throws Exception;

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    public void takeback(String tenantId, String positionId, String taskId, String reason) throws Exception;
}
