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
     * 获取任务的用户信息
     *
     * @param taskId 任务id
     * @return List<IdentityLink>
     */
    List<IdentityLink> listIdentityLinksForTaskByTaskId(String taskId);
}
