package net.risesoft.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risesoft.entity.Reminder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ReminderService {

    /**
     * 根据传进来的id数组删除催办信息
     * 
     * @param ids
     * @param status
     */
    void deleteList(String[] ids);

    /**
     * 根据TaskId查询Reminder
     *
     * @param taskIds
     * @return
     */
    List<Reminder> findAllByTaskId(Collection<String> taskIds);

    /**
     * 根据taskId和催办人Id查找Reminder
     * 
     * @param taskIds
     * @param senderId
     * @return
     */
    List<Reminder> findAllByTaskIdsAndSenderId(Collection<String> taskIds, String senderId);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    Reminder findById(String id);

    /**
     * Description:
     * 
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> findByProcessInstanceId(String processInstanceId, int page, int rows);

    /**
     * Description:
     * 
     * @param senderId
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId, int page,
        int rows);

    /**
     * 根据taskId查找是否存在催办
     *
     * @param taskId
     * @return
     */
    Reminder findByTaskId(String taskId);

    /**
     * Description:
     * 
     * @param taskId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> findByTaskId(String taskId, int page, int rows);

    /**
     * 根据TaskId和催办人查找催办
     *
     * @param taskId
     * @param senderId
     * @return
     */
    Reminder findByTaskIdAndSenderId(String taskId, String senderId);

    /**
     * Description:
     * 
     * @param taskId
     * @param reminderSendType
     * @return
     */
    List<Reminder> findByTastIdAndReminderSendType(String taskId, String reminderSendType);

    /**
     * Description:
     * 
     * @param msgContent
     * @param procInstId
     * @param reminderAutomatic
     * @param remType
     * @param taskId
     * @param taskAssigneeId
     * @param documentTitle
     * @return
     */
    String handleReminder(String msgContent, String procInstId, Integer reminderAutomatic, String remType,
        String taskId, String taskAssigneeId, String documentTitle);

    /**
     * 保存或者编辑催办
     * 
     * @param reminder
     * @return
     */
    Reminder saveOrUpdate(Reminder reminder);

    /**
     * 保存多条催办信息
     *
     * @param list
     */
    void saveReminder(List<Reminder> list);

    /**
     * 保存催办信息
     *
     * @param reminder
     */
    void saveReminder(Reminder reminder);

    /**
     * Description:
     * 
     * @param readTime
     * @param taskId
     * @param type
     */
    void setReadTime(Date readTime, String taskId, String type);

    /**
     * Description:
     * 
     * @param ids
     */
    void setReadTime(String[] ids);
}
