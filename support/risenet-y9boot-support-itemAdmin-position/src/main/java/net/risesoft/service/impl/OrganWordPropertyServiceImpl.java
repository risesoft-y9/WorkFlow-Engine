package net.risesoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import net.risesoft.consts.UtilConsts;
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
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "organWordPropertyService")
public class OrganWordPropertyServiceImpl implements OrganWordPropertyService {

    @Autowired
    private OrganWordPropertyRepository organWordPropertyRepository;

    @Override
    public List<OrganWordProperty> findAll() {
        return organWordPropertyRepository.findAll();
    }

    @Override
    public OrganWordProperty findById(String id) {
        return organWordPropertyRepository.findById(id).orElse(null);
    }

    @Override
    public List<OrganWordProperty> findByOrganWordId(String organWordId) {
        return organWordPropertyRepository.findByOrganWordIdOrderByTabIndexAsc(organWordId);
    }

    @Override
    public OrganWordProperty findByOrganWordIdAndName(String organWordId, String name) {
        return organWordPropertyRepository.findByOrganWordIdAndName(organWordId, name);
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
    public Map<String, Object> save(OrganWordProperty organWordProperty) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败!");
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

            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功!");
            map.put("property", oldop);
            return map;
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

        organWordPropertyRepository.save(newop);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "保存成功!");
        map.put("property", newop);
        return map;
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
