package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;

/**
 * 工作流办件信息列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OfficeDoneInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/officeDoneInfo")
public interface OfficeDoneInfoApiClient extends OfficeDoneInfoApi {

}