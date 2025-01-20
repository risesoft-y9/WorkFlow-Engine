package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.PreWorkInfoApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "PreWorkInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/preWorkInfo")
public interface PreWorkInfoApiClient extends PreWorkInfoApi {

}
