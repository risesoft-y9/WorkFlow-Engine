package y9.client.rest.itemadmin.core;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.core.ActRuDetailApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ActRuDetailApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/actRuDetail")
public interface ActRuDetailApiClient extends ActRuDetailApi {

}
