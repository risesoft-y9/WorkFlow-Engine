package y9.client.rest.processadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.processadmin.HistoricIdentityApi;

/**
 * @author qinman
 * @date 2024/12/23
 */
@FeignClient(contextId = "HistoricIdentityApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:server-processadmin}/services/rest/historicIdentity")
public interface HistoricIdentityApiClient extends HistoricIdentityApi {

}
