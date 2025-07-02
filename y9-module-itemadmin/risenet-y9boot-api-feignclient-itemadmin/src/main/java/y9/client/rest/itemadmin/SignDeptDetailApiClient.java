package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.SignDeptDetailApi;

/**
 * 会签信息接口
 *
 * @author qinman
 * @date 2024/12/13
 */
@FeignClient(contextId = "SignDeptDetailApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/signDeptDetail")
public interface SignDeptDetailApiClient extends SignDeptDetailApi {

}
