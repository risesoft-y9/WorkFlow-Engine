package net.risesoft.service.entrust;

import net.risesoft.entity.entrust.EntrustDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface EntrustDetailService {

    /**
     * 根据任务id获取委托信息
     *
     * @param taskId
     * @return
     */
    EntrustDetail findByTaskId(String taskId);

    /**
     * 保存委托信息
     *
     * @param processInstanceId
     * @param taskId
     * @param ownerId
     * @param assigneeId
     */
    void save(String processInstanceId, String taskId, String ownerId, String assigneeId);
}
