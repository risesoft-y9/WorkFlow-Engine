package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemInterfaceParamsBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemInterfaceParamsBindRepository;
import net.risesoft.service.ItemInterfaceParamsBindService;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemInterfaceParamsBindServiceImpl implements ItemInterfaceParamsBindService {

    private final ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    @Override
    public List<ItemInterfaceParamsBind> listByItemIdAndInterfaceIdAndType(String itemId, String interfaceId,
        String type) {
        return itemInterfaceParamsBindRepository.findByItemIdAndInterfaceIdAndBindTypeOrderByCreateTimeDesc(itemId,
            interfaceId, type);
    }

    @Override
    @Transactional
    public void removeBind(String id) {
        itemInterfaceParamsBindRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveBind(ItemInterfaceParamsBind info) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = info.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemInterfaceParamsBind item = itemInterfaceParamsBindRepository.findById(id).orElse(null);
            if (null != item) {
                item.setColumnName(info.getColumnName());
                item.setTableName(info.getTableName());
                item.setParameterName(info.getParameterName());
                item.setParameterType(info.getParameterType());
                itemInterfaceParamsBindRepository.save(item);
            } else {
                item = new ItemInterfaceParamsBind();
                item.setColumnName(info.getColumnName());
                item.setTableName(info.getTableName());
                item.setParameterName(info.getParameterName());
                item.setParameterType(info.getParameterType());
                item.setBindType(info.getBindType());
                item.setInterfaceId(info.getInterfaceId());
                item.setItemId(info.getItemId());
                item.setId(info.getId());
                item.setCreateTime(sdf.format(new Date()));
                itemInterfaceParamsBindRepository.save(item);
            }
        } else {
            ItemInterfaceParamsBind item = new ItemInterfaceParamsBind();
            item.setColumnName(info.getColumnName());
            item.setTableName(info.getTableName());
            item.setParameterName(info.getParameterName());
            item.setParameterType(info.getParameterType());
            item.setBindType(info.getBindType());
            item.setInterfaceId(info.getInterfaceId());
            item.setItemId(info.getItemId());
            item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            item.setCreateTime(sdf.format(new Date()));
            itemInterfaceParamsBindRepository.save(item);
        }
    }
}
