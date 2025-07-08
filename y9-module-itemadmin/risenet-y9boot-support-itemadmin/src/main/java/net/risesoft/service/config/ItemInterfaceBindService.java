package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.interfaceinfo.ItemInterfaceBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public interface ItemInterfaceBindService {

    /**
     * 复制事项接口配置绑定信息
     *
     * @param itemId
     * @param newItemId
     */
    void copyBindInfo(String itemId, String newItemId);

    /**
     * 删除事项接口配置绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据接口id获取绑定关系列表
     *
     * @param interfaceId
     * @return
     */
    List<ItemInterfaceBind> listByInterfaceId(String interfaceId);

    /**
     * 根据事项id获取绑定列表
     *
     * @param itemId
     * @return
     */
    List<ItemInterfaceBind> listByItemId(String itemId);

    /**
     * 根据id删除绑定关系
     *
     * @param id
     */
    void removeBind(String id);

    /**
     * 保存事项链接绑定
     *
     * @param itemId
     * @param interfaceIds
     * @return
     */
    void saveBind(String itemId, String[] interfaceIds);
}
