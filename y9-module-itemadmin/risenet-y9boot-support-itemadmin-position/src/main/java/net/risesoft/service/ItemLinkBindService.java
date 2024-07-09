package net.risesoft.service;

import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.ItemLinkRole;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public interface ItemLinkBindService {

    /**
     * 根据事项id获取绑定列表
     *
     * @param itemId
     * @return
     */
    List<ItemLinkBind> findByItemId(String itemId);

    /**
     * 根据事项链接绑定id获取权限绑定列表
     *
     * @param itemLinkId
     * @return
     */
    List<ItemLinkRole> findByItemLinkId(String itemLinkId);

    /**
     * 根据链接id获取绑定关系列表
     *
     * @param linkId
     * @return
     */
    List<ItemLinkBind> findByLinkId(String linkId);

    /**
     * 获取链接绑定角色列表
     *
     * @param itemLinkId
     * @return
     */
    List<ItemLinkRole> getBindRoleList(String itemLinkId);

    /**
     * 根据id删除绑定关系
     *
     * @param ids
     */
    void removeBind(String[] ids);

    /**
     * 根据id删除权限绑定
     *
     * @param ids
     */
    void removeRole(String[] ids);

    /**
     * 保存绑定角色
     *
     * @param itemLinkId 绑定关系id
     * @param roleIds 角色id
     */
    void saveBindRole(String itemLinkId, String roleIds);

    /**
     * 保存事项链接绑定
     *
     * @param itemId
     * @param linkIds
     * @return
     */
    void saveItemLinkBind(String itemId, String[] linkIds);

    /**
     * 复制事项链接绑定信息
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId, String newItemId);
}
