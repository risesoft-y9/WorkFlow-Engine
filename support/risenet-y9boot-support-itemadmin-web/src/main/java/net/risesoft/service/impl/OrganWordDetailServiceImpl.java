package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OrganWordDetail;
import net.risesoft.repository.jpa.OrganWordDetailRepository;
import net.risesoft.service.OrganWordDetailService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "organWordDetailService")
public class OrganWordDetailServiceImpl implements OrganWordDetailService {

    @Autowired
    private OrganWordDetailRepository organWordDetailRepository;

    @Override
    public OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue,
        Integer year, String itemId) {
        return organWordDetailRepository.findByCustomAndCharacterValueAndYearAndItemId(custom, characterValue, year,
            itemId);
    }

    @Override
    @Transactional(readOnly = false)
    public OrganWordDetail save(OrganWordDetail organWordDetail) {
        return organWordDetailRepository.save(organWordDetail);
    }

    @Override
    @Transactional(readOnly = false)
    public OrganWordDetail saveOrUpdate(OrganWordDetail organWordDetail) {
        // TODO Auto-generated method stub
        return null;
    }

}
