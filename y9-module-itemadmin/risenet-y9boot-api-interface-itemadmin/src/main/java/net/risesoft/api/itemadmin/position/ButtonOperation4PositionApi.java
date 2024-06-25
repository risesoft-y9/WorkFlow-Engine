package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ButtonOperation4PositionApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId activityId
     * @param parentExecutionId parentExecutionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    @PostMapping("/addMultiInstanceExecution")
    Y9Result<Object> addMultiInstanceExecution(@RequestParam("tenantId") String tenantId, @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId, @RequestParam("taskId") String taskId, @RequestParam("elementUser") String elementUser)
        throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @throws Exception exception
     */
    @PostMapping("/deleteMultiInstanceExecution")
    Y9Result<Object> deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId, @RequestParam("executionId") String executionId, @RequestParam("taskId") String taskId, @RequestParam("elementUser") String elementUser) throws Exception;

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
    @PostMapping("/directSend")
    Y9Result<Object> directSend(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return Map
     */
    @PostMapping("/refuseClaimRollback")
    Y9Result<Object> refuseClaimRollback(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId);

    /**
     * 重定位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId repositionToTaskId
     * @param userChoice userChoice
     * @param reason reason
     * @param sponsorGuid sponsorGuid
     * @throws Exception exception
     */
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> reposition(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("repositionToTaskId") String repositionToTaskId, @RequestParam("userChoice") List<String> userChoice,
        @RequestParam("reason") String reason, @RequestParam("sponsorGuid") String sponsorGuid) throws Exception;

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @PostMapping("/rollBack")
    Y9Result<Object> rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception exception
     */
    @PostMapping("/rollbackToSender")
    Y9Result<Object> rollbackToSender(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param resson 原因
     * @throws Exception exception
     */
    @PostMapping("/rollbackToStartor")
    Y9Result<Object> rollbackToStartor(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("resson") String resson) throws Exception;

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @PostMapping("/specialComplete")
    Y9Result<Object> specialComplete(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @PostMapping("/takeback")
    Y9Result<Object> takeback(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestParam("reason") String reason) throws Exception;

}
