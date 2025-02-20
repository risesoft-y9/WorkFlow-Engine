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
     * Description: 退回至流转过的节点
     *
     * @param taskId
     * @param targetTaskDefineKey
     * @param users
     * @param reason
     * @param sponsorGuid
     */
    void rollBack2History(String taskId, String targetTaskDefineKey, List<String> users, String reason,
        String sponsorGuid);

    /**
     * 退回上一步
     *
     * @param taskId
     * @param reason
     */
    void rollBack(String taskId, String reason);

    /**
     * 返回发送人
     *
     * @param taskId
     */
    void rollbackToSender(String taskId);

    /**
     * 返回拟稿人
     *
     * @param taskId
     * @param reason
     */
    void rollbackToStartor(String taskId, String reason);

    /**
     * Description: 特殊办结
     *
     * @param taskId
     * @param reason
     */
    void specialComplete(String taskId, String reason);

    /**
     * 收回
     *
     * @param taskId
     * @param reason
     */
    void takeBack(String taskId, String reason);

    /**
     * 收回
     *
     * @param taskId
     * @param taskDefKey
     * @param reason
     */
    void takeBack2TaskDefKey(String taskId, String taskDefKey, String reason);
}
