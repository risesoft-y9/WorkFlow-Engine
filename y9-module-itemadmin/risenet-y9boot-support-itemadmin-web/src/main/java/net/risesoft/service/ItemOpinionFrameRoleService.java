package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ItemOpinionFrameRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ItemOpinionFrameRoleService {

    /**
     * 根据唯一标示删除
     * 
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据事项和意见框绑定的唯一标示查找绑定的角色
     * 
     * @param itemOpinionFrameId
     * @return
     */
    List<ItemOpinionFrameRole> findByItemOpinionFrameId(String itemOpinionFrameId);

    /**
     * 根据事项和意见框绑定的唯一标示查找绑定的角色（包含角色名称）
     * 
     * @param itemOpinionFrameId
     * @return
     */
    List<ItemOpinionFrameRole> findByItemOpinionFrameIdContainRoleName(String itemOpinionFrameId);

    /**
     * Description: 删除多个
     * 
     * @param ids
     */
    void remove(String[] ids);

    /**
     * Description: 删除绑定对应的角色
     * 
     * @param itemOpinionFrameId
     */
    void removeByItemOpinionFrameId(String itemOpinionFrameId);

    /**
     * 保存或者更新
     * 
     * @param itemOpinionFrameId
     * @param roleId
     * @return
     */
    ItemOpinionFrameRole saveOrUpdate(String itemOpinionFrameId, String roleId);
}
