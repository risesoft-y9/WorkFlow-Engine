package net.risesoft.service.core;

import net.risesoft.entity.ItemExtendProps;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemExtendPropsService {

    void deleteByItemId(String s);

    /**
     * 根据事项id查询事项扩展属性
     *
     * @param itemId 事项id
     * @return
     */
    ItemExtendProps findByItemId(String itemId);

    /**
     * 保存事项扩展属性
     *
     * @param item
     */
    void saveExtendProps(ItemExtendProps item);
}
