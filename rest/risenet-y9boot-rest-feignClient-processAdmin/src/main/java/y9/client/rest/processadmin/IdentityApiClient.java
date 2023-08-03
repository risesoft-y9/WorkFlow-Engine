package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.model.processadmin.IdentityLinkModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "IdentityApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/identity")
public interface IdentityApiClient extends IdentityApi {

    /**
     * 获取任务的用户信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;IdentityLinkModel&gt;
     */
    @Override
    @GetMapping("/getIdentityLinksForTask")
    List<IdentityLinkModel> getIdentityLinksForTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);
}
