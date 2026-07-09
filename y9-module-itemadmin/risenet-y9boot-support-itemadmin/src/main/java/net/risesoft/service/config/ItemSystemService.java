package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemSystem;

/**
 * @author qinman
 * @date 2026/07/09
 */
public interface ItemSystemService {

    /**
     * 根据id删除系统
     *
     * @param id 主键
     */
    void deleteById(String id);

    /**
     * 根据id获取系统
     *
     * @param id 主键
     * @return ItemSystem
     */
    ItemSystem findById(String id);

    /**
     * 获取所有系统列表（按排序号升序）
     *
     * @return List<ItemSystem>
     */
    List<ItemSystem> listAll();

    /**
     * 保存或更新系统
     *
     * @param entity 系统实体
     * @return ItemSystem
     */
    ItemSystem save(ItemSystem entity);

    /**
     * 保存排序
     *
     * @param idAndTabIndexs id和排序号数组，格式为 "id:tabIndex"
     */
    void saveOrder(List<String> ids);

    /**
     * 校验系统英文名称是否已存在
     *
     * @param name 系统英文名称
     * @param id   当前记录id（编辑时传入，排除自身）
     * @return true=已存在, false=不存在
     */
    boolean isNameExist(String name, String id);

    /**
     * 校验系统中文名称是否已存在
     *
     * @param cnName 系统中文名称
     * @param id     当前记录id（编辑时传入，排除自身）
     * @return true=已存在, false=不存在
     */
    boolean isCnNameExist(String cnName, String id);
}
