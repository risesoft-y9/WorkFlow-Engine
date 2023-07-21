package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemOpinionFrameRoleApi {

    /**
     * 获取意见框绑定角色
     *
     * @param tenantId 租户id
     * @param itemOpinionFrameId 意见框绑定id
     * @return List<ItemOpinionFrameRoleModel>
     */
    public List<ItemOpinionFrameRoleModel> findByItemOpinionFrameId(String tenantId, String itemOpinionFrameId);
}
