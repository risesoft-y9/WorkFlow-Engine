package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemViewConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemViewConfService {

    /**
     * 复制视图配置绑定信息
     *
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId, String newItemId);

    /**
     * 复制视图列的数据到指定视图下面
     *
     * @param ids
     * @param viewType
     */
    void copyView(String[] ids, String viewType);

    /**
     * 删除视图配置绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据唯一标示查找事项视图配置
     *
     * @param id
     * @return
     */
    ItemViewConf findById(String id);

    /**
     * 根据事项唯一标示和视图类型
     *
     * @param itemId
     * @return
     */
    List<ItemViewConf> listByItemId(String itemId);

    /**
     * 根据事项唯一标示和视图类型
     *
     * @param itemId
     * @param viewType
     * @return
     */
    List<ItemViewConf> listByItemIdAndViewType(String itemId, String viewType);

    /**
     * Description:
     *
     * @param viewType
     * @return
     */
    List<ItemViewConf> listByViewType(String viewType);

    /**
     * Description:
     *
     * @param viewType
     */
    void removeByViewType(String viewType);

    /**
     * 根据视图配置唯一标示数据删除视图配置
     *
     * @param itemViewConfIds
     */
    void removeItemViewConfs(String[] itemViewConfIds);

    /**
     * 保存视图配置
     *
     * @param itemViewConf
     */
    void saveOrUpdate(ItemViewConf itemViewConf);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void update4Order(String[] idAndTabIndexs);
}
