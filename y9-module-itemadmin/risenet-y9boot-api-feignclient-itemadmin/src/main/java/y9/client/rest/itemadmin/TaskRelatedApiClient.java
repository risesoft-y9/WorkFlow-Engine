package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.TaskRelatedApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "TaskRelatedApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
        url = "${y9.service.itemAdmin.directUrl:}",
        path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/taskRelated")
public interface TaskRelatedApiClient extends TaskRelatedApi {

}
