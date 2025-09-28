package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.organword.ItemOrganWordRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemOrganWordRoleService {

    /**
     * 根据唯一标示删除
     *
     * @param id 唯一标识
     */
    void deleteById(String id);

    /**
     * 根据事项和编号框绑定的唯一标示查找绑定的角色
     *
     * @param itemOrganWordBindId 绑定唯一标识
     * @return List<ItemOrganWordRole>
     */
    List<ItemOrganWordRole> listByItemOrganWordBindId(String itemOrganWordBindId);

    /**
     * 根据事项和编号框绑定的唯一标示查找绑定的角色（包含角色名称）
     *
     * @param itemOrganWordBindId 绑定唯一标识
     * @return List<ItemOrganWordRole>
     */
    List<ItemOrganWordRole> listByItemOrganWordBindIdContainRoleName(String itemOrganWordBindId);

    /**
     * 删除多个
     *
     * @param ids 绑定唯一标识
     */
    void remove(String[] ids);

    /**
     * 删除绑定对应的角色
     *
     * @param itemOrganWordBindId 绑定唯一标识
     */
    void removeByItemOrganWordBindId(String itemOrganWordBindId);

    /**
     * 保存或者更新
     *
     * @param itemOrganWordBindId 绑定唯一标识
     * @param roleId 角色唯一标识
     * @return ItemOrganWordRole
     */
    ItemOrganWordRole saveOrUpdate(String itemOrganWordBindId, String roleId);
}
