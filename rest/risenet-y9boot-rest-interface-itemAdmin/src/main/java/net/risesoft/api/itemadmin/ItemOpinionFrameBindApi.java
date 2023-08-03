package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemOpinionFrameBindApi {

    /**
     * 根据事项id获取所有绑定意见框
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return List&lt;OpinionFrameTaskRoleBindModel&gt;
     */
    public List<ItemOpinionFrameBindModel> findByItemId(String tenantId, String itemId);

    /**
     * 根据事项id和任务id获取绑定意见框
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String tenantId,
        String userId, String itemId, String processDefinitionId, String taskDefKey);

    /**
     * 根据事项id和任务id获取绑定意见框（包含角色信息）
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(String tenantId,
        String userId, String itemId, String processDefinitionId, String taskDefKey);
}
