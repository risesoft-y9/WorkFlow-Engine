package net.risesoft.service;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
public interface QuickSendService {

    /**
     * 获取快捷发送人
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return
     */
    String getAssignee(String itemId, String taskKey);

    /**
     * 保存快捷发送人
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     */
    void saveOrUpdate(String itemId, String taskKey, String assignee);

}
