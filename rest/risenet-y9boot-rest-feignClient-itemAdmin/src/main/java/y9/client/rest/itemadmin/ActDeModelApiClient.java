package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ActDeModelApi;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@FeignClient(contextId = "ActRuDetailApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/actDeModel")
public interface ActDeModelApiClient extends ActDeModelApi {

}
