package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.OrganWordDetail;
import net.risesoft.repository.jpa.OrganWordDetailRepository;
import net.risesoft.service.OrganWordDetailService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OrganWordDetailServiceImpl implements OrganWordDetailService {

    private final OrganWordDetailRepository organWordDetailRepository;

    @Override
    public OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue,
        Integer year, String itemId) {
        return organWordDetailRepository.findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year,
            itemId);
    }

    @Override
    @Transactional
    public OrganWordDetail save(OrganWordDetail organWordDetail) {
        return organWordDetailRepository.save(organWordDetail);
    }

    @Override
    @Transactional
    public OrganWordDetail saveOrUpdate(OrganWordDetail organWordDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getMaxNumber(String custom, String itemId) {
        return organWordDetailRepository.getMaxNumber(custom, itemId);
    }

    @Override
    public OrganWordDetail findByCustomAndCharacterValueAndItemId(String custom, String characterValue, String itemId) {
        return organWordDetailRepository.findByCustomAndCharacterValueAndItemId(custom, characterValue, itemId);
    }

    @Override
    public OrganWordDetail findByCustomAndItemId(String custom, String itemId) {
        return organWordDetailRepository.findByCustomAndItemId(custom, itemId);
    }

}
