package net.risesoft.service;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface OperationService {

    /**
     * Description: 重定向
     * 
     * @param taskId
     * @param targetTaskDefineKey
     * @param users
     * @param reason
     * @param sponsorGuid
     */
    void reposition(String taskId, String targetTaskDefineKey, List<String> users, String reason, String sponsorGuid);

    /**
     * Description: 重定向(岗位)
     * 
     * @param taskId
     * @param targetTaskDefineKey
     * @param users
     * @param reason
     * @param sponsorGuid
     */
    void reposition4Position(String taskId, String targetTaskDefineKey, List<String> users, String reason,
        String sponsorGuid);

    /**
     * 退回
     *
     * @param taskId 任务Id
     * @param reason 退回的原因
     */
    void rollBack(String taskId, String reason);

    /**
     * 退回（岗位）
     *
     * @param taskId
     * @param reason
     */
    void rollBack4Position(String taskId, String reason);

    /***
     * 返回发送人
     *
     * @param taskId 任务id
     */
    void rollbackToSender(String taskId);

    /**
     * 返回发送人
     *
     * @param taskId
     */
    void rollbackToSender4Position(String taskId);

    /***
     * 返回拟稿人
     *
     * @param taskId
     * @param reason
     */
    void rollbackToStartor(String taskId, String reason);

    /**
     * 返回拟稿人
     *
     * @param taskId
     * @param reason
     */
    void rollbackToStartor4Position(String taskId, String reason);

    /**
     * Description: 特殊办结
     * 
     * @param taskId
     * @param reason
     * @throws Exception
     */
    void specialComplete(String taskId, String reason) throws Exception;

    /**
     * Description: 特殊办结
     * 
     * @param taskId
     * @param reason
     * @throws Exception
     */
    void specialComplete4Position(String taskId, String reason) throws Exception;

    /**
     * 收回
     *
     * @param taskId 任务Id
     * @param reason 收回的原因
     */
    void takeBack(String taskId, String reason);

    /**
     * 收回(岗位)
     *
     * @param taskId
     * @param reason
     */
    void takeBack4Position(String taskId, String reason);
}
