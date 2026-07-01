package net.risesoft.listener;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Item;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.enums.SysTypeEnum;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;

/**
 * 抄送事件监听器
 *
 * @author qinman
 * @date 2025/07/23
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ItemListener {

    private final ItemMappingConfRepository itemMappingConfRepository;
    private final ItemViewConfService itemViewConfService;

    @EventListener
    public void onCreated(Y9EntityCreatedEvent<Item> event) {
        Item item = event.getEntity();
        String tenantId = item.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);
        List<String> viewTypes = List.of("draft", "todo", "doing", "done");
        viewTypes.forEach(viewType -> itemViewConfService.init(item.getId(), viewType));
    }

    @EventListener
    public void onDeleted(Y9EntityDeletedEvent<Item> event) {
        Item item = event.getEntity();
        String tenantId = item.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("item deleted: {},{}", item.getId(), item.getName());
        }
    }

    @EventListener
    public void onUpdate(Y9EntityUpdatedEvent<Item> event) {
        Item updated = event.getUpdatedEntity();

        ItemMappingConf itemMappingConfIn = itemMappingConfRepository
            .findTopByItemIdAndSysTypeOrderByCreateTimeDesc(updated.getId(), SysTypeEnum.IN.getValue());
        // 删除事项映射字段
        if (itemMappingConfIn != null && (StringUtils.isBlank(updated.getDockingItemId())
            || !updated.getDockingItemId().equals(itemMappingConfIn.getMappingId()))) {
            itemMappingConfRepository.deleteByMappingId(itemMappingConfIn.getMappingId());
        }
        ItemMappingConf itemMappingConfOut = itemMappingConfRepository
            .findTopByItemIdAndSysTypeOrderByCreateTimeDesc(updated.getId(), SysTypeEnum.OUT.getValue());
        // 删除系统映射字段
        if (itemMappingConfOut != null && (StringUtils.isBlank(updated.getDockingSystem())
            || !updated.getDockingSystem().equals(itemMappingConfOut.getMappingId()))) {
            itemMappingConfRepository.deleteByMappingId(itemMappingConfOut.getMappingId());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("item onUpdate: {}", updated.getName());
        }
    }
}
