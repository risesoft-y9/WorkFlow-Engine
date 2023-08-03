package y9.client.rest.itemadmin.position;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Entrust4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/entrust4Position")
public interface Entrust4PositionApiClient extends Entrust4PositionApi {

    /**
     * 根据用户唯一标示，查找当前时间所有委托对象的所有岗位Id集合
     *
     * @param tenantId 租户Id
     * @param assigneeId 被委托人Id
     * @param currentTime 当前时间
     * @return
     */
    @Override
    @GetMapping("/getPositionIdsByAssigneeIdAndTime")
    public List<String> getPositionIdsByAssigneeIdAndTime(@RequestParam("tenantId") String tenantId,
        @RequestParam("assigneeId") String assigneeId, @RequestParam("currentTime") String currentTime);
}
