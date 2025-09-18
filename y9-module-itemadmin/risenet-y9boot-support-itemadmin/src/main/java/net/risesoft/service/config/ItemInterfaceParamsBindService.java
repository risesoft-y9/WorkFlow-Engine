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
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param type 参数类型
     * @return List<ItemInterfaceParamsBind>
     */
    List<ItemInterfaceParamsBind> listByItemIdAndInterfaceIdAndType(String itemId, String interfaceId, String type);

    /**
     * 根据事项id和接口id获取绑定列表
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @return List<ItemInterfaceParamsBind>
     */
    List<ItemInterfaceParamsBind> listByItemIdAndInterfaceId(String itemId, String interfaceId);

    /**
     * 删除绑定
     *
     * @param id 绑定id
     */
    void removeBind(String id);

    /**
     * 保存绑定
     *
     * @param info 绑定信息
     */
    void saveBind(ItemInterfaceParamsBind info);

}
