package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.PrintApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "PrintApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/print")
public interface PrintApiClient extends PrintApi {

    /**
     * 打开打印模板
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/openDocument")
    public String openDocument(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itemId") String itemId);

}
