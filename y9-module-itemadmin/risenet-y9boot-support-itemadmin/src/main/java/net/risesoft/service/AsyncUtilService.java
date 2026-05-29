package net.risesoft.service;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AsyncUtilService {

    /**
     * 异步循环发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     */
    void loopSending(final String tenantId, final String orgUnitId, final String itemId,
        final String processInstanceId);

    /**
     * 指定节点收回任务审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param taskId
     * @param taskDefKey
     */
    void takeBackTwoTaskDefKeyAuditLog(final String tenantId, final String orgUnitId, final String taskId,
        String taskDefKey);

    /**
     * 快速发送审计日志
     *
     * @param tenantId
     * @param orgUnitId
     * @param itemId
     * @param taskKey
     * @param assignee
     * @param optType
     */
    void quickSendAuditLog(final String tenantId, final String orgUnitId, final String itemId, final String taskKey,
        final String assignee, final String optType);

    /**
     * 提醒消息审计日志
     * 
     * @param tenantId
     * @param orgUnitId
     * @param taskIds
     * @param processInstanceId
     * @param process
     * @param arriveTaskKey
     * @param completeTaskKey
     */
    void remindMsgAuditLog(final String tenantId, final String orgUnitId, final String taskIds,
        final String processInstanceId, final Boolean process, final String arriveTaskKey,
        final String completeTaskKey);

    /**
     * 异步保存发送审计日志
     *
     * @param tenantId
     * @param userIds
     */
    void sendAuditLog(final String tenantId, final String title, final String userIds);

    /**
     * 异步保存发送审计日志
     *
     * @param tenantId
     * @param userIdList
     */
    void sendAuditLog(final String tenantId, final String title, final List<String> userIdList);

    /**
     * 异步保存办件提交发送审计日志
     *
     * @param tenantId
     * @param title
     * @param userIdList
     */
    void submitSendAuditLog(final String tenantId, final String title, final List<String> userIdList);

    /**
     * 异步保存关联文件审计日志
     *
     * @param tenantId
     * @param processInstanceIds
     */
    void saveAssociatedFileAuditLog(final String tenantId, final String processInstanceIds);

    /**
     * 异步保存删除关联文件审计日志
     *
     * @param processInstanceIds
     */
    void deleteAssociatedFileAuditLog(final String processInstanceIds);
}
