package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ItemViewConfModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemViewConfApi {

    /**
     * 获取事项视图配置列表
     * 
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return
     */
    List<ItemViewConfModel> findByItemIdAndViewType(String tenantId, String itemId, String viewType);
}
