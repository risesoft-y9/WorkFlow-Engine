package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.OrganWordUseHistory;
import net.risesoft.repository.jpa.OrganWordUseHistoryRepository;
import net.risesoft.service.OrganWordUseHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OrganWordUseHistoryServiceImpl implements OrganWordUseHistoryService {

    private final OrganWordUseHistoryRepository organWordUseHistoryRepository;

    @Override
    public OrganWordUseHistory findByItemIdAndNumberString(String itemId, String numberString) {
        return organWordUseHistoryRepository.findByItemIdAndNumberString(itemId, numberString);
    }

    @Override
    public OrganWordUseHistory findByProcessSerialNumberAndCustom(String processSerialNumber, String custom) {
        return organWordUseHistoryRepository.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
    }

    @Override
    @Transactional
    public OrganWordUseHistory save(OrganWordUseHistory organWordUseHistory) {
        return organWordUseHistoryRepository.save(organWordUseHistory);
    }
}
