package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.RemindInstance;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface RemindInstanceService {

    /**
     * 根据流程实例id获取个人消息提醒设置
     *
     * @param processInstanceId
     * @return
     */
    RemindInstance getRemindInstance(String processInstanceId);

    /**
     * 根据流程实例id获取消息提醒设置
     *
     * @param processInstanceId
     * @return
     */
    List<RemindInstance> listByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstance> listByProcessInstanceIdAndArriveTaskKey(String processInstanceId, String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    List<RemindInstance> listByProcessInstanceIdAndCompleteTaskKey(String processInstanceId, String taskKey);

    /**
     * 根据流程实例id和提醒类型获取消息提醒设置
     *
     * @param processInstanceId
     * @param remindType
     * @return
     */
    List<RemindInstance> listByProcessInstanceIdAndRemindType(String processInstanceId, String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<RemindInstance> listByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

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
    Y9Result<String> saveRemindInstance(String processInstanceId, String taskIds, Boolean process, String arriveTaskKey,
        String completeTaskKey);

}
