package net.risesoft.service;

import java.util.List;

public interface AsyncUtilService {

    /**
     * 更新统一待办，抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     */
    void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle);

    /**
     * 恢复待办审计日志
     * 
     * @param tenantId
     * @param processInstanceId
     * @param title
     */
    void resumeToDoAuditLog(final String tenantId, final String processInstanceId, final String title);

    /**
     * 保存签收任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     */
    void claimAuditLog(final String tenantId, final String orgUnitId, final String taskId);

    /**
     * 发送给发起人审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     */
    void sendStarterAuditLog(final String tenantId, final String orgUnitId, final String taskId);

    /**
     * 并行处理审计日志
     * 
     * @param tenantId
     * @param taskName
     * @param processInstanceId
     */
    void handleParallelAuditLog(final String tenantId, final String taskName, final String processInstanceId);

    /**
     * 串行处理审计日志
     *
     * @param tenantId
     * @param taskName
     * @param processInstanceId
     */
    void handleSerialAuditLog(final String tenantId, final String taskName, final String processInstanceId);

    /**
     * 任务委托审计日志
     * 
     * @param tenantId
     * @param taskId
     * @param orgUnitId
     */
    void reAssignAuditLog(final String tenantId, final String taskId, final String orgUnitId);

    /**
     * 拒绝签收任务审计日志
     *
     * @param tenantId
     * @param taskId
     * @param orgUnitId
     */
    void refuseClaimAuditLog(final String tenantId, final String taskId, final String orgUnitId);

    /**
     * 最后一人拒绝签收任务回滚审计日志
     *
     * @param tenantId
     * @param taskId
     * @param orgUnitId
     */
    void refuseClaimRollbackAuditLog(final String tenantId, final String taskId, final String orgUnitId);

    /**
     * 任务重新定位审计日志
     * 
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param targetTaskKey
     * @param users
     */
    void repositionAuditLog(final String tenantId, final String orgUnitId, final String taskId,
        final String targetTaskKey, final List<String> users);

    /**
     * 多步退回任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param targetTaskKey
     * @param users
     */
    void rollBackTwoHistoryAuditLog(final String tenantId, final String orgUnitId, final String taskId,
        final String targetTaskKey, final List<String> users);

    /**
     * 单步退回任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param reason
     */
    void rollbackAuditLog(final String tenantId, final String orgUnitId, final String taskId, final String reason);

    /**
     * 退回发送人审计日志
     * 
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param optType
     */
    void rollbackToSenderAuditLog(final String tenantId, final String orgUnitId, final String taskId,
        final String optType);

    /**
     * 退回发起人审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param optType
     */
    void rollbackToStartorAuditLog(final String tenantId, final String orgUnitId, final String taskId,
        final String optType);

    /**
     * 特殊完成任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     */
    void specialCompleteAuditLog(final String tenantId, final String orgUnitId, final String taskId);

    /**
     * 收回任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     */
    void takebackAuditLog(final String tenantId, final String orgUnitId, final String taskId);

    /**
     * 撤回签收任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     */
    void unclaimAuditLog(final String tenantId, final String orgUnitId, final String taskId);

    /**
     * 删除待办审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param processSerialNumber
     */
    void deleteToDoAuditLog(final String tenantId, final String orgUnitId, final String processSerialNumber);

    /**
     * 恢复待办审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param processSerialNumber
     */
    void recoveryToDoAuditLog(final String tenantId, final String orgUnitId, final String processSerialNumber);

    /**
     * 删除待办审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param processSerialNumber
     * @param title
     */
    void removeToDoAuditLog(final String tenantId, final String orgUnitId, final String processSerialNumber,
        final String title);

    /**
     * 异步保存并行加签审计日志
     *
     * @param tenantId
     * @param userName
     * @param title
     * @param taskName
     * @param taskId
     * @param users
     */
    void addMultiInstanceParallelAuditLog(final String tenantId, final String userName, final String title,
        final String taskId, final String taskName, final String users);

    /**
     * 异步保存串行加签审计日志
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskId
     * @param users
     */
    void addExecutionIdSequentialAuditLog(final String tenantId, final String userId, final String processInstanceId,
        final String taskId, final List<String> users);

    /**
     * 异步保存并行减签审计日志
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskId
     * @param users
     */
    void deleteMultiInstanceParallelAuditLog(final String tenantId, final String userId, final String processInstanceId,
        final String taskId, final String users);

    /**
     * 异步保存串行减签审计日志
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskId
     * @param users
     */
    void deleteMultiInstanceSequentialAuditLog(final String tenantId, final String userId,
        final String processInstanceId, final String taskId, final String users);
}
