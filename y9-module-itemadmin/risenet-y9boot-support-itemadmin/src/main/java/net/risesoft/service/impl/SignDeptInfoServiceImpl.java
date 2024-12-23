package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.entity.SignOutDept;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.repository.jpa.SignDeptInfoRepository;
import net.risesoft.repository.jpa.SignOutDeptRepository;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SignDeptInfoServiceImpl implements SignDeptInfoService {

    private final SignDeptInfoRepository signDeptInfoRepository;

    private final SignOutDeptRepository signOutDeptRepository;

    private final DepartmentApi departmentApi;

    @Override
    @Transactional
    public void deleteById(String id) {
        signDeptInfoRepository.deleteById(id);
    }

    @Override
    public List<SignDeptInfo> getSignDeptList(String processInstanceId, String deptType) {
        return signDeptInfoRepository.findByProcessInstanceIdAndDeptTypeOrderByOrderIndexAsc(processInstanceId,
            deptType);
    }

    @Override
    public Page<SignOutDept> listAll(String name, Integer page, Integer rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "deptOrder");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return signOutDeptRepository.findAll(new Specification<SignOutDept>() {
            @Override
            public Predicate toPredicate(Root<SignOutDept> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(name)) {
                    list.add(builder.like(root.get("deptName"), "%" + name + "%"));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
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
    public void saveOrUpdate(SignOutDept info) {
        if (StringUtils.isBlank(info.getDeptId())) {
            info.setDeptId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            Integer maxDeptOrder = signOutDeptRepository.getMaxDeptOrder();
            info.setDeptOrder(maxDeptOrder == null ? 1 : maxDeptOrder + 1);
        }
        signOutDeptRepository.save(info);
    }

    @Override
    @Transactional
    public void saveSignDept(String processInstanceId, String deptType, String deptIds) {
        String[] split = deptIds.split(",");
        List<String> split1 = Arrays.asList(split);
        signDeptInfoRepository.deleteByProcessInstanceIdAndDeptTypeAndDeptIdNotIn(processInstanceId, deptType, split1);
        for (int i = 0; i < split.length; i++) {
            String deptId = split[i];
            SignDeptInfo signDeptInfo =
                signDeptInfoRepository.findByProcessInstanceIdAndDeptTypeAndDeptId(processInstanceId, deptType, deptId);
            if (signDeptInfo != null) {
                signDeptInfo.setOrderIndex(i + 1);
            } else {
                signDeptInfo = new SignDeptInfo();
                signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                signDeptInfo.setInputPerson(Y9LoginUserHolder.getOrgUnit().getName());
                signDeptInfo.setInputPersonId(Y9LoginUserHolder.getOrgUnitId());
                signDeptInfo.setOrderIndex(i + 1);
                signDeptInfo.setDeptId(deptId);
                signDeptInfo.setRecordTime(new Date());
                if (deptType.equals("0")) {
                    Department department = departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
                    signDeptInfo.setDeptName(department != null ? department.getName() : "部门不存在");
                } else {
                    SignOutDept signOutDept = signOutDeptRepository.findById(deptId).orElse(null);
                    signDeptInfo.setDeptName(signOutDept != null ? signOutDept.getDeptName() : "单位不存在");
                }
                signDeptInfo.setProcessInstanceId(processInstanceId);
                signDeptInfo.setDeptType(deptType);
            }
            signDeptInfoRepository.save(signDeptInfo);
        }
    }

    @Override
    @Transactional
    public void saveSignDeptInfo(String id, String userName) {
        SignDeptInfo signDeptInfo = signDeptInfoRepository.findById(id).orElse(null);
        if (signDeptInfo != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            signDeptInfo.setUserName(userName);
            signDeptInfo.setSignDate(simpleDateFormat.format(new Date()));
            signDeptInfoRepository.save(signDeptInfo);
        }
    }
}
