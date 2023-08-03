package net.risesoft.api.itemadmin.position;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Reminder4PositionApi {

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
     * 获取待办的提醒页面的数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> getReminderList(String tenantId, String userId, String taskId);

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
    public Map<String, Object> saveReminder(String tenantId, String userId, String reminderId, String taskIds,
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
     * 更新催办信息
     *
     * @param tenantId 租户滴
     * @param userId 人员id
     * @param reminderId 催办id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> updateReminder(String tenantId, String userId, String reminderId, String taskIds,
        String msgContent);

}
