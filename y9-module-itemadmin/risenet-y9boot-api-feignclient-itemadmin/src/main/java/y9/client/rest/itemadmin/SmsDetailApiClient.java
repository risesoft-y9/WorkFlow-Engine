package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.SmsDetailApi;

/**
 * @author qinman
 * @date 2025/08/22
 */
@FeignClient(contextId = "SmsDetailApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/smsDetail")
public interface SmsDetailApiClient extends SmsDetailApi {

}
