package y9.client.rest.itemadmin.opinion;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.opinion.OpinionCopyApi;

/**
 * @author : qinman
 * @date : 2025-02-12
 * @since 9.6.8
 **/
@FeignClient(contextId = "OpinionCopyApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/opinionCopy")
public interface OpinionCopyApiClient extends OpinionCopyApi {}
