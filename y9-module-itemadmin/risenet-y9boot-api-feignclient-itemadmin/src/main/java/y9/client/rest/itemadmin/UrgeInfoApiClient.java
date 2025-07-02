package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.UrgeInfoApi;

/**
 * 催办信息接口
 * 
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
@FeignClient(contextId = "UrgeInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/urgeInfo")
public interface UrgeInfoApiClient extends UrgeInfoApi {}
