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
     * Description:
     *
     * @param itemButtonId
     */
    void deleteByItemButtonId(String itemButtonId);

    /**
     * Description:
     *
     * @param itemButtonId
     * @return
     */
    List<ItemButtonRole> listByItemButtonId(String itemButtonId);

    /**
     * Description:
     *
     * @param itemButtonId
     * @return
     */
    List<ItemButtonRole> listByItemButtonIdContainRoleName(String itemButtonId);

    /**
     * Description: 删除多个
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * Description:
     *
     * @param itemButtonId
     * @param roleId
     */
    void saveOrUpdate(String itemButtonId, String roleId);
}
