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
}
