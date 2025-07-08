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
     * @param id
     */
    void deleteById(String id);

    /**
     * Description: 根据事项和编号框绑定的唯一标示查找绑定的角色
     *
     * @param itemOrganWordBindId
     * @return
     */
    List<ItemOrganWordRole> listByItemOrganWordBindId(String itemOrganWordBindId);

    /**
     * Description: 根据事项和编号框绑定的唯一标示查找绑定的角色（包含角色名称）
     *
     * @param itemOrganWordBindId
     * @return
     */
    List<ItemOrganWordRole> listByItemOrganWordBindIdContainRoleName(String itemOrganWordBindId);

    /**
     * Description: 删除多个
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 删除绑定对应的角色
     *
     * @param itemOrganWordBindId
     */
    void removeByItemOrganWordBindId(String itemOrganWordBindId);

    /**
     * Description: 保存或者更新
     *
     * @param itemOrganWordBindId
     * @param roleId
     * @return
     */
    ItemOrganWordRole saveOrUpdate(String itemOrganWordBindId, String roleId);
}
