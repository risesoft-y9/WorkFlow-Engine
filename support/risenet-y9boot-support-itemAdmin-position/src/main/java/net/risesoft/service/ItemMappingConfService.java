package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ItemMappingConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemMappingConfService {

    /**
     * Description: 删除映射
     * 
     * @param ids
     */
    void delItemMappingConf(String[] ids);

    /**
     * 获取映射字段列表
     *
     * @param itemId
     * @param mappingId
     * @return
     */
    List<ItemMappingConf> getList(String itemId, String mappingId);

    /**
     * 保存映射信息
     *
     * @param itemMappingConf
     * @return
     */
    void saveItemMappingConf(ItemMappingConf itemMappingConf);
}
