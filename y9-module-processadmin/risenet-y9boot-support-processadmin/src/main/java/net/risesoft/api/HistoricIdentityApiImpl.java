package net.risesoft.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.HistoricIdentityApi;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 历史流转用户信息接口
 *
 * @author qinman
 * @date 2024/12/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicIdentity")
public class HistoricIdentityApiImpl implements HistoricIdentityApi {

    private final CustomHistoricTaskService customHistoricTaskService;

    /**
     * 获取任务的用户信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<IdentityLinkModel>>} 通用请求返回对象 - data 历任务的用户信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<IdentityLinkModel>> getIdentityLinksForTask(@RequestParam String tenantId,
        @RequestParam String taskId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<IdentityLinkModel> list = customHistoricTaskService.listIdentityLinksForTaskByTaskId(taskId, year);
        return Y9Result.success(list);
    }
}
