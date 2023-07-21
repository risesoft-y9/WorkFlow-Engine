package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ButtonOperationApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ButtonOperationApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/buttonOperation")
public interface ButtonOperationApiClient extends ButtonOperationApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param activityId activityId
     * @param parentExecutionId parentExecutionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    @Override
    @PostMapping("/addMultiInstanceExecution")
    public void addMultiInstanceExecution(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId, @RequestParam("taskId") String taskId,
        @RequestParam("elementUser") String elementUser) throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    @Override
    @PostMapping("/deleteMultiInstanceExecution")
    public void deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("executionId") String executionId, @RequestParam("taskId") String taskId, @RequestParam("elementUser") String elementUser) throws Exception;

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    @Override
    @PostMapping("/directSend")
    public boolean directSend(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @return Map
     */
    @Override
    @PostMapping("/refuseClaimRollback")
    public Map<String, Object> refuseClaimRollback(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     * Description: 重定位
     * 
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param repositionToTaskId 目标任务节点
     * @param userChoice 目标用户
     * @param reason 原因
     * @param sponsorGuid 主办人
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("repositionToTaskId") String repositionToTaskId, @RequestBody List<String> userChoice, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid) throws Exception;

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollBack")
    public void rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollbackToSender")
    public void rollbackToSender(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param resson 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollbackToStartor")
    public void rollbackToStartor(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("reason") String resson) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping("/specialComplete")
    public void specialComplete(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping("/takeback")
    public void takeback(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;
}
