package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 催办
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ReminderApi {

    /**
     * 删除催办
     *
     * @param ids 催办ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteList")
    Y9Result<Object> deleteList(@RequestBody String[] ids);

    /**
     *
     * 根据id获取催办
     *
     * @param id 催办id
     * @return {@code Y9Result<ReminderModel>} 通用请求返回对象 - rows 是待办的催办信息
     * @since 9.6.6
     */
    @GetMapping("/findById")
    Y9Result<ReminderModel> findById(@RequestParam("id") String id);

    /**
     * 获取流程实例的催办信息
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是催办信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessInstanceId")
    Y9Page<ReminderModel> findByProcessInstanceId(@RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * 获取当前催办人的在办任务的催办信息
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是催办信息
     * @since 9.6.6
     */
    @GetMapping("/findBySenderIdAndProcessInstanceIdAndActive")
    Y9Page<ReminderModel> findBySenderIdAndProcessInstanceIdAndActive(
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 获取待办的提醒数据
     *
     * @param taskId 任务id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是待办的催办信息
     * @since 9.6.6
     */
    @GetMapping("/findByTaskId")
    Y9Page<ReminderModel> findByTaskId(@RequestParam("taskId") String taskId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 保存催办信息
     *
     * @param processInstanceId 流程实例id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveReminder", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveReminder(@RequestParam("processInstanceId") String processInstanceId,
        @RequestBody String[] taskIds, @RequestParam("msgContent") String msgContent);

    /**
     * 发送催办信息
     *
     * @param remType 催办类型，"1":短信,"2":邮件",3":站内信",4":待办列表中
     * @param procInstId procInstId
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @param taskId 任务id
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/sendReminderMessage")
    Y9Result<String> sendReminderMessage(@RequestParam("remType") String remType,
        @RequestParam("procInstId") String procInstId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle, @RequestParam("taskId") String taskId,
        @RequestParam("msgContent") String msgContent);

    /**
     * 设置为查看状态
     *
     * @param ids 催办ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/setReadTime", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setReadTime(@RequestBody String[] ids);

    /**
     * 更新催办信息
     *
     * @param id 催办id
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateReminder")
    Y9Result<String> updateReminder(@RequestParam("id") String id, @RequestParam("msgContent") String msgContent);

}
