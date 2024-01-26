package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.QuickSendApi;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@FeignClient(contextId = "QuickSendApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/quickSend")
public interface QuickSendApiClient extends QuickSendApi {

}
