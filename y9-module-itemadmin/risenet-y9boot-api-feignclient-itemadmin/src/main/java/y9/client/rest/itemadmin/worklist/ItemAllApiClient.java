package y9.client.rest.itemadmin.worklist;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ItemAllApi;

/**
 * 所有本人经手的件接口
 *
 * @author qinman
 * @date 2024/12/19
 */
@FeignClient(contextId = "ItemAllApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemAll")
public interface ItemAllApiClient extends ItemAllApi {

}
