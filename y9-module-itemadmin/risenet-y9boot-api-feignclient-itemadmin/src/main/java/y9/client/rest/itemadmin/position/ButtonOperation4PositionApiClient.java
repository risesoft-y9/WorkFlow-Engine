package y9.client.rest.itemadmin.position;

import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ButtonOperation4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/buttonOperation4Position")
public interface ButtonOperation4PositionApiClient extends ButtonOperation4PositionApi {

    
}
