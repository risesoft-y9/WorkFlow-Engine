package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.WorkflowApi;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "WorkflowApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/workflow")
public interface WorkflowApiClient extends WorkflowApi {

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return Y9Result<List<TargetModel>>
     */
    @Override
    @GetMapping("/getCurrentTaskTargets")
    Y9Result<List<TargetModel>> getCurrentTaskTargets(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);
}
