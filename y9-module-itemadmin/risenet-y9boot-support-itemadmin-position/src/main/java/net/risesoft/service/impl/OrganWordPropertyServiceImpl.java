package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.OrganWordProperty;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.OrganWordPropertyRepository;
import net.risesoft.service.OrganWordPropertyService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OrganWordPropertyServiceImpl implements OrganWordPropertyService {

    private final OrganWordPropertyRepository organWordPropertyRepository;

    @Override
    public OrganWordProperty findById(String id) {
        return organWordPropertyRepository.findById(id).orElse(null);
    }

    @Override
    public OrganWordProperty findByOrganWordIdAndName(String organWordId, String name) {
        return organWordPropertyRepository.findByOrganWordIdAndName(organWordId, name);
    }

    @Override
    public List<OrganWordProperty> listAll() {
        return organWordPropertyRepository.findAll();
    }

    @Override
    public List<OrganWordProperty> listByOrganWordId(String organWordId) {
        return organWordPropertyRepository.findByOrganWordIdOrderByTabIndexAsc(organWordId);
    }

    @Override
    @Transactional(readOnly = false)
    public void removeOrganWordPropertys(String[] organWordPropertyIds) {
        for (String id : organWordPropertyIds) {
            organWordPropertyRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public OrganWordProperty save(OrganWordProperty organWordProperty) {
        String id = organWordProperty.getId();
        if (StringUtils.isNotEmpty(id)) {
            OrganWordProperty oldop = this.findById(id);
            if (null != oldop) {
                oldop.setInitNumber(organWordProperty.getInitNumber());
                oldop.setName(organWordProperty.getName());
                organWordPropertyRepository.save(oldop);
            } else {
                oldop = organWordPropertyRepository.save(organWordProperty);
            }

            return oldop;
        }
        OrganWordProperty newop = new OrganWordProperty();
        newop.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newop.setInitNumber(organWordProperty.getInitNumber());
        newop.setName(organWordProperty.getName());
        newop.setOrganWordId(organWordProperty.getOrganWordId());
        Integer index = organWordPropertyRepository.getMaxTabIndex(organWordProperty.getOrganWordId());
        if (index == null) {
            newop.setTabIndex(1);
        } else {
            newop.setTabIndex(index + 1);
        }

        return organWordPropertyRepository.save(newop);
    }

    @Override
    @Transactional(readOnly = false)
    public void update4Order(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        try {
            for (String s : list) {
                String[] arr = s.split(SysVariables.COLON);
                organWordPropertyRepository.update4Order(Integer.parseInt(arr[1]), arr[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
