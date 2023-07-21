package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ReminderApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ReminderApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/reminder")
public interface ReminderApiClient extends ReminderApi {

    /**
     * 删除催办
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    @Override
    @PostMapping(value = "/deleteList", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteList(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 
     * Description: 根据唯一标示查找催办信息
     * 
     * @param tenantId
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findById")
    Map<String, Object> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取当前催办人的在办任务的催办信息
     *
     * @param tenantId
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    public Map<String, Object> findByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取当前催办人的在办任务的催办信息
     *
     * @param tenantId
     * @param senderId
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/findBySenderIdAndProcessInstanceIdAndActive")
    public Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(@RequestParam("tenantId") String tenantId, @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 
     * Description: 获取待办的提醒页面的数据
     * 
     * @param tenantId
     * @param taskId
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/findByTaskId")
    public Map<String, Object> findByTaskId(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("page") int page, @RequestParam("rows") int rows);

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
    public Map<String, Object> getReminder(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId, @RequestParam("type") String type);

    /**
     * 
     * Description: 保存催办信息
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskIds
     * @param msgContent
     * @return
     */
    @Override
    @PostMapping(value = "/saveReminder", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveReminder(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId, @RequestBody String[] taskIds, @RequestParam("msgContent") String msgContent);

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
    public Map<String, Object> sendReminderMessage(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("remType") String remType, @RequestParam("procInstId") String procInstId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle, @RequestParam("taskId") String taskId, @RequestParam("taskAssigneeId") String taskAssigneeId, @RequestParam("msgContent") String msgContent);

    /**
     * 设置为查看状态
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    @Override
    @PostMapping(value = "/setReadTime", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setReadTime(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 更新催办信息
     *
     * @param tenantId 租户滴
     * @param userId 人员id
     * @param id 催办id
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/updateReminder")
    public Map<String, Object> updateReminder(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("id") String id, @RequestParam("msgContent") String msgContent);

}
