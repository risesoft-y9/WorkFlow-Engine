package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.SecretLevelRecordApi;

/**
 * @author yihong
 * @date 2025/02/12
 */
@FeignClient(contextId = "SecretLevelRecordApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/secretLevelRecord")
public interface SecretLevelRecordApiClient extends SecretLevelRecordApi {

}
