package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
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

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public void addSignDept(String processSerialNumber, String deptType, String deptIds) {
        String[] deptIdArr = deptIds.split(",");
        Arrays.stream(deptIdArr).forEach(deptId -> {
            SignDeptInfo signDeptInfo = new SignDeptInfo();
            signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            signDeptInfo.setInputPerson(Y9LoginUserHolder.getOrgUnit().getName());
            signDeptInfo.setInputPersonId(Y9LoginUserHolder.getOrgUnitId());
            signDeptInfo.setDeptId(deptId);
            signDeptInfo.setRecordTime(new Date());
            if ("0".equals(deptType)) {
                Department department = departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
                signDeptInfo.setDeptName(department == null ? "部门不存在" : StringUtils.isBlank(department.getAliasName())
                    ? department.getName() : department.getAliasName());
            } else {
                SignOutDept signOutDept = signOutDeptRepository.findById(deptId).orElse(null);
                signDeptInfo.setDeptName(signOutDept != null ? signOutDept.getDeptName() : "单位不存在");
            }
            signDeptInfo.setProcessSerialNumber(processSerialNumber);
            signDeptInfo.setDeptType(deptType);
            Integer maxTabIndex = signDeptInfoRepository.getMaxTabIndex(processSerialNumber, deptType);
            signDeptInfo.setOrderIndex(null == maxTabIndex ? 1 : maxTabIndex + 1);
            signDeptInfoRepository.save(signDeptInfo);
        });
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        signDeptInfoRepository.deleteById(id);
    }

    @Override
    public List<SignDeptInfo> getSignDeptList(String processSerialNumber, String deptType) {
        return signDeptInfoRepository.findByProcessSerialNumberAndDeptTypeOrderByOrderIndexAsc(processSerialNumber,
            deptType);
    }

    @Override
    @Transactional
    public void saveSignDept(String processSerialNumber, String deptType, String deptIds, String tzsDeptId) {
        String[] split = deptIds.split(",");
        List<String> split1 = Arrays.asList(split);
        signDeptInfoRepository.deleteByProcessSerialNumberAndDeptTypeAndDeptIdNotIn(processSerialNumber, deptType,
            split1);
        for (int i = 0; i < split.length; i++) {
            String deptId = split[i];
            SignDeptInfo signDeptInfo = signDeptInfoRepository
                .findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber, deptType, deptId);
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
                signDeptInfo.setDisplayDeptId(deptId);
                if ("0".equals(deptType)) {
                    Department department = departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
                    signDeptInfo
                        .setDeptName(department == null ? "部门不存在" : StringUtils.isBlank(department.getAliasName())
                            ? department.getName() : department.getAliasName());
                    signDeptInfo.setDisplayDeptName(signDeptInfo.getDeptName());
                    if (StringUtils.isNotBlank(tzsDeptId) && deptId.equals(tzsDeptId)) {
                        // 需要将显示名称改为原司局单位名称
                        Department bureau = (Department)orgUnitApi
                            .getBureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getOrgUnitId()).getData();
                        signDeptInfo.setDisplayDeptId(bureau.getId());
                        signDeptInfo.setDisplayDeptName(
                            StringUtils.isBlank(bureau.getAliasName()) ? bureau.getName() : bureau.getAliasName());
                    }
                } else {
                    SignOutDept signOutDept = signOutDeptRepository.findById(deptId).orElse(null);
                    signDeptInfo.setDeptName(signOutDept != null ? signOutDept.getDeptName() : "单位不存在");
                }
                signDeptInfo.setProcessSerialNumber(processSerialNumber);
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
