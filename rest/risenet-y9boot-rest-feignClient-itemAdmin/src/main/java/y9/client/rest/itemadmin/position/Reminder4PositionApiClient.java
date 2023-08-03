package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.Reminder4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Reminder4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/reminder4Position")
public interface Reminder4PositionApiClient extends Reminder4PositionApi {

    /**
     * 查看催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param type 类型，todo（待办），doing（在办），done（办结）
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getReminder")
    public Map<String, Object> getReminder(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId,
        @RequestParam("type") String type);

    /**
     * 获取待办的提醒页面的数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getReminderList")
    public Map<String, Object> getReminderList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     * 保存催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param reminderId 催办id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/saveReminder")
    public Map<String, Object> saveReminder(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("reminderId") String reminderId,
        @RequestParam("taskIds") String taskIds, @RequestParam("msgContent") String msgContent);

    /**
     * 发送催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param remType 催办类型，"1":短信,"2":邮件",3":站内信",4":待办列表中
     * @param procInstId procInstId
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @param taskId 任务id
     * @param taskAssigneeId taskAssigneeId
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/sendReminderMessage")
    public Map<String, Object> sendReminderMessage(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("remType") String remType,
        @RequestParam("procInstId") String procInstId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle, @RequestParam("taskId") String taskId,
        @RequestParam("taskAssigneeId") String taskAssigneeId, @RequestParam("msgContent") String msgContent);

    /**
     * 更新催办信息
     *
     * @param tenantId 租户滴
     * @param userId 人员id
     * @param reminderId 催办id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/updateReminder")
    public Map<String, Object> updateReminder(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("reminderId") String reminderId,
        @RequestParam("taskIds") String taskIds, @RequestParam("msgContent") String msgContent);

}
