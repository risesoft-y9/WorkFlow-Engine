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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/addMultiInstanceExecution")
    Y9Result<Object> addMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId,
        @RequestParam("taskId") String taskId, @RequestParam("elementUser") String elementUser);

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId executionId
     * @param taskId 任务id
     * @param elementUser elementUser
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/deleteMultiInstanceExecution")
    Y9Result<Object> deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId, @RequestParam("taskId") String taskId,
        @RequestParam("elementUser") String elementUser);

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例ID
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/directSend")
    Y9Result<Object> directSend(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/refuseClaimRollback")
    Y9Result<Object> refuseClaimRollback(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId);

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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> reposition(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("repositionToTaskId") String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, @RequestParam("reason") String reason,
        @RequestParam("sponsorGuid") String sponsorGuid);

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/rollBack")
    Y9Result<Object> rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason);

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/rollbackToSender")
    Y9Result<Object> rollbackToSender(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId);

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param resson 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/rollbackToStartor")
    Y9Result<Object> rollbackToStartor(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("resson") String resson);

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/specialComplete")
    Y9Result<Object> specialComplete(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/takeback")
    Y9Result<Object> takeback(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId, @RequestParam("reason") String reason);

}
