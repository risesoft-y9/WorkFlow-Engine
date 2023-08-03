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
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    List<RemindInstanceModel> findRemindInstance(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     * 
     * @param tenantId
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String tenantId,
        String processInstanceId, String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     * 
     * @param tenantId
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String tenantId,
        String processInstanceId, String taskKey);

    /**
     * 
     * Description: 根据流程实例id和提醒类型获取消息提醒设置
     * 
     * @param tenantId
     * @param processInstanceId
     * @param remindType
     * @return
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndRemindType(String tenantId,
        String processInstanceId, String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     * 
     * @param tenantId
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndTaskId(String tenantId, String processInstanceId,
        String taskId);

    /**
     * 根据流程实例id获取个人消息提醒设置
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    RemindInstanceModel getRemindInstance(String tenantId, String userId, String processInstanceId);

    /**
     * 保存消息提醒
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskIds
     * @param process
     * @param arriveTaskKey
     * @param completeTaskKey
     * @return
     */
    Map<String, Object> saveRemindInstance(String tenantId, String userId, String processInstanceId, String taskIds,
        Boolean process, String arriveTaskKey, String completeTaskKey);

}
