package net.risesoft.service;

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
}
