package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.AfterWorkInfo;
import net.risesoft.repository.jpa.AfterWorkInfoRepository;
import net.risesoft.service.AfterWorkInfoService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AfterWorkInfoServiceImpl implements AfterWorkInfoService {

    private final AfterWorkInfoRepository afterWorkInfoRepository;

    @Override
    @Transactional
    public void deleteById(String id) {
        afterWorkInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(AfterWorkInfo afterWorkInfo) {
        afterWorkInfoRepository.save(afterWorkInfo);
    }
}
