package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.SpecialOperationApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "SpecialOperationApiClient", name = "${y9.service.processAdmin.name:processAdmin}", url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/specialOperation")
public interface SpecialOperationApiClient extends SpecialOperationApi {

    /**
     *
     * Description: 重定向
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @param targetTaskDefineKey
     * @param users
     * @param reason
     * @param sponsorGuid
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    void reposition(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("targetTaskDefineKey") String targetTaskDefineKey,
        @RequestBody List<String> users, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid) throws Exception;

    /**
     *
     * Description: 重定向(岗位)
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param repositionToTaskId
     * @param userChoice
     * @param reason
     * @param sponsorGuid
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/reposition4Position", consumes = MediaType.APPLICATION_JSON_VALUE)
    void reposition4Position(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestParam("repositionToTaskId") String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid) throws Exception;

    /**
     * 退回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @throws Exception
     */
    @Override
    @PostMapping("/rollBack")
    void rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 退回（岗位）
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param reason
     * @throws Exception
     */
    @Override
    @PostMapping("/rollBack4Position")
    void rollBack4Position(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 发回给发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception
     */
    @Override
    @PostMapping("/rollbackToSender")
    void rollbackToSender(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId) throws Exception;

    /**
     *
     * Description: 发回给发送人
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @throws Exception
     */
    @Override
    @PostMapping("/rollbackToSender4Position")
    void rollbackToSender4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 返回拟稿人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception
     */
    @Override
    @PostMapping("/rollbackToStartor")
    void rollbackToStartor(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     *
     * Description: 返回拟稿人
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param reason
     * @throws Exception
     */
    @Override
    @PostMapping("/rollbackToStartor4Position")
    void rollbackToStartor4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception
     */
    @Override
    @PostMapping("/specialComplete")
    void specialComplete(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param reason
     * @throws Exception
     */
    @Override
    @PostMapping("/specialComplete4Position")
    void specialComplete4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason) throws Exception;

    /**
     * 收回办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @throws Exception
     */
    @Override
    @PostMapping("/takeBack")
    void takeBack(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 收回(岗位)
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param reason
     * @throws Exception
     */
    @Override
    @PostMapping("/takeBack4Position")
    void takeBack4Position(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;
}
