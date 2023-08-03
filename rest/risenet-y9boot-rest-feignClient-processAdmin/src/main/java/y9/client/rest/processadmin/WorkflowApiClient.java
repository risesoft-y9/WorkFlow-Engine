package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.WorkflowApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "WorkflowApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/workflow")
public interface WorkflowApiClient extends WorkflowApi {

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    @Override
    @GetMapping("/getCurrentTaskTargets")
    public List<Map<String, String>> getCurrentTaskTargets(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);
}
