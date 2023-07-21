package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.model.itemadmin.RemindInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "RemindInstanceApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/remindInstance")
public interface RemindInstanceApiClient extends RemindInstanceApi {

    /**
     * 根据流程实例id获取消息提醒设置
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findRemindInstance")
    List<RemindInstanceModel> findRemindInstance(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    @Override
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndArriveTaskKey")
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndArriveTaskKey(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskKey") String taskKey);

    /**
     * 根据流程实例id和任务key获取消息提醒设置
     *
     * @param tenantId
     * @param processInstanceId
     * @param taskKey
     * @return
     */
    @Override
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndCompleteTaskKey")
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndCompleteTaskKey(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskKey") String taskKey);

    /**
     * 
     * Description: 根据流程实例id和提醒类型获取消息提醒设置
     * 
     * @param tenantId
     * @param processInstanceId
     * @param remindType
     * @return
     */
    @Override
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndRemindType")
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndRemindType(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("remindType") String remindType);

    /**
     * 根据流程实例id和任务id获取消息提醒设置
     *
     * @param tenantId
     * @param processInstanceId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/findRemindInstanceByProcessInstanceIdAndTaskId")
    List<RemindInstanceModel> findRemindInstanceByProcessInstanceIdAndTaskId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId);

    /**
     * 根据流程实例id获取个人消息提醒设置
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/getRemindInstance")
    RemindInstanceModel getRemindInstance(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

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
    @Override
    @PostMapping("/saveRemindInstance")
    Map<String, Object> saveRemindInstance(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskIds") String taskIds, @RequestParam("process") Boolean process,
        @RequestParam("arriveTaskKey") String arriveTaskKey, @RequestParam("completeTaskKey") String completeTaskKey);

}
