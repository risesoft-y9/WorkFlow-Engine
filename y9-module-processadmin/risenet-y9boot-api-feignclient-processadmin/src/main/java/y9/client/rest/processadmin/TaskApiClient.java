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
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/task")
public interface TaskApiClient extends TaskApi {

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param userIdList 人员ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    @Override
    @PostMapping(value = "/createWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> createWithVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") String personId, @RequestParam("routeToTaskId") String routeToTaskId,
        @SpringQueryMap Map<String, Object> vars, @RequestBody List<String> userIdList);

    /**
     * 创建变量/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param positionIdList 岗位ids
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    @Override
    @PostMapping(value = "/createWithVariables1", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> createWithVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("personId") String personId,
        @RequestParam("routeToTaskId") String routeToTaskId, @SpringQueryMap Map<String, Object> vars,
        @RequestBody List<String> positionIdList);

}
