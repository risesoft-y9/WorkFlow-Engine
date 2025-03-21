package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流转用户信息接口
 *
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
     * @return {@code Y9Result<List<IdentityLinkModel>>} 通用请求返回对象 - data 历任务的用户信息
     * @since 9.6.6
     */
    @GetMapping("/getIdentityLinksForTask")
    Y9Result<List<IdentityLinkModel>> getIdentityLinksForTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);
}
