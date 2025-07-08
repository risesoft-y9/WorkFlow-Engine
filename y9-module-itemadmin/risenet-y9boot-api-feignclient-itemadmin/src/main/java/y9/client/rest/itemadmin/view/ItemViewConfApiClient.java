package y9.client.rest.itemadmin.view;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.view.ItemViewConfApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemViewConfApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemViewConf")
public interface ItemViewConfApiClient extends ItemViewConfApi {

}
