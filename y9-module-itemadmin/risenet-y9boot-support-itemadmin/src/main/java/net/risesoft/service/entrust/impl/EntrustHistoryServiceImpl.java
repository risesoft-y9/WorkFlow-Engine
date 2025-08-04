package net.risesoft.service.entrust.impl;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Item;
import net.risesoft.entity.entrust.EntrustHistory;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.entrust.EntrustHistoryRepository;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.entrust.EntrustHistoryService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EntrustHistoryServiceImpl implements EntrustHistoryService {

    private final EntrustHistoryRepository entrustHistoryRepository;

    private final ItemService itemService;

    private final PersonApi personApi;

    @Override
    public List<EntrustHistory> listByOwnerId(String ownerId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<EntrustHistory> ehList = entrustHistoryRepository.findByOwnerId(ownerId);
        Person pTemp = null;
        Item itemTemp = null;
        for (EntrustHistory eh : ehList) {
            pTemp = personApi.get(tenantId, eh.getAssigneeId()).getData();
            eh.setAssigneeName(pTemp.getName());
            pTemp = personApi.get(tenantId, eh.getOwnerId()).getData();
            eh.setOwnerName(pTemp.getName());

            String itemId = eh.getItemId();
            if ("ALL".equals(itemId)) {
                eh.setItemName("所有事项");
            } else {
                itemTemp = itemService.findById(eh.getItemId());
                eh.setItemName(itemTemp.getName());
            }
        }
        return ehList;
    }

    @Override
    public List<EntrustHistory> listByOwnerIdAndItemId(String ownerId, String itemId) {
        List<EntrustHistory> ehList = entrustHistoryRepository.findByOwnerIdAndItemId(ownerId, itemId);
        Person pTemp = null;
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item itemTemp = null;

        String itemName = "此事项不存在";
        if (UtilConsts.ALL.equals(itemId)) {
            itemName = "所有事项";
        } else {
            itemTemp = itemService.findById(itemId);
            itemName = itemTemp.getName();
        }

        for (EntrustHistory eh : ehList) {
            pTemp = personApi.get(tenantId, eh.getAssigneeId()).getData();
            eh.setAssigneeName(pTemp.getName());
            pTemp = personApi.get(tenantId, eh.getOwnerId()).getData();
            eh.setOwnerName(pTemp.getName());
            eh.setItemName(itemName);
        }
        return ehList;
    }

    @Override
    public Page<EntrustHistory> pageAll(int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        return entrustHistoryRepository.findAll(pageable);
    }

    @Override
    public Page<EntrustHistory> pageByAssigneeId(String assigneeId, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        return entrustHistoryRepository.findByAssigneeIdOrderByStartTimeDesc(assigneeId, pageable);
    }

    @Override
    public Page<EntrustHistory> pageByOwnerId(String ownerId, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        return entrustHistoryRepository.findByOwnerIdOrderByCreatTimeDesc(ownerId, pageable);
    }

    @Override
    @Transactional
    public EntrustHistory save(EntrustHistory entrustHistory) throws ParseException {
        return entrustHistoryRepository.save(entrustHistory);
    }
}
