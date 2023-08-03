package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ReminderApi {

    /**
     * 删除催办
     * 
     * @param tenantId 租户id
     * @param ids ids
     */
    public void deleteList(String tenantId, String[] ids);

    /**
     * 
     * Description: 查找催办
     * 
     * @param tenantId
     * @param id
     * @return
     */
    Map<String, Object> findById(String tenantId, String id);

    /**
     * 获取当前催办人的在办任务的催办信息
     * 
     * @param tenantId
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> findByProcessInstanceId(String tenantId, String processInstanceId, int page, int rows);

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
    public Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(String tenantId, String senderId,
        String processInstanceId, int page, int rows);

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
    public Map<String, Object> findByTaskId(String tenantId, String taskId, int page, int rows);

    /**
     * 查看催办信息
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param type 类型，todo（待办），doing（在办），done（办结）
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> getReminder(String tenantId, String userId, String taskId, String type);

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
    public Map<String, Object> saveReminder(String tenantId, String userId, String processInstanceId, String[] taskIds,
        String msgContent);

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
    public Map<String, Object> sendReminderMessage(String tenantId, String userId, String remType, String procInstId,
        String processInstanceId, String documentTitle, String taskId, String taskAssigneeId, String msgContent);

    /**
     * 设置为查看状态
     * 
     * @param tenantId 租户id
     * @param ids ids
     */
    public void setReadTime(String tenantId, String[] ids);

    /**
     * 更新催办信息
     * 
     * @param tenantId 租户滴
     * @param userId 人员id
     * @param id 催办id
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> updateReminder(String tenantId, String userId, String id, String msgContent);

}
