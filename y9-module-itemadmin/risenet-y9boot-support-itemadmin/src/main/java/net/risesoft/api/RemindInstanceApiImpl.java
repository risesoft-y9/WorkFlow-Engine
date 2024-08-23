package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.entity.RemindInstance;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.RemindInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 消息提醒接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/remindInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class RemindInstanceApiImpl implements RemindInstanceApi {

    private final RemindInstanceService remindInstanceService;

    /**
     * 根据流程实例id获取消息提醒设置列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstance(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list = remindInstanceService.listByProcessInstanceId(processInstanceId);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务key获取任务到达的消息提醒设置列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务key
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是任务到达的消息提醒列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(
        @RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.listByProcessInstanceIdAndArriveTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务key获取任务完成的消息提醒设置列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务key
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是任务完成的消息提醒列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(
        @RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.listByProcessInstanceIdAndCompleteTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和提醒类型获取消息提醒设置列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param remindType 提醒类型
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndRemindType(
        @RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String remindType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.listByProcessInstanceIdAndRemindType(processInstanceId, remindType);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务id获取消息提醒设置列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndTaskId(
        @RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list = remindInstanceService.listByProcessInstanceIdAndTaskId(processInstanceId, taskId);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id获取个人消息提醒设置
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<RemindInstanceModel>} 通用请求返回对象 - data 是消息提醒对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<RemindInstanceModel> getRemindInstance(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(userId);
        RemindInstance remindInstance = remindInstanceService.getRemindInstance(processInstanceId);
        RemindInstanceModel remindInstanceModel = null;
        if (remindInstance != null) {
            remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
        }
        return Y9Result.success(remindInstanceModel);
    }

    /**
     * 保存消息提醒设置
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param taskIds 任务ids
     * @param process 是否流程办结提醒
     * @param arriveTaskKey 节点到达任务
     * @param completeTaskKey 节点完成任务
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveRemindInstance(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId, @RequestParam String taskIds, @RequestParam Boolean process,
        @RequestParam String arriveTaskKey, @RequestParam String completeTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(userId);
        return remindInstanceService.saveRemindInstance(processInstanceId, taskIds, process, arriveTaskKey,
            completeTaskKey);
    }

}
