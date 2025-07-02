package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.EleAttachmentApi;

/**
 * @author qinman
 * @date 2024/11/11
 */
@FeignClient(contextId = "EleAttachmentApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/eleAttachment")
public interface EleAttachmentApiClient extends EleAttachmentApi {

}
