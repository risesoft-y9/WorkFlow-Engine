package net.risesoft.api;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.WorkflowApi;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkflowProcessInstanceService;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 获取当前任务节点的目标节点
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/workflow")
public class WorkflowApiImpl implements WorkflowApi {

    private final WorkflowProcessInstanceService workflowProcessInstanceService;

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<TargetModel>>} 通用请求返回对象 - data 是目标节点信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getCurrentTaskTargets(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return workflowProcessInstanceService.getCurrentTaskTargets(processDefinitionId, taskDefKey);
    }
}
