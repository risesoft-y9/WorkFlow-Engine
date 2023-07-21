package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.EntrustHistoryModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface EntrustHistoryApi {

    /**
     * 获取某个用户的某个事项委托历史对象集合
     * 
     * @param tenantId 租户id
     * @param userId 人员滴
     * @param ownerId 委托人id
     * @param itemId 事项粒度
     * @return List&lt;EntrustHistoryModel&gt;
     */
    public List<EntrustHistoryModel> findByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId);

    /**
     * 获取某个用户的委托历史对象集合
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @return List&lt;EntrustHistoryModel&gt;
     */
    public List<EntrustHistoryModel> findOneByOwnerId(String tenantId, String userId, String ownerId);
}
