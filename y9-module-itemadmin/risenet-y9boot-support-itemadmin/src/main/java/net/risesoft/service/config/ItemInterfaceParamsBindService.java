package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.interfaceinfo.ItemInterfaceParamsBind;

/**
 * @author zhangchongjie
 * @date 2024/05/27
 */
public interface ItemInterfaceParamsBindService {

    /**
     * 获取绑定列表
     *
     * @param itemId
     * @param interfaceId
     * @param type
     * @return
     */
    List<ItemInterfaceParamsBind> listByItemIdAndInterfaceIdAndType(String itemId, String interfaceId, String type);

    /**
     * 根据事项id和接口id获取绑定列表
     *
     * @param itemId
     * @param interfaceId
     * @return
     */
    List<ItemInterfaceParamsBind> listByItemIdAndInterfaceId(String itemId, String interfaceId);

    /**
     * 删除绑定
     *
     * @param id
     */
    void removeBind(String id);

    /**
     * 保存绑定
     *
     * @param info
     */
    void saveBind(ItemInterfaceParamsBind info);

}
