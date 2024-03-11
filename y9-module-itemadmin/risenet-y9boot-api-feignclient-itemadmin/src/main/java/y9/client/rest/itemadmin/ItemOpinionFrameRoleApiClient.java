package y9.client.rest.itemadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemOpinionFrameRoleApi;
import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemOpinionFrameRoleApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemOpinionFrameRole")
public interface ItemOpinionFrameRoleApiClient extends ItemOpinionFrameRoleApi {

    /**
     * 
     * Description: 查找绑定的意见框
     * 
     * @param tenantId
     * @param itemOpinionFrameId
     * @return
     */
    @Override
    @GetMapping("/findByItemOpinionFrameId")
    public List<ItemOpinionFrameRoleModel> findByItemOpinionFrameId(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemOpinionFrameId") String itemOpinionFrameId);
}
