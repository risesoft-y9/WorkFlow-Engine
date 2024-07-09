package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.entity.InterfaceInfo;
import net.risesoft.entity.ItemInterfaceBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.*;
import net.risesoft.service.ItemInterfaceBindService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemInterfaceBindServiceImpl implements ItemInterfaceBindService {

    private final ItemInterfaceBindRepository itemInterfaceBindRepository;

    private final InterfaceInfoRepository interfaceInfoRepository;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final ItemInterfaceTaskBindRepository itemInterfaceTaskBindRepository;

    private final ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    @Override
    public List<ItemInterfaceBind> findByInterfaceId(String interfaceId) {
        List<ItemInterfaceBind> list = itemInterfaceBindRepository.findByInterfaceIdOrderByCreateTimeDesc(interfaceId);
        for (ItemInterfaceBind bind : list) {
            SpmApproveItem item = spmApproveItemRepository.findById(bind.getItemId()).orElse(null);
            bind.setItemName(item != null ? item.getName() : "事项不存在");
        }
        return list;
    }

    @Override
    public List<ItemInterfaceBind> findByItemId(String itemId) {
        List<ItemInterfaceBind> bindList = itemInterfaceBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
        for (ItemInterfaceBind bind : bindList) {
            InterfaceInfo info = interfaceInfoRepository.findById(bind.getInterfaceId()).orElse(null);
            bind.setInterfaceName(info != null ? info.getInterfaceName() : "接口不存在");
            bind.setInterfaceAddress(info != null ? info.getInterfaceAddress() : "接口不存在");
        }
        return bindList;
    }

    @Override
    @Transactional
    public void removeBind(String id) {
        ItemInterfaceBind bind = itemInterfaceBindRepository.findById(id).orElse(null);
        if (bind != null) {
            itemInterfaceBindRepository.deleteById(id);
            itemInterfaceTaskBindRepository.deleteByItemIdAndInterfaceId(bind.getItemId(), bind.getInterfaceId());
            itemInterfaceParamsBindRepository.deleteByItemIdAndInterfaceId(bind.getItemId(), bind.getInterfaceId());
        }
    }

    @Override
    @Transactional
    public void saveBind(String itemId, String[] interfaceIds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String interfaceId : interfaceIds) {
            ItemInterfaceBind item = itemInterfaceBindRepository.findByInterfaceIdAndItemId(interfaceId, itemId);
            if (item == null) {
                item = new ItemInterfaceBind();
                item.setItemId(itemId);
                item.setInterfaceId(interfaceId);
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setCreateTime(sdf.format(new Date()));
                itemInterfaceBindRepository.save(item);
            }
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId) {
        try {
            List<ItemInterfaceBind> bindList = itemInterfaceBindRepository.findByItemIdOrderByCreateTimeDesc(itemId);
            for (ItemInterfaceBind bind : bindList) {
                ItemInterfaceBind newBind = new ItemInterfaceBind();
                newBind.setItemId(newItemId);
                newBind.setInterfaceId(bind.getInterfaceId());
                newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newBind.setCreateTime(bind.getCreateTime());
                itemInterfaceBindRepository.save(newBind);
            }
        } catch (Exception e) {
            LOGGER.error("复制事项接口绑定关系失败", e);
        }
    }
}
