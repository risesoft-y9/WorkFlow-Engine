package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ItemSystemApi;

/**
 * 事项系统接口
 *
 * @author qinman
 * @date 2026/07/10
 */
@FeignClient(contextId = "ItemSystemApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemSystem")
public interface ItemSystemApiClient extends ItemSystemApi {

}