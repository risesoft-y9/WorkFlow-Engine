package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.WorkflowApi;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.WorkflowProcessInstanceService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/workflow")
public class WorkflowApiImpl implements WorkflowApi {

    @Autowired
    private WorkflowProcessInstanceService workflowProcessInstanceService;

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getCurrentTaskTargets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getCurrentTaskTargets(String tenantId, String processDefinitionId,
        String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap =
            workflowProcessInstanceService.getCurrentTaskTargets(processDefinitionId, taskDefKey);
        return listMap;
    }
}
