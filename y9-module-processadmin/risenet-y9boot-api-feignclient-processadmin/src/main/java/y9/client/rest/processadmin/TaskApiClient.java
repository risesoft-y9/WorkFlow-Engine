package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "TaskApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:server-processadmin}/services/rest/task")
public interface TaskApiClient extends TaskApi {

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位Id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param orgUnitIdList 人员、岗位ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    @Override
    @PostMapping(value = "/createWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> createWithVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("routeToTaskId") String routeToTaskId,
        @SpringQueryMap Map<String, Object> vars, @RequestBody List<String> orgUnitIdList);

}
