package y9.client.rest.itemadmin.position;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.position.Document4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Document4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/document4Position")
public interface Document4PositionApiClient extends Document4PositionApi {

}
