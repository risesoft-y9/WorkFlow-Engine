package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.view.ItemViewConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemViewConfService {

    /**
     * 复制视图配置绑定信息
     *
     * @param itemId 事项id
     * @param newItemId 新事项id
     */
    void copyBindInfo(String itemId, String newItemId);

    /**
     * 复制视图列的数据到指定视图下面
     *
     * @param ids 视图id数组
     * @param viewType 视图分类
     */
    void copyView(String[] ids, String viewType);

    /**
     * 删除视图配置绑定信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据唯一标示查找事项视图配置
     *
     * @param id 视图配置唯一标示
     * @return ItemViewConf
     */
    ItemViewConf findById(String id);

    /**
     * 根据事项id、视图分类和字段名称查询视图绑定信息
     * 
     * @param itemId 事项唯一标示
     * @param viewType 视图分类
     * @param columnName 字段名称
     * @return ItemViewConf
     */
    ItemViewConf findByItemIdAndViewTypeAndColumnName(String itemId, String viewType, String columnName);

    /**
     * 根据事项唯一标示和视图类型
     *
     * @param itemId 事项唯一标示
     * @return List<ItemViewConf>
     */
    List<ItemViewConf> listByItemId(String itemId);

    /**
     * 根据事项唯一标示和视图类型
     *
     * @param itemId 事项唯一标示
     * @param viewType 视图类型
     * @return List<ItemViewConf>
     */
    List<ItemViewConf> listByItemIdAndViewType(String itemId, String viewType);

    /**
     * 
     *
     * @param viewType 视图类型
     * @return List<ItemViewConf>
     */
    List<ItemViewConf> listByViewType(String viewType);

    /**
     * 
     * @param viewType 视图类型
     */
    void removeByViewType(String viewType);

    /**
     * 根据视图配置唯一标示数据删除视图配置
     *
     * @param itemViewConfIds 视图配置唯一标示数组
     */
    void removeItemViewConfs(String[] itemViewConfIds);

    /**
     * 保存视图配置
     *
     * @param itemViewConf 视图配置
     */
    void saveOrUpdate(ItemViewConf itemViewConf);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void update4Order(String[] idAndTabIndexs);
}
