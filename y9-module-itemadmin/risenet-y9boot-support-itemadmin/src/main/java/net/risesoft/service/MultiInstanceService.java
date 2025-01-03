package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface MultiInstanceService {

    /**
     * Description: 加签
     * 
     * @param activityId
     * @param parentExecutionId
     * @param taskId
     * @param elementUser
     */
    void addMultiInstanceExecution(String activityId, String parentExecutionId, String taskId, String elementUser);

    /**
     * Description: 加签
     *
     * @param activityId
     * @param parentExecutionId
     * @param elementUser
     */
    Y9Result<Object> addMultiInstanceExecution(String activityId, String parentExecutionId, String elementUser);

    /**
     * Description: 减签
     * 
     * @param executionId
     * @param taskId
     * @param elementUser
     */
    void deleteMultiInstanceExecution(String executionId, String taskId, String elementUser);
}
