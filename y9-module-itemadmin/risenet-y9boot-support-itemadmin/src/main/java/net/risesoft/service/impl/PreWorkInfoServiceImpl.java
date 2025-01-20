package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.PreWorkInfo;
import net.risesoft.repository.jpa.PreWorkInfoRepository;
import net.risesoft.service.PreWorkInfoService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class PreWorkInfoServiceImpl implements PreWorkInfoService {

    private final PreWorkInfoRepository preWorkInfoRepository;

    @Override
    @Transactional
    public void deleteById(String id) {
        preWorkInfoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(PreWorkInfo preWorkInfo) {
        preWorkInfoRepository.save(preWorkInfo);
    }
}
