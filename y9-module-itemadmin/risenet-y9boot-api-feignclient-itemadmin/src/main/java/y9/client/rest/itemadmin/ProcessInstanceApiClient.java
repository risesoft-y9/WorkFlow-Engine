package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ProcessInstanceApi;

/**
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@FeignClient(contextId = "ProcessInstanceApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/processInstance")
public interface ProcessInstanceApiClient extends ProcessInstanceApi {

}
