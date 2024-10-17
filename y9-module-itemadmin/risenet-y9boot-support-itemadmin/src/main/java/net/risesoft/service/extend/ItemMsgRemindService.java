package net.risesoft.service.extend;

import net.risesoft.model.itemadmin.ItemMsgRemindModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemMsgRemindService {

    void deleteMsgRemindInfo(String processInstanceId);

    String getRemindConfig(String userId, String type);

    Boolean saveMsgRemindInfo(ItemMsgRemindModel info);
}
