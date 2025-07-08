package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.button.SendButton;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SendButtonService {

    /**
     * 新增的时候，判断传进来的customId是否已经存在
     *
     * @param customId
     * @return
     */
    boolean checkCustomId(String customId);

    /**
     * 根据唯一标示查找
     *
     * @param id
     * @return
     */
    SendButton getById(String id);

    /**
     * 查找所有
     *
     * @return
     */
    List<SendButton> listAll();

    /**
     * Description: 根据传进来的Id集合删除
     *
     * @param sendButtonIds
     */
    void removeSendButtons(String[] sendButtonIds);

    /**
     * Description: 保存或者更新
     *
     * @param sendButton
     * @return
     */
    SendButton saveOrUpdate(SendButton sendButton);
}
