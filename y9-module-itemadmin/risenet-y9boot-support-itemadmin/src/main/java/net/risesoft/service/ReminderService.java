package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.Reminder;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ReminderService {

    /**
     * 根据传进来的id数组删除催办信息
     *
     * @param ids
     */
    void deleteList(String[] ids);

    /**
     * 
     *
     * @param id
     * @return
     */
    Reminder findById(String id);

    /**
     * 
     *
     * @param msgContent
     * @param procInstId
     * @param reminderAutomatic
     * @param remType
     * @param taskId
     * @param documentTitle
     * @return
     */
    String handleReminder(String msgContent, String procInstId, Integer reminderAutomatic, String remType,
        String taskId, String documentTitle);

    /**
     * 
     *
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<ReminderModel> pageByProcessInstanceId(String processInstanceId, int page, int rows);

    /**
     * 
     *
     * @param senderId
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<ReminderModel> pageBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId,
        int page, int rows);

    /**
     * 
     *
     * @param taskId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<ReminderModel> pageByTaskId(String taskId, int page, int rows);

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
     * 
     *
     * @param ids
     */
    void setReadTime(String[] ids);
}
