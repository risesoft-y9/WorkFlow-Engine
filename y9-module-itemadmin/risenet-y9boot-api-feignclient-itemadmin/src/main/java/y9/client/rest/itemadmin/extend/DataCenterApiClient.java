package y9.client.rest.itemadmin.extend;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.extend.DataCenterApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DataCenterApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/dataCenter")
public interface DataCenterApiClient extends DataCenterApi {

}
