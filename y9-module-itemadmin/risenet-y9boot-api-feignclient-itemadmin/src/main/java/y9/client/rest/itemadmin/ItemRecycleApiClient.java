package y9.client.rest.itemadmin;

import net.risesoft.api.itemadmin.ItemDoneApi;
import net.risesoft.api.itemadmin.ItemRecycleApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "itemRecycleApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemRecycle")
public interface ItemRecycleApiClient extends ItemRecycleApi {

}
