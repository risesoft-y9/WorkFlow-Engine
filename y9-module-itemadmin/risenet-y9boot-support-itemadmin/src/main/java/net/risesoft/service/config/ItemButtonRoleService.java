package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.button.ItemButtonRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemButtonRoleService {

    /**
     * 
     * @param itemButtonId 绑定唯一标识
     */
    void deleteByItemButtonId(String itemButtonId);

    /**
     * 
     *
     * @param itemButtonId 绑定唯一标识
     * @return List<ItemButtonRole>
     */
    List<ItemButtonRole> listByItemButtonId(String itemButtonId);

    /**
     * 
     *
     * @param itemButtonId 绑定唯一标识
     * @return List<ItemButtonRole>
     */
    List<ItemButtonRole> listByItemButtonIdContainRoleName(String itemButtonId);

    /**
     * 删除多个
     *
     * @param ids 唯一标识集合
     */
    void remove(String[] ids);

    /**
     * 
     *
     * @param itemButtonId 绑定唯一标识
     * @param roleId 角色唯一标识
     */
    void saveOrUpdate(String itemButtonId, String roleId);
}
