package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.DynamicRole;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.DynamicRoleRepository;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DynamicRoleServiceImpl implements DynamicRoleService {

    private final DynamicRoleRepository dynamicRoleRepository;

    @Override
    public DynamicRole getById(String id) {
        return dynamicRoleRepository.findById(id).orElse(null);
    }

    @Override
    public List<DynamicRole> listAll() {
        return dynamicRoleRepository.findAllByOrderByTabIndexAsc();
    }

    @Override
    @Transactional
    public void removeDynamicRoles(String[] dynamicRoleIds) {
        for (String id : dynamicRoleIds) {
            dynamicRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public DynamicRole saveOrUpdate(DynamicRole dynamicRole) {
        String id = dynamicRole.getId();
        if (StringUtils.isNotEmpty(id)) {
            DynamicRole older = dynamicRoleRepository.findById(id).orElse(null);
            if (null != older) {
                older.setName(dynamicRole.getName());
                older.setDescription(dynamicRole.getDescription());
                older.setClassPath(dynamicRole.getClassPath());
                older.setUseProcessInstanceId(dynamicRole.isUseProcessInstanceId());
                older.setKinds(dynamicRole.getKinds());
                older.setRanges(dynamicRole.getRanges());
                older.setRoleId(dynamicRole.getRoleId());
                older.setDeptPropCategory(dynamicRole.getDeptPropCategory());
                dynamicRoleRepository.save(older);
                return older;
            } else {
                return dynamicRoleRepository.save(dynamicRole);
            }
        }

        DynamicRole d = new DynamicRole();
        d.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        d.setName(dynamicRole.getName());
        d.setTenantId(Y9LoginUserHolder.getTenantId());
        d.setDescription(dynamicRole.getDescription());
        d.setClassPath(dynamicRole.getClassPath());
        d.setUseProcessInstanceId(dynamicRole.isUseProcessInstanceId());
        d.setKinds(dynamicRole.getKinds());
        d.setRanges(dynamicRole.getRanges());
        d.setRoleId(dynamicRole.getRoleId());
        d.setDeptPropCategory(dynamicRole.getDeptPropCategory());

        Integer tabIndex = dynamicRoleRepository.getMaxTabIndex();
        if (tabIndex == null) {
            tabIndex = 0;
        } else {
            tabIndex += 1;
        }
        d.setTabIndex(tabIndex);
        dynamicRoleRepository.save(d);
        return d;
    }

    @Override
    public DynamicRole findByNameAndClassPath(String name, String classPath) {
        return dynamicRoleRepository.findByNameAndClassPath(name, classPath);
    }
}
