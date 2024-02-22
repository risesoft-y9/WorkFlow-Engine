package net.risesoft.api.processadmin;

import java.util.List;

import net.risesoft.model.processadmin.IdentityLinkModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface IdentityApi {

    /**
     * 获取任务的用户信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;IdentityLinkModel&gt;
     */
    List<IdentityLinkModel> getIdentityLinksForTask(String tenantId, String taskId);
}
