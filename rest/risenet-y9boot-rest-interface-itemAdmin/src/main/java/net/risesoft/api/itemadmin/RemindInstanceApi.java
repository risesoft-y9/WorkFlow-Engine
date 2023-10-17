package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.RemindInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface RemindInstanceApi {

    /**
     * 根据流程实例id获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;RemindInstanceModel&gt;
     */
    List<RemindInstanceModel> findRemindInstance(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务key
     * @return List&lt;RemindInstanceModel&gt;
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String tenantId, String processInstanceId, String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey taskKey
     * @return List&lt;RemindInstanceModel&gt;
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String tenantId, String processInstanceId, String taskKey);

    /**
     *
     * Description: 根据流程实例id和提醒类型获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param remindType remindType
     * @return List&lt;RemindInstanceModel&gt;
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndRemindType(String tenantId, String processInstanceId, String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return List&lt;RemindInstanceModel&gt;
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndTaskId(String tenantId, String processInstanceId, String taskId);

    /**
     * 根据流程实例id获取个人消息提醒设置
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return RemindInstanceModel
     */
    RemindInstanceModel getRemindInstance(String tenantId, String userId, String processInstanceId);

    /**
     * 保存消息提醒
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param taskIds 任务ids
     * @param process process
     * @param arriveTaskKey arriveTaskKey
     * @param completeTaskKey completeTaskKey
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveRemindInstance(String tenantId, String userId, String processInstanceId, String taskIds, Boolean process, String arriveTaskKey, String completeTaskKey);

}
