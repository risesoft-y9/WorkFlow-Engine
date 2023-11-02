package y9.client.rest.itemadmin.position;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Entrust4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/entrust4Position")
public interface Entrust4PositionApiClient extends Entrust4PositionApi {

}
