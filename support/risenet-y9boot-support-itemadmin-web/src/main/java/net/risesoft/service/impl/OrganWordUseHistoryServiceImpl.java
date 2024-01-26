package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OrganWordUseHistory;
import net.risesoft.repository.jpa.OrganWordUseHistoryRepository;
import net.risesoft.service.OrganWordUseHistoryService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "organWordUseHistoryService")
public class OrganWordUseHistoryServiceImpl implements OrganWordUseHistoryService {

    @Autowired
    private OrganWordUseHistoryRepository organWordUseHistoryRepository;

    @Override
    public OrganWordUseHistory findByItemIdAndNumberString(String itemId, String numberString) {
        return organWordUseHistoryRepository.findByItemIdAndNumberString(itemId, numberString);
    }

    @Override
    public OrganWordUseHistory findByProcessSerialNumberAndCustom(String processSerialNumber, String custom) {
        return organWordUseHistoryRepository.findByProcessSerialNumberAndCustom(processSerialNumber, custom);
    }

    @Override
    @Transactional(readOnly = false)
    public OrganWordUseHistory save(OrganWordUseHistory organWordUseHistory) {
        return organWordUseHistoryRepository.save(organWordUseHistory);
    }
}
