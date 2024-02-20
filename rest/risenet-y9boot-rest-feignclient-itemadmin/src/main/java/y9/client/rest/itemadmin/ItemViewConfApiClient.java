package y9.client.rest.itemadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.model.itemadmin.ItemViewConfModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemViewConfApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemViewConf")
public interface ItemViewConfApiClient extends ItemViewConfApi {

    /**
     * 获取事项视图配置列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return
     */
    @Override
    @GetMapping("/findByItemIdAndViewType")
    List<ItemViewConfModel> findByItemIdAndViewType(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("viewType") String viewType);
}
