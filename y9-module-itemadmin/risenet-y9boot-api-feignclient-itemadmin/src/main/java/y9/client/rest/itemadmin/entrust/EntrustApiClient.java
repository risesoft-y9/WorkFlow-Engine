package y9.client.rest.itemadmin.entrust;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.entrust.EntrustApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "EntrustApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/entrust")
public interface EntrustApiClient extends EntrustApi {

}
