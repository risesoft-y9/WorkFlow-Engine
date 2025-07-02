package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.PaperAttachmentApi;

/**
 * @author qinman
 * @date 2024/11/07
 */
@FeignClient(contextId = "PaperAttachmentApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/paperAttachment")
public interface PaperAttachmentApiClient extends PaperAttachmentApi {

}
