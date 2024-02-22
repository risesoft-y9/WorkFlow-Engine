package net.risesoft.service;

import net.risesoft.entity.RejectReason;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface RejectReasonService {
    /**
     * 根据任务Id和标识查找原因
     *
     * @param taskId
     * @param action
     * @return
     */
    RejectReason findByTaskIdAndAction(String taskId, Integer action);

    /**
     * 保存拒绝原因
     *
     * @param reason
     * @param taskId
     * @param action
     */
    void save(String reason, String taskId, Integer action);
}
