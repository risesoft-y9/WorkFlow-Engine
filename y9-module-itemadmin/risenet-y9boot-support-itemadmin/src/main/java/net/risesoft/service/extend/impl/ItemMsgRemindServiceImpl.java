package net.risesoft.service.extend.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.itemadmin.ItemMsgRemindModel;
import net.risesoft.service.extend.ItemMsgRemindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemMsgRemindServiceImpl implements ItemMsgRemindService {

    @Override
    public void deleteMsgRemindInfo(String processInstanceId) {

    }

    @Override
    public String getRemindConfig(String userId, String type) {
        return "";
    }

    @Override
    public Boolean saveMsgRemindInfo(ItemMsgRemindModel info) {
        return true;
    }
}
