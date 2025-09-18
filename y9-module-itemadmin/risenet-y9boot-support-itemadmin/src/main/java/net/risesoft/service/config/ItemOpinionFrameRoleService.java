package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.opinion.ItemOpinionFrameRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemOpinionFrameRoleService {

    /**
     * 根据唯一标示删除
     *
     * @param id 唯一标示
     */
    void deleteById(String id);

    /**
     * 根据事项和意见框绑定的唯一标示查找绑定的角色
     *
     * @param itemOpinionFrameId 事项和意见框绑定的唯一标示
     * @return
     */
    List<ItemOpinionFrameRole> listByItemOpinionFrameId(String itemOpinionFrameId);

    /**
     * 根据事项和意见框绑定的唯一标示查找绑定的角色（包含角色名称）
     *
     * @param itemOpinionFrameId 事项和意见框绑定的唯一标示
     * @return List<ItemOpinionFrameRole>
     */
    List<ItemOpinionFrameRole> listByItemOpinionFrameIdContainRoleName(String itemOpinionFrameId);

    /**
     * 删除多个
     *
     * @param ids 唯一标识集合
     */
    void remove(String[] ids);

    /**
     *
     * 删除绑定对应的角色
     *
     * @param itemOpinionFrameId 事项和意见框绑定的唯一标示
     */
    void removeByItemOpinionFrameId(String itemOpinionFrameId);

    /**
     * 保存或者更新
     *
     * @param itemOpinionFrameId 事项和意见框绑定的唯一标示
     * @param roleId 角色id
     * @return ItemOpinionFrameRole
     */
    ItemOpinionFrameRole saveOrUpdate(String itemOpinionFrameId, String roleId);
}
