package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 退回，收回，重定向，特殊办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SpecialOperationApi {

    /**
     * 重定向
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/reposition", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> reposition(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("taskId") String taskId, @RequestParam("repositionToTaskId") String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice,
        @RequestParam(value = "reason", required = false) String reason,
        @RequestParam(value = "sponsorGuid", required = false) String sponsorGuid);

    /**
     * 退回
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 退回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/rollBack")
    Y9Result<Object> rollBack(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("taskId") String taskId, @RequestParam(value = "reason", required = false) String reason);

    /**
     * 发回给发送人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/rollbackToSender")
    Y9Result<Object> rollbackToSender(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("taskId") String taskId);

    /**
     * 返回拟稿人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/rollbackToStartor")
    Y9Result<Object> rollbackToStartor(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("taskId") String taskId,
        @RequestParam(value = "reason", required = false) String reason);

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/specialComplete")
    Y9Result<Object> specialComplete(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("taskId") String taskId,
        @RequestParam(value = "reason", required = false) String reason);

    /**
     * 收回
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 收回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/takeBack")
    Y9Result<Object> takeBack(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("taskId") String taskId, @RequestParam(value = "reason", required = false) String reason);

}
