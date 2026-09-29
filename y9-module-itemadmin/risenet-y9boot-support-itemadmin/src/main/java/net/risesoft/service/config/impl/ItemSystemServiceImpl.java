package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ItemSystem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemSystemRepository;
import net.risesoft.service.config.ItemSystemService;

/**
 * @author qinman
 * @date 2026/07/09
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemSystemServiceImpl implements ItemSystemService {

    private final ItemSystemRepository itemSystemRepository;

    public ItemSystemServiceImpl(ItemSystemRepository itemSystemRepository) {
        this.itemSystemRepository = itemSystemRepository;
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        itemSystemRepository.deleteById(id);
    }

    @Override
    public ItemSystem findById(String id) {
        return itemSystemRepository.findById(id).orElse(null);
    }

    @Override
    public List<ItemSystem> listAll() {
        return itemSystemRepository.findAllOrderByTabIndex();
    }

    @Override
    @Transactional
    public ItemSystem save(ItemSystem entity) {
        ItemSystem itemSystem;
        if (StringUtils.isNotBlank(entity.getId())) {
            // 更新
            Optional<ItemSystem> optional = itemSystemRepository.findById(entity.getId());
            if (optional.isPresent()) {
                itemSystem = optional.get();
            } else {
                // id 不存在则新建
                itemSystem = new ItemSystem();
                itemSystem.setId(entity.getId());
            }
        } else {
            // 新建，自动生成id
            itemSystem = new ItemSystem();
            itemSystem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            // 新建时自动设置排序号
            Integer maxTabIndex = itemSystemRepository.getMaxTabIndex();
            itemSystem.setTabIndex(maxTabIndex == null ? 1 : maxTabIndex + 1);
        }
        itemSystem.setName(entity.getName());
        itemSystem.setCnName(entity.getCnName());
        if (entity.getTabIndex() != null) {
            itemSystem.setTabIndex(entity.getTabIndex());
        }
        return itemSystemRepository.save(itemSystem);
    }

    @Override
    @Transactional
    public void saveOrder(List<String> ids) {
        List<ItemSystem> list = new ArrayList<>();
        int i = 1;
        for (String id : ids) {
            ItemSystem itemSystem = this.findById(id);
            if (itemSystem != null) {
                itemSystem.setTabIndex(i);
                list.add(itemSystem);
            }
            i++;
        }
        itemSystemRepository.saveAll(list);
    }

    @Override
    public boolean isNameExist(String name, String id) {
        ItemSystem existing = itemSystemRepository.findByName(name);
        if (existing == null) {
            return false;
        }
        // 编辑时排除自身
        if (StringUtils.isNotBlank(id)) {
            return !existing.getId().equals(id);
        }
        return true;
    }

    @Override
    public boolean isCnNameExist(String cnName, String id) {
        ItemSystem existing = itemSystemRepository.findByCnName(cnName);
        if (existing == null) {
            return false;
        }
        // 编辑时排除自身
        if (StringUtils.isNotBlank(id)) {
            return !existing.getId().equals(id);
        }
        return true;
    }
}
