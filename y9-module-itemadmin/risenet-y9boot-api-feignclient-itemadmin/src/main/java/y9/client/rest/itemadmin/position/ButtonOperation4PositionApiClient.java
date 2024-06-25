package y9.client.rest.itemadmin.position;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ButtonOperation4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/buttonOperation4Position")
public interface ButtonOperation4PositionApiClient extends ButtonOperation4PositionApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId activityId
     * @param parentExecutionId parentExecutionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/addMultiInstanceExecution")
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId,
        @RequestParam("taskId") String taskId, @RequestParam("elementUser") String elementUser) throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/deleteMultiInstanceExecution")
    public Y9Result<Object> deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId, @RequestParam("taskId") String taskId,
        @RequestParam("elementUser") String elementUser) throws Exception;

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    @Override
    @PostMapping("/directSend")
    public Y9Result<Object> directSend(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return Map
     */
    @Override
    @PostMapping("/refuseClaimRollback")
    public Y9Result<Object> refuseClaimRollback(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId);

    /**
     * 重定位 Description:
     *
     * @param tenantId
     * @param positionId
     * @param taskId
     * @param repositionToTaskId
     * @param userChoice
     * @param reason
     * @param sponsorGuid
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> reposition(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("repositionToTaskId") String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid) throws Exception;

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollBack")
    public Y9Result<Object> rollBack(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason) throws Exception;

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollbackToSender")
    public Y9Result<Object> rollbackToSender(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param resson 原因
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/rollbackToStartor")
    public Y9Result<Object> rollbackToStartor(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("resson") String resson) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/specialComplete")
    public Y9Result<Object> specialComplete(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason) throws Exception;

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return
     * @throws Exception exception
     */
    @Override
    @PostMapping("/takeback")
    public Y9Result<Object> takeback(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason) throws Exception;
}
