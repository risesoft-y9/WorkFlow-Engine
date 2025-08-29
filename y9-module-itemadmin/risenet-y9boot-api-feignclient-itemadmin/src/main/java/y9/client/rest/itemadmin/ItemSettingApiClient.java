package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ItemSettingApi;

/**
 * 事项配置信息接口
 *
 * @author qinman
 * @date 2025/08/28
 */
@FeignClient(contextId = "ItemSettingApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/itemSetting")
public interface ItemSettingApiClient extends ItemSettingApi {

}