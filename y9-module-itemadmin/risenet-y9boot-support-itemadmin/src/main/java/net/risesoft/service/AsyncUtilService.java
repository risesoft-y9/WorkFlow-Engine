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
     * 异步保存加签审计日志
     * 
     * @param tenantId
     * @param taskId
     * @param userId
     */
    void addMultiInstanceAuditLog(final String tenantId, final String taskId, final String userId);

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
}
