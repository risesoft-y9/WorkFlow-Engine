package net.risesoft.service.extend;

import net.risesoft.model.itemadmin.ItemMsgRemindModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemMsgRemindService {

    /**
     * 删除消息提醒信息
     * 
     * @param processInstanceId 流程实例ID
     */
    void deleteMsgRemindInfo(String processInstanceId);

    /**
     * 获取消息提醒配置
     * 
     * @param userId 用户ID
     * @param type 类型
     * @return
     */
    String getRemindConfig(String userId, String type);

    /**
     * 保存消息提醒信息
     * 
     * @param info 待保存的消息提醒信息
     * @return
     */
    Boolean saveMsgRemindInfo(ItemMsgRemindModel info);
}
