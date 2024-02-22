package net.risesoft.service;

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
     * @throws Exception
     */
    void addMultiInstanceExecution(String activityId, String parentExecutionId, String taskId, String elementUser)
        throws Exception;

    /**
     * Description: 减签
     * 
     * @param executionId
     * @param taskId
     * @param elementUser
     * @throws Exception
     */
    void deleteMultiInstanceExecution(String executionId, String taskId, String elementUser) throws Exception;
}
