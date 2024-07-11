package y9.client.rest.processadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.processadmin.ProcessDoingApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DoingApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/doing")
public interface ProcessDoingApiClient extends ProcessDoingApi {

}
