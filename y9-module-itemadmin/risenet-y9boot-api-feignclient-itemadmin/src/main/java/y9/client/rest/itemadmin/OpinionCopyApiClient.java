package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.OpinionCopyApi;

/**
 * @author : qinman
 * @date : 2025-02-12
 * @since 9.6.8
 **/
@FeignClient(contextId = "OpinionCopyApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/opinionCopy")
public interface OpinionCopyApiClient extends OpinionCopyApi {}
