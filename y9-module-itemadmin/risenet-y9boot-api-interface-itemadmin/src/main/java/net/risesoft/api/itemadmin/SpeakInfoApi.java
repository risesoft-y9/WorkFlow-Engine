package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.SpeakInfoModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SpeakInfoApi {

    /**
     * 逻辑删除发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> deleteById(String tenantId, String userId, String id);

    /**
     * 根据唯一标示超找发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return SpeakInfoModel
     */
    SpeakInfoModel findById(String tenantId, String userId, String id);

    /**
     * 根据流程实例查找某一个流程的所有发言信息，根据时间倒叙排列
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return List&lt;SpeakInfoModel&gt;
     */
    List<SpeakInfoModel> findByProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 获取未读消息计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return int
     */
    int getNotReadCount(String tenantId, String userId, String processInstanceId);

    /**
     * 保存或者更新发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param speakInfoModel speakInfoModel
     * @return String
     */
    String saveOrUpdate(String tenantId, String userId, SpeakInfoModel speakInfoModel);
}
