package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.RejectReasonApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "RejectReasonApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/rejectReason")
public interface RejectReasonApiClient extends RejectReasonApi {

    /**
     * 保存退回/收回的原因
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     */
    @Override
    @PostMapping("/save")
    void save(@RequestParam("userId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("action") Integer action, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);
}
