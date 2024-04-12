package net.risesoft.api;

import java.util.List;

import org.flowable.identitylink.api.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.model.processadmin.IdentityLinkModel;
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
@RequestMapping(value = "/services/rest/identity")
public class IdentityApiImpl implements IdentityApi {

    @Autowired
    private CustomIdentityService customIdentityService;

    /**
     * 获取任务的用户信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<IdentityLinkModel>
     */
    @Override
    @GetMapping(value = "/getIdentityLinksForTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IdentityLinkModel> getIdentityLinksForTask(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<IdentityLink> list = customIdentityService.getIdentityLinksForTask(taskId);
        List<IdentityLinkModel> model = FlowableModelConvertUtil.identityLinkList2ModelList(list);
        return model;
    }
}
