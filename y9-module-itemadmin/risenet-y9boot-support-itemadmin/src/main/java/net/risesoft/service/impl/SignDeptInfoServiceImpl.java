package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.entity.SignOutDept;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.repository.jpa.SignDeptInfoRepository;
import net.risesoft.repository.jpa.SignOutDeptRepository;
import net.risesoft.service.FormDataService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

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

    private final SignDeptDetailService signDeptDetailService;

    private final FormDataService formDataService;

    private final ProcessParamService processParamService;

    @Override
    @Transactional
    public void deleteById(String id) {
        Optional<SignDeptInfo> sdiOptional = signDeptInfoRepository.findById(id);
        if (sdiOptional.isPresent()) {
            SignDeptInfo sdi = sdiOptional.get();
            if ("0".equals(sdi.getDeptType())) {
                List<SignDeptDetail> doneList = signDeptDetailService.findByProcessSerialNumberAndStatus(
                    sdi.getProcessSerialNumber(), SignDeptDetailStatusEnum.DONE.getValue());
                boolean match = doneList.stream().anyMatch(detail -> detail.getDeptId().equals(sdi.getDeptId()));
                if (!match) {
                    signDeptInfoRepository.deleteById(id);
                    refresh(sdi.getProcessSerialNumber());
                }
            } else {
                signDeptInfoRepository.deleteById(id);
            }
        }
    }

    @Override
    public List<SignDeptInfo> getSignDeptList(String processSerialNumber, String deptType) {
        return signDeptInfoRepository.findByProcessSerialNumberAndDeptTypeOrderByOrderIndexAsc(processSerialNumber,
            deptType);
    }

    @Override
    @Transactional
    public void saveSignDept(String processSerialNumber, String deptType, String deptIds, String tzsDeptId) {
        String[] deptIdArr = deptIds.split(",");
        List<String> deptIdList = Arrays.asList(deptIdArr);
        if ("0".equals(deptType)) {
            List<SignDeptDetail> doneList = signDeptDetailService
                .findByProcessSerialNumberAndStatus(processSerialNumber, SignDeptDetailStatusEnum.DONE.getValue());
            doneList.forEach(detail -> {
                if (!deptIdList.contains(detail.getDeptId())) {
                    deptIdList.add(detail.getDeptId());
                }
            });
        }
        signDeptInfoRepository.deleteByProcessSerialNumberAndDeptTypeAndDeptIdNotIn(processSerialNumber, deptType,
            deptIdList);
        if ("0".equals(deptType)) {
            List<Department> deptList = departmentApi.listByIds(Y9LoginUserHolder.getTenantId(), deptIdList).getData();
            deptList.forEach(dept -> {
                if (null == signDeptInfoRepository.findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber,
                    deptType, dept.getId())) {
                    SignDeptInfo signDeptInfo = new SignDeptInfo();
                    signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    signDeptInfo.setInputPerson(Y9LoginUserHolder.getOrgUnit().getName());
                    signDeptInfo.setInputPersonId(Y9LoginUserHolder.getOrgUnitId());
                    signDeptInfo.setOrderIndex(dept.getTabIndex());
                    signDeptInfo.setDeptId(dept.getId());
                    signDeptInfo.setRecordTime(new Date());
                    Department department = departmentApi.get(Y9LoginUserHolder.getTenantId(), dept.getId()).getData();
                    signDeptInfo
                        .setDeptName(department == null ? "部门不存在" : StringUtils.isBlank(department.getAliasName())
                            ? department.getName() : department.getAliasName());
                    signDeptInfo.setProcessSerialNumber(processSerialNumber);
                    signDeptInfo.setDeptType(deptType);
                    signDeptInfo.setDisplayDeptName(signDeptInfo.getDeptName());
                    if (StringUtils.isNotBlank(tzsDeptId) && dept.getId().equals(tzsDeptId)) {
                        // 需要将显示名称改为原司局单位名称
                        Department bureau = (Department)orgUnitApi
                            .getBureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getOrgUnitId()).getData();
                        signDeptInfo.setDisplayDeptId(bureau.getId());
                        signDeptInfo.setDisplayDeptName(
                            StringUtils.isBlank(bureau.getAliasName()) ? bureau.getName() : bureau.getAliasName());
                    }
                    signDeptInfoRepository.save(signDeptInfo);

                }
            });
            refresh(processSerialNumber);
        } else {
            Integer index = 1;
            deptIdList.forEach(deptId -> {
                SignDeptInfo signDeptInfo = signDeptInfoRepository
                .findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber, deptType, deptId);
                if (signDeptInfo == null) {
                    signDeptInfo = new SignDeptInfo();
                    signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    signDeptInfo.setInputPerson(Y9LoginUserHolder.getOrgUnit().getName());
                    signDeptInfo.setInputPersonId(Y9LoginUserHolder.getOrgUnitId());
                    signDeptInfo.setOrderIndex(index);
                    signDeptInfo.setDeptId(deptId);
                    signDeptInfo.setRecordTime(new Date());
                    Optional<SignOutDept> signOutDept = signOutDeptRepository.findById(deptId);
                    signDeptInfo.setDeptName(signOutDept.isPresent() ? signOutDept.get().getDeptName() : "单位不存在");
                    signDeptInfo.setProcessSerialNumber(processSerialNumber);
                    signDeptInfo.setDeptType(deptType);
                }
                signDeptInfo.setOrderIndex(index);
                signDeptInfoRepository.save(signDeptInfo);
            });
        }
    }

    @Override
    @Transactional
    public void addSignDept(String processSerialNumber, String deptType, String deptIds) {
        String[] deptIdArr = deptIds.split(",");
        List<Department> deptList =
            departmentApi.listByIds(Y9LoginUserHolder.getTenantId(), Arrays.asList(deptIdArr)).getData();
        deptList.forEach(dept -> {
            if (null == signDeptInfoRepository.findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber,
                deptType, dept.getId())) {
                SignDeptInfo signDeptInfo = new SignDeptInfo();
                signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                signDeptInfo.setInputPerson(Y9LoginUserHolder.getOrgUnit().getName());
                signDeptInfo.setInputPersonId(Y9LoginUserHolder.getOrgUnitId());
                signDeptInfo.setOrderIndex(dept.getTabIndex());
                signDeptInfo.setDeptId(dept.getId());
                signDeptInfo.setRecordTime(new Date());
                Department department = departmentApi.get(Y9LoginUserHolder.getTenantId(), dept.getId()).getData();
                signDeptInfo.setDeptName(department == null ? "部门不存在" : StringUtils.isBlank(department.getAliasName())
                    ? department.getName() : department.getAliasName());
                signDeptInfo.setProcessSerialNumber(processSerialNumber);
                signDeptInfo.setDeptType(deptType);
                signDeptInfoRepository.save(signDeptInfo);

            }
        });
        refresh(processSerialNumber);
    }

    private void refresh(String processSerialNumber) {
        // 委内抄送单位
        String alias = "fw", columnName = "wncsdw";
        String aliasColumnName = alias + "." + columnName;
        Map<String, Object> map = new HashMap<>(1);
        StringBuffer deptNames = new StringBuffer();
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        String starter = null == processParam ? Y9LoginUserHolder.getOrgUnit().getId() : processParam.getStartor();
        Department bureau = (Department)orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), starter).getData();
        deptNames.append(StringUtils.isNotBlank(bureau.getAliasName()) ? bureau.getAliasName() : bureau.getName())
            .append("(3)");
        List<SignDeptInfo> signDeptList = this.getSignDeptList(processSerialNumber, "0");
        signDeptList.forEach(signDeptInfo -> deptNames.append(",").append(signDeptInfo.getDeptName()).append("(3)"));
        map.put(aliasColumnName, deptNames);
        formDataService.updateFormData(processSerialNumber, Y9JsonUtil.writeValueAsString(map));
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
