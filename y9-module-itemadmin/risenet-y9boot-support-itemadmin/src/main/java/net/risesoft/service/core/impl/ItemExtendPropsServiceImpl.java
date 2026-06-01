package net.risesoft.service.core.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ItemExtendProps;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemExtendPropsRepository;
import net.risesoft.service.core.ItemExtendPropsService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemExtendPropsServiceImpl implements ItemExtendPropsService {

    private final ItemExtendPropsRepository itemExtendPropsRepository;

    @Override
    @Transactional
    public void deleteByItemId(String itemId) {
        itemExtendPropsRepository.deleteByItemId(itemId);
    }

    @Override
    public ItemExtendProps findByItemId(String itemId) {
        return itemExtendPropsRepository.findByItemId(itemId);
    }

    @Override
    @Transactional
    public void saveExtendProps(ItemExtendProps item) {
        if (StringUtils.isBlank(item.getId())) {
            item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        itemExtendPropsRepository.save(item);
    }
}
