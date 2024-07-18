package net.risesoft.service;

import java.util.List;

import org.flowable.identitylink.api.IdentityLink;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomIdentityService {

    /**
     * 由于任务产生的时候，删除流程的参与人，避免待办和在办同时出现，此时任务完成还原词流程的参与人
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     */
    void addParticipantUser(String tenantId, String userId, String processInstanceId);

    /**
     * 任务产生的时候，删除流程的参与人，避免待办和在办同时出现，等任务完成的时候再还原词流程的参与人
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     */
    void deleteParticipantUser(String tenantId, String userId, String processInstanceId);

    /**
     * 获取任务的用户信息
     *
     * @param taskId
     * @return
     */
    List<IdentityLink> listIdentityLinksForTaskByTaskId(String taskId);
}
