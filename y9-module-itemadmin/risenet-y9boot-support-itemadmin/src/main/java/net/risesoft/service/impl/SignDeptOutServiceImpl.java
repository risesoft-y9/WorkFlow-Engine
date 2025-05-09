package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.SignOutDept;
import net.risesoft.entity.SignOutDeptType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SignOutDeptRepository;
import net.risesoft.repository.jpa.SignOutDeptTypeRepository;
import net.risesoft.service.SignDeptOutService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SignDeptOutServiceImpl implements SignDeptOutService {

    private final SignOutDeptRepository signOutDeptRepository;

    private final SignOutDeptTypeRepository signOutDeptTypeRepository;

    @Override
    @Transactional
    public void disableDept(String[] ids, Integer status) {
        for (String id : ids) {
            SignOutDept signOutDept = signOutDeptRepository.findById(id).orElse(null);
            if (signOutDept != null) {
                signOutDept.setIsForbidden(status);
                signOutDeptRepository.save(signOutDept);
            }
        }
    }

    @Override
    @Transactional
    public void disableType(String id, Integer status) {
        SignOutDeptType signOutDeptType = signOutDeptTypeRepository.findById(id).orElse(null);
        if (signOutDeptType != null) {
            signOutDeptType.setIsForbidden(status);
            signOutDeptTypeRepository.save(signOutDeptType);
        }
    }

    @Override
    public List<SignOutDeptType> getDeptTypeList(String name) {
        return signOutDeptTypeRepository.findAllOrderByTabIndexAsc();
    }

    @Override
    public List<SignOutDept> listAll(String deptTypeId, String name, Integer page, Integer rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "deptOrder");
        // PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return signOutDeptRepository.findAll(new Specification<SignOutDept>() {
            @Override
            public Predicate toPredicate(Root<SignOutDept> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(name)) {
                    list.add(builder.like(root.get("deptName"), "%" + name + "%"));
                }
                if (StringUtils.isNotEmpty(deptTypeId)) {
                    list.add(builder.equal(root.get("deptTypeId"), deptTypeId));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @Override
    @Transactional
    public void remove(String[] ids) {
        for (String id : ids) {
            signOutDeptRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void removeDetpType(String id) {
        signOutDeptTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveDeptOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        for (String s : list) {
            String[] arr = s.split(SysVariables.COLON);
            signOutDeptRepository.updateOrder(Integer.parseInt(arr[1]), arr[0]);
        }
    }

    @Override
    @Transactional
    public void saveDetpType(SignOutDeptType info) {
        if (StringUtils.isBlank(info.getDeptTypeId())) {
            info.setDeptTypeId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            Integer tabIndex = signOutDeptTypeRepository.getMaxTabIndex();
            info.setTabIndex(tabIndex == null ? 1 : tabIndex + 1);
        }
        signOutDeptTypeRepository.save(info);
        List<SignOutDept> list = signOutDeptRepository.findByDeptTypeId(info.getDeptTypeId());
        for (SignOutDept signOutDept : list) {
            signOutDept.setDeptType(info.getDeptType());
            signOutDeptRepository.save(signOutDept);
        }
    }

    @Override
    @Transactional
    public void saveOrUpdate(SignOutDept info) {
        if (StringUtils.isBlank(info.getDeptId())) {
            info.setDeptId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            Integer maxDeptOrder = signOutDeptRepository.getMaxDeptOrder(info.getDeptTypeId());
            info.setDeptOrder(maxDeptOrder == null ? 1 : maxDeptOrder + 1);
        }
        signOutDeptRepository.save(info);
    }

    @Override
    @Transactional
    public void saveTypeOrder(String[] idAndTabIndexs) {
        List<String> list = Lists.newArrayList(idAndTabIndexs);
        for (String s : list) {
            String[] arr = s.split(SysVariables.COLON);
            signOutDeptTypeRepository.updateOrder(Integer.parseInt(arr[1]), arr[0]);
        }
    }

    @Override
    @Transactional
    public SignOutDept findByDeptNameMax(String deptNameMax) {
        return signOutDeptRepository.findByDeptNameMax(deptNameMax);
    }
}
