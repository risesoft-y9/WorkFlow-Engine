package net.risesoft.service;

import net.risesoft.entity.EntrustDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
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
     * 获取任务委托人id
     * 
     * @param taskId
     * @return
     */
    String getEntrustOwnerId(String taskId);

    /**
     * 判断该任务是否存在出差委托
     * 
     * @param taskId
     * @return
     */
    boolean haveEntrustDetailByTaskId(String taskId);

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
