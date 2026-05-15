package net.risesoft.service;

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
}
