package y9.client.rest.itemadmin.documentword;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.documentword.DocumentWordApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DocumentWordApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/docWord")
public interface DocumentWordApiClient extends DocumentWordApi {

}
