package y9.client.rest.itemadmin.worklist;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.worklist.ItemHaveDoneApi;

/**
 * 已办（包含在办和办结）接口
 *
 * @author qinman
 * @date 2024/12/18
 */
@FeignClient(contextId = "ItemHaveDoneApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemHaveDone")
public interface ItemHaveDoneApiClient extends ItemHaveDoneApi {

}
