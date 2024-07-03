package net.risesoft.api;

import java.util.List;

import org.flowable.identitylink.api.IdentityLink;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomIdentityService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * 流转用户信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/identity")
public class IdentityApiImpl implements IdentityApi {

    private final CustomIdentityService customIdentityService;

    /**
     * 获取任务的用户信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return Y9Result<List<IdentityLinkModel>>
     */
    @Override
    public Y9Result<List<IdentityLinkModel>> getIdentityLinksForTask(@RequestParam String tenantId,
        @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<IdentityLink> list = customIdentityService.getIdentityLinksForTask(taskId);
        return Y9Result.success(FlowableModelConvertUtil.identityLinkList2ModelList(list));
    }
}
