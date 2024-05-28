package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "itemInterfaceParamsBindService")
public class ItemInterfaceParamsBindServiceImpl implements ItemInterfaceParamsBindService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    @Override
    public List<ItemInterfaceParamsBind> getBindList(String itemId, String interfaceId, String type) {
        List<ItemInterfaceParamsBind> bindList = itemInterfaceParamsBindRepository.findByItemIdAndInterfaceIdAndBindTypeOrderByCreateTimeDesc(itemId, interfaceId, type);
        return bindList;
    }

    @Override
    @Transactional(readOnly = false)
    public void removeBind(String id) {
        itemInterfaceParamsBindRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveBind(ItemInterfaceParamsBind info) {
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
