package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.OrganWordUseHistory;
import net.risesoft.repository.jpa.OrganWordUseHistoryRepository;
import net.risesoft.service.OrganWordUseHistoryService;

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

    @Override
    public OrganWordUseHistory findByItemIdAndNumberStringAndCustom(String itemId, String numberString, String custom) {
        return organWordUseHistoryRepository.findByItemIdAndNumberStringAndCustom(itemId, numberString, custom);
    }

    @Override
    public OrganWordUseHistory findByItemIdAndNumberStrAndCustomAndProcessSerialNumber(String itemId,
        String numberString, String custom, String processSerialNumber) {
        return organWordUseHistoryRepository.findByItemIdAndNumberStringAndCustomAndProcessSerialNumber(itemId,
            numberString, custom, processSerialNumber);
    }
}
