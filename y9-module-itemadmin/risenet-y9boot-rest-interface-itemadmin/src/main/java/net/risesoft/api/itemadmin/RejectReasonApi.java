package net.risesoft.api.itemadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface RejectReasonApi {

    /**
     * 保存退回/收回的原因
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     */
    void save(String tenantId, String userId, Integer action, String taskId, String reason);
}
