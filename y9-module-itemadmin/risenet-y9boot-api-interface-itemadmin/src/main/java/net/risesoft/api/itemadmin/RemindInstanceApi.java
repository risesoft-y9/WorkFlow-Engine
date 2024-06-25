package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * 消息提醒
 *
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
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     */
    @GetMapping("/findRemindInstance")
    Y9Result<List<RemindInstanceModel>> findRemindInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey 任务key
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     */
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndArriveTaskKey")
    Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskKey") String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskKey taskKey
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     */
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndCompleteTaskKey")
    Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskKey") String taskKey);

    /**
     *
     * Description: 根据流程实例id和提醒类型获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param remindType remindType
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     */
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndRemindType")
    Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndRemindType(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("remindType") String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<List<RemindInstanceModel>>} 通用请求返回对象 - data 是消息提醒列表
     */
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndTaskId")
    Y9Result<List<RemindInstanceModel>> findRemindInstanceByProcessInstanceIdAndTaskId(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据流程实例id获取个人消息提醒设置
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<RemindInstanceModel>} 通用请求返回对象 - data 是消息提醒对象
     */
    @GetMapping("/getRemindInstance")
    Y9Result<RemindInstanceModel> getRemindInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/saveRemindInstance")
    Y9Result<String> saveRemindInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskIds") String taskIds, @RequestParam("process") Boolean process,
        @RequestParam("arriveTaskKey") String arriveTaskKey, @RequestParam("completeTaskKey") String completeTaskKey);

}
