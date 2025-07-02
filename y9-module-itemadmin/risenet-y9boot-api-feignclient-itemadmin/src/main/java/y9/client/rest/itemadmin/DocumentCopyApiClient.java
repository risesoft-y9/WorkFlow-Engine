package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.DocumentCopyApi;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
@FeignClient(contextId = "DocumentCopyApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/documentCopy")
public interface DocumentCopyApiClient extends DocumentCopyApi {}
