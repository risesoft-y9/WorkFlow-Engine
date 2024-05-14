package y9.client.rest.itemadmin.position;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.position.ItemLink4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemLink4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemLink4Position")
public interface ItemLink4PositionApiClient extends ItemLink4PositionApi {

}
