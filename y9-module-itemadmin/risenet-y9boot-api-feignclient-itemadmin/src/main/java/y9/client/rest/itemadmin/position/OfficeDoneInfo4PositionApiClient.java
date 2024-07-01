package y9.client.rest.itemadmin.position;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;

/**
 * 工作流办件信息列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OfficeDoneInfo4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/officeDoneInfo4Position")
public interface OfficeDoneInfo4PositionApiClient extends OfficeDoneInfo4PositionApi {

}