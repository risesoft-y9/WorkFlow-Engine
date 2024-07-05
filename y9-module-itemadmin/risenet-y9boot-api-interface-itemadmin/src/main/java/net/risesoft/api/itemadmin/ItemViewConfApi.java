package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项视图配置
 *
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
     * @return {@code Y9Result<List<ItemViewConfModel>>} 通用请求返回对象 -data是事项视图配置列表
     * @since 9.6.6
     */
    @GetMapping("/findByItemIdAndViewType")
    Y9Result<List<ItemViewConfModel>> findByItemIdAndViewType(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("viewType") String viewType);
}
