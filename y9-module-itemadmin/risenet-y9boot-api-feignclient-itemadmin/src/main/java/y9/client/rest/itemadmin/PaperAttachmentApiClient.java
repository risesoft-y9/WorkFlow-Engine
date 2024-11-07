package y9.client.rest.itemadmin;

import net.risesoft.api.itemadmin.PaperAttachmentApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author qinman
 * @date 2024/11/07
 */
@FeignClient(contextId = "PaperAttachmentApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/paperAttachment")
public interface PaperAttachmentApiClient extends PaperAttachmentApi {

}
