package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SpeakInfo;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SpeakInfoService {

    /**
     * Description: 逻辑删除发言信息
     *
     * @param id
     * @return
     */
    Y9Result<Object> deleteById(String id);

    /**
     * 根据唯一标示超找发言信息
     *
     * @param id
     * @return
     */
    SpeakInfo findById(String id);

    /**
     * 根据流程实例查找某一个流程的所有发言信息，根据时间倒叙排列
     *
     * @param processInstanceId
     * @return
     */
    List<SpeakInfo> findByProcessInstanceId(String processInstanceId);

    /**
     * 获取未读消息计数
     *
     * @param processInstanceId
     * @param userId
     * @return
     */
    int getNotReadCount(String processInstanceId, String userId);

    /**
     * 保存或者更新发言信息
     *
     * @param speakInfo
     * @return
     */
    String saveOrUpdate(SpeakInfo speakInfo);
}
