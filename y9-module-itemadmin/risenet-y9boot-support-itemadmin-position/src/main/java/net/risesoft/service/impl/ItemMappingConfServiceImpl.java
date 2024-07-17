package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemMappingConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.service.ItemMappingConfService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class ItemMappingConfServiceImpl implements ItemMappingConfService {

    private final ItemMappingConfRepository itemMappingConfRepository;

    @Override
    @Transactional
    public void delItemMappingConf(String[] ids) {
        for (String id : ids) {
            itemMappingConfRepository.deleteById(id);
        }
    }

    @Override
    public List<ItemMappingConf> listByItemIdAndMappingId(String itemId, String mappingId) {
        return itemMappingConfRepository.findByItemIdAndMappingIdOrderByCreateTimeDesc(itemId, mappingId);
    }

    @Override
    @Transactional
    public void saveItemMappingConf(ItemMappingConf itemMappingConf) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = itemMappingConf.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemMappingConf oldConf = itemMappingConfRepository.findById(id).orElse(null);
            if (null != oldConf) {
                oldConf.setColumnName(itemMappingConf.getColumnName());
                oldConf.setMappingName(itemMappingConf.getMappingName());
                oldConf.setMappingTableName(itemMappingConf.getMappingTableName());
                oldConf.setTableName(itemMappingConf.getTableName());
                oldConf.setCreateTime(sdf.format(new Date()));
                itemMappingConfRepository.save(oldConf);
            }
        } else {
            itemMappingConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            itemMappingConf.setCreateTime(sdf.format(new Date()));
            itemMappingConfRepository.save(itemMappingConf);
        }
    }

}
