package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.EntrustHistory;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.platform.Person;
import net.risesoft.repository.jpa.EntrustHistoryRepository;
import net.risesoft.service.EntrustHistoryService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;

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

    private final SpmApproveItemService spmApproveItemService;

    private final PersonApi personManager;

    @Override
    public Page<EntrustHistory> findAll(int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        Page<EntrustHistory> ehPage = entrustHistoryRepository.findAll(pageable);
        return ehPage;
    }

    @Override
    public Page<EntrustHistory> findByAssigneeId(String assigneeId, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        Page<EntrustHistory> ehPage =
            entrustHistoryRepository.findByAssigneeIdOrderByStartTimeDesc(assigneeId, pageable);
        return ehPage;
    }

    @Override
    public List<EntrustHistory> list(String ownerId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<EntrustHistory> ehList = entrustHistoryRepository.findByOwnerId(ownerId);
        Person pTemp = null;
        SpmApproveItem itemTemp = null;
        for (EntrustHistory eh : ehList) {
            pTemp = personManager.get(tenantId, eh.getAssigneeId()).getData();
            eh.setAssigneeName(pTemp.getName());
            pTemp = personManager.get(tenantId, eh.getOwnerId()).getData();
            eh.setOwnerName(pTemp.getName());

            String itemId = eh.getItemId();
            if ("ALL".equals(itemId)) {
                eh.setItemName("所有事项");
            } else {
                itemTemp = spmApproveItemService.findById(eh.getItemId());
                eh.setItemName(itemTemp.getName());
            }
        }
        return ehList;
    }

    @Override
    public Page<EntrustHistory> list(String ownerId, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows);
        Page<EntrustHistory> ehPage = entrustHistoryRepository.findByOwnerIdOrderByCreatTimeDesc(ownerId, pageable);
        return ehPage;
    }

    @Override
    public List<EntrustHistory> list(String ownerId, String itemId) {
        List<EntrustHistory> ehList = entrustHistoryRepository.findByOwnerIdAndItemId(ownerId, itemId);
        Person pTemp = null;
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem itemTemp = null;

        String itemName = "此事项不存在";
        if (UtilConsts.ALL.equals(itemId)) {
            itemName = "所有事项";
        } else {
            itemTemp = spmApproveItemService.findById(itemId);
            itemName = itemTemp.getName();
        }

        for (EntrustHistory eh : ehList) {
            pTemp = personManager.get(tenantId, eh.getAssigneeId()).getData();
            eh.setAssigneeName(pTemp.getName());
            pTemp = personManager.get(tenantId, eh.getOwnerId()).getData();
            eh.setOwnerName(pTemp.getName());
            eh.setItemName(itemName);
        }
        return ehList;
    }

    @Override
    @Transactional
    public EntrustHistory save(EntrustHistory entrustHistory) throws ParseException {
        return entrustHistoryRepository.save(entrustHistory);
    }
}
