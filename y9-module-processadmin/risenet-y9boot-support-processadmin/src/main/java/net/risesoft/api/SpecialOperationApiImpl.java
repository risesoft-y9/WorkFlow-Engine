package net.risesoft.api;

import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OperationService;

/**
 * 退回，收回，重定向，特殊办结接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/specialOperation", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpecialOperationApiImpl implements SpecialOperationApi {

    private final OperationService operationService;

    /**
     * 重定向
     *
     * @param taskId 任务id
     * @param repositionToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 重定向原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> reposition(@RequestParam String taskId, @RequestParam String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        operationService.reposition(taskId, repositionToTaskId, userChoice, reason, sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 退回
     *
     * @param taskId 任务id
     * @param reason 退回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollBack(@RequestParam String taskId, String reason) {
        operationService.rollBack(taskId, reason);
        return Y9Result.success();
    }

    /**
     * 退回至流转过的节点
     *
     * @param taskId 任务id
     * @param routeToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 退回原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> rollBack2History(@RequestParam String taskId, @RequestParam String routeToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        operationService.rollBack2History(taskId, routeToTaskId, userChoice, reason, sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 发回给发送人
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToSender(@RequestParam String taskId) {
        operationService.rollbackToSender(taskId);
        return Y9Result.success();
    }

    /**
     * 返回拟稿人
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToStartor(@RequestParam String taskId, String reason) {
        operationService.rollbackToStartor(taskId, reason);
        return Y9Result.success();
    }

    /**
     * 特殊办结(仅适用主流程，不适用SubProcess)
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> specialComplete(@RequestParam String taskId, String reason) {
        operationService.specialComplete(taskId, reason);
        return Y9Result.success();
    }

    /**
     * 收回
     *
     * @param taskId 任务id
     * @param reason 收回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> takeBack(@RequestParam String taskId, String reason) {
        operationService.takeBack(taskId, reason);
        return Y9Result.success();
    }

    /**
     * 收回
     *
     * @param taskId 任务id
     * @param reason 收回的原因
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> takeBack2TaskDefKey(@RequestParam String taskId, @RequestParam String taskDefKey,
        String reason) {
        operationService.takeBack2TaskDefKey(taskId, taskDefKey, reason);
        return Y9Result.success();
    }
}
