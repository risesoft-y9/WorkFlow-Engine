package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "SpecialOperationApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/specialOperation")
public interface SpecialOperationApiClient extends SpecialOperationApi {

    /**
     * 重定向
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param targetTaskDefineKey 任务key
     * @param users 人员id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     *
     */
    @Override
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> reposition(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("targetTaskDefineKey") String targetTaskDefineKey,
        @RequestBody List<String> users, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid);

    /**
     * 重定向(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     *
     */
    @Override
    @PostMapping(value = "/reposition4Position", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> reposition4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("repositionToTaskId") String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid);

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 退回的原因
     *
     */
    @Override
    @PostMapping("/rollBack")
    Y9Result<Object> rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason);

    /**
     * 退回（岗位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 退回的原因
     *
     */
    @Override
    @PostMapping("/rollBack4Position")
    Y9Result<Object> rollBack4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);

    /**
     * 发回给发送人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     *
     */
    @Override
    @PostMapping("/rollbackToSender4Position")
    Y9Result<Object> rollbackToSender4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId);

    /**
     * 返回拟稿人/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     *
     */
    @Override
    @PostMapping("/rollbackToStartor4Position")
    Y9Result<Object> rollbackToStartor4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);

    /**
     * 特殊办结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     *
     */
    @Override
    @PostMapping("/specialComplete4Position")
    Y9Result<Object> specialComplete4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 收回的原因
     *
     */
    @Override
    @PostMapping("/takeBack")
    Y9Result<Object> takeBack(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason);

    /**
     * 收回(岗位)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 收回的原因
     *
     */
    @Override
    @PostMapping("/takeBack4Position")
    Y9Result<Object> takeBack4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);
}
