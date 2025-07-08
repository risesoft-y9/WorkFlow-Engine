package y9.client.rest.itemadmin.worklist;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.QueryListApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "QueryListApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/queryList")
public interface QueryListApiClient extends QueryListApi {

}
