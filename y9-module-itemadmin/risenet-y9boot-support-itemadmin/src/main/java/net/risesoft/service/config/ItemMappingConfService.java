package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemMappingConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemMappingConfService {

    /**
     * 删除映射
     *
     * @param ids 唯一标识集合
     */
    void delItemMappingConf(String[] ids);

    /**
     * 获取映射字段列表
     *
     * @param itemId 事项id
     * @param mappingId 映射id
     * @return List<ItemMappingConf>
     */
    List<ItemMappingConf> listByItemIdAndMappingId(String itemId, String mappingId);

    /**
     * 保存映射信息
     *
     * @param itemMappingConf 映射信息
     */
    void saveItemMappingConf(ItemMappingConf itemMappingConf);
}
