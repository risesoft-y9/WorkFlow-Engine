package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface WorkflowApi {

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return Y9Result<List<TargetModel>>
     */
    @GetMapping("/getCurrentTaskTargets")
    Y9Result<List<TargetModel>> getCurrentTaskTargets(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);
}
