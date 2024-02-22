package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.RemindInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface RemindInstanceService {

    /**
     * 根据流程实例id获取消息提醒设置
     * 
     * @param processInstanceId
     * @return
     */
    List<RemindInstance> findRemindInstance(String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     * 
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstance> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(String processInstanceId,
        String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     * 
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstance> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(String processInstanceId,
        String taskKey);

    /**
     * 根据流程实例id和提醒类型获取消息提醒设置
     * 
     * @param processInstanceId
     * @param remindType
     * @return
     */
    List<RemindInstance> findRemindInstanceByProcessInstanceIdAndRemindType(String processInstanceId,
        String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     * 
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<RemindInstance> findRemindInstanceByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

    /**
     * 根据流程实例id获取个人消息提醒设置
     * 
     * @param processInstanceId
     * @return
     */
    RemindInstance getRemindInstance(String processInstanceId);

    /**
     * 保存流程实例消息提醒
     * 
     * @param processInstanceId
     * @param taskIds
     * @param process
     * @param arriveTaskKey
     * @param completeTaskKey
     * @return
     */
    Map<String, Object> saveRemindInstance(String processInstanceId, String taskIds, Boolean process,
        String arriveTaskKey, String completeTaskKey);

}
