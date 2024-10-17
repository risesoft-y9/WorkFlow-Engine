package y9.client.rest.itemadmin.extend;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.extend.ItemMsgRemindApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemMsgRemindApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemMsgRemind")
public interface ItemMsgRemindApiClient extends ItemMsgRemindApi {

}
