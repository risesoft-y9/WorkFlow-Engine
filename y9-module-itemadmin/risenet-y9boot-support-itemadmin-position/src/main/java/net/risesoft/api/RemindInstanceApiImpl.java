package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 根据流程实例id获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<RemindInstanceModel>>
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstance(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list = remindInstanceService.findRemindInstance(processInstanceId);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务ley
     * @return Y9Result<List<RemindInstanceModel>>
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String tenantId,
        String processInstanceId, String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndArriveTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务ley
     * @return Y9Result<List<RemindInstanceModel>>
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String tenantId,
        String processInstanceId, String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(processInstanceId, taskKey);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和提醒类型获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param remindType 提醒类型
     * @return {@code Y9Result<List<RemindInstanceModel>>}
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndRemindType(String tenantId,
        String processInstanceId, String remindType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndRemindType(processInstanceId, remindType);
        List<RemindInstanceModel> newList = new ArrayList<>();
        for (RemindInstance remindInstance : list) {
            RemindInstanceModel remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
            newList.add(remindInstanceModel);
        }
        return Y9Result.success(newList);
    }

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<List<RemindInstanceModel>>}
     */
    @Override
    public Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndTaskId(String tenantId,
        String processInstanceId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<RemindInstance> list =
            remindInstanceService.findRemindInstanceByProcessInstanceIdAndTaskId(processInstanceId, taskId);
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
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<RemindInstanceModel>}
     */
    @Override
    public Y9Result<RemindInstanceModel> getRemindInstance(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(userId);
        RemindInstance remindInstance = remindInstanceService.getRemindInstance(processInstanceId);
        RemindInstanceModel remindInstanceModel = null;
        if (remindInstance != null) {
            remindInstanceModel = new RemindInstanceModel();
            Y9BeanUtil.copyProperties(remindInstance, remindInstanceModel);
        }
        return Y9Result.success(remindInstanceModel);
    }

    /**
     * 保存消息提醒
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param taskIds 任务ids
     * @param process 是否流程办结提醒
     * @param arriveTaskKey 节点到达任务
     * @param completeTaskKey 节点完成任务
     * @return {@code Y9Result<String>}
     */
    @Override
    public Y9Result<String> saveRemindInstance(String tenantId, String userId, String processInstanceId, String taskIds,
        Boolean process, String arriveTaskKey, String completeTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(userId);
        return remindInstanceService.saveRemindInstance(processInstanceId, taskIds, process, arriveTaskKey,
            completeTaskKey);
    }

}
