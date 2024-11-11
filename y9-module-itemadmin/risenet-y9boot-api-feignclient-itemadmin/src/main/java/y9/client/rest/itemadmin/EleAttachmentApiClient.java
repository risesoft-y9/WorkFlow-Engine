package y9.client.rest.itemadmin;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.EleAttachmentApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author qinman
 * @date 2024/11/11
 */
@FeignClient(contextId = "EleAttachmentApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/eleAttachment")
public interface EleAttachmentApiClient extends EleAttachmentApi {

}
