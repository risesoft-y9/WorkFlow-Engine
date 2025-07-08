package y9.client.rest.itemadmin.documentword;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.documentword.DocumentWpsApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DocumentWpsApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/documentWps")
public interface DocumentWpsApiClient extends DocumentWpsApi {

}
