package y9.client.rest.itemadmin.worklist;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ItemMonitorApi;

/**
 * 监控接口
 *
 * @author qinman
 * @date 2024/12/19
 */
@FeignClient(contextId = "ItemMonitorApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemMonitor")
public interface ItemMonitorApiClient extends ItemMonitorApi {

}
