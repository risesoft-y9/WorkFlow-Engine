package net.risesoft.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.entity.SignOutDept;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.org.Department;
import net.risesoft.repository.jpa.SignDeptInfoRepository;
import net.risesoft.repository.jpa.SignOutDeptRepository;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.form.FormDataService;
import net.risesoft.util.Y9DateTimeUtils;
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
    public void addSignDept(String processSerialNumber, String deptType, String deptIds) {
        String[] deptIdArr = deptIds.split(",");
        List<Department> deptList =
            departmentApi.listByIds(Y9LoginUserHolder.getTenantId(), Arrays.asList(deptIdArr)).getData();
        deptList.forEach(dept -> {
            if (null == signDeptInfoRepository.findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber,
                deptType, dept.getId())) {
                SignDeptInfo signDeptInfo = new SignDeptInfo();
                signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                signDeptInfo.setInputPerson(Y9FlowableHolder.getOrgUnit().getName());
                signDeptInfo.setInputPersonId(Y9FlowableHolder.getOrgUnitId());
                signDeptInfo.setOrderIndex(dept.getTabIndex());
                signDeptInfo.setDeptId(dept.getId());
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

    @Override
    @Transactional
    public void deleteById(String id) {
        Optional<SignDeptInfo> sdiOptional = signDeptInfoRepository.findById(id);
        if (sdiOptional.isPresent()) {
            SignDeptInfo sdi = sdiOptional.get();
            if ("0".equals(sdi.getDeptType())) {
                List<SignDeptDetail> doneList = signDeptDetailService
                    .findByProcessSerialNumberAndStatus(sdi.getProcessSerialNumber(), SignDeptDetailStatusEnum.DONE);
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

    private void refresh(String processSerialNumber) {
        // 委内抄送单位
        String alias = "fw", columnName = "wncsdw";
        String aliasColumnName = alias + "." + columnName;
        Map<String, Object> map = new HashMap<>(1);
        StringBuffer deptNames = new StringBuffer();
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        String starter = null == processParam ? Y9FlowableHolder.getOrgUnit().getId() : processParam.getStartor();
        Department bureau = (Department)orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), starter).getData();
        deptNames.append(StringUtils.isNotBlank(bureau.getAliasName()) ? bureau.getAliasName() : bureau.getName());
        List<SignDeptInfo> signDeptList = this.getSignDeptList(processSerialNumber, "0");
        signDeptList.forEach(signDeptInfo -> deptNames.append(",").append(signDeptInfo.getDeptName()));
        map.put(aliasColumnName, deptNames);
        formDataService.updateFormData(processSerialNumber, Y9JsonUtil.writeValueAsString(map));
    }

    @Override
    @Transactional
    public void saveSignDept(String processSerialNumber, String deptType, String deptIds, String tzsDeptId) {
        try {
            String[] deptIdArr = deptIds.split(",");
            List<String> deptIdList = Arrays.asList(deptIdArr);

            // 处理已完成的会签部门
            if ("0".equals(deptType)) {
                handleDoneSignDept(processSerialNumber, deptIdList);
            }

            // 删除不在列表中的部门信息
            signDeptInfoRepository.deleteByProcessSerialNumberAndDeptTypeAndDeptIdNotIn(processSerialNumber, deptType,
                deptIdList);

            // 根据部门类型处理保存逻辑
            if ("0".equals(deptType)) {
                saveInternalSignDept(processSerialNumber, deptType, deptIdList, tzsDeptId);
            } else {
                saveExternalSignDept(processSerialNumber, deptType, deptIdList);
            }
        } catch (Exception e) {
            LOGGER.error("保存会签部门信息失败，processSerialNumber: {}", processSerialNumber, e);
            throw new RuntimeException("保存会签部门信息失败", e);
        }
    }

    /**
     * 处理已完成的会签部门
     */
    private void handleDoneSignDept(String processSerialNumber, List<String> deptIdList) {
        List<SignDeptDetail> doneList = signDeptDetailService.findByProcessSerialNumberAndStatus(processSerialNumber,
            SignDeptDetailStatusEnum.DONE);

        for (SignDeptDetail detail : doneList) {
            if (!deptIdList.contains(detail.getDeptId())) {
                deptIdList.add(detail.getDeptId());
            }
        }
    }

    /**
     * 保存内部会签部门
     */
    private void saveInternalSignDept(String processSerialNumber, String deptType, List<String> deptIdList,
        String tzsDeptId) {
        try {
            List<Department> deptList = departmentApi.listByIds(Y9LoginUserHolder.getTenantId(), deptIdList).getData();

            for (Department dept : deptList) {
                processInternalSignDept(processSerialNumber, deptType, dept, tzsDeptId);
            }

            refresh(processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("保存内部会签部门失败，processSerialNumber: {}", processSerialNumber, e);
            throw new RuntimeException("保存内部会签部门失败", e);
        }
    }

    /**
     * 处理单个内部会签部门
     */
    private void processInternalSignDept(String processSerialNumber, String deptType, Department dept,
        String tzsDeptId) {
        SignDeptInfo existingSignDept = signDeptInfoRepository
            .findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber, deptType, dept.getId());

        if (existingSignDept != null) {
            return; // 已存在，跳过
        }

        SignDeptInfo signDeptInfo = createInternalSignDeptInfo(processSerialNumber, deptType, dept, tzsDeptId);
        signDeptInfoRepository.save(signDeptInfo);
    }

    /**
     * 创建内部会签部门信息
     */
    private SignDeptInfo createInternalSignDeptInfo(String processSerialNumber, String deptType, Department dept,
        String tzsDeptId) {
        SignDeptInfo signDeptInfo = new SignDeptInfo();
        signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        signDeptInfo.setInputPerson(Y9FlowableHolder.getOrgUnit().getName());
        signDeptInfo.setInputPersonId(Y9FlowableHolder.getOrgUnitId());
        signDeptInfo.setOrderIndex(dept.getTabIndex());
        signDeptInfo.setDeptId(dept.getId());

        // 设置部门名称
        Department department = getDepartmentInfo(dept.getId());
        signDeptInfo.setDeptName(department == null ? "部门不存在"
            : StringUtils.isBlank(department.getAliasName()) ? department.getName() : department.getAliasName());

        signDeptInfo.setProcessSerialNumber(processSerialNumber);
        signDeptInfo.setDeptType(deptType);
        signDeptInfo.setDisplayDeptId(dept.getId());
        signDeptInfo.setDisplayDeptName(signDeptInfo.getDeptName());

        // 处理tzs部门显示名称
        if (StringUtils.isNotBlank(tzsDeptId) && dept.getId().equals(tzsDeptId)) {
            updateTzsDisplayInfo(signDeptInfo);
        }

        return signDeptInfo;
    }

    /**
     * 获取部门信息
     */
    private Department getDepartmentInfo(String deptId) {
        try {
            return departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
        } catch (Exception e) {
            LOGGER.warn("获取部门信息失败，deptId: {}", deptId, e);
            return null;
        }
    }

    /**
     * 更新tzs显示信息
     */
    private void updateTzsDisplayInfo(SignDeptInfo signDeptInfo) {
        try {
            Department bureau =
                (Department)orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getOrgUnitId())
                    .getData();
            if (bureau != null) {
                signDeptInfo.setDisplayDeptId(bureau.getId());
                signDeptInfo.setDisplayDeptName(
                    StringUtils.isBlank(bureau.getAliasName()) ? bureau.getName() : bureau.getAliasName());
            }
        } catch (Exception e) {
            LOGGER.warn("更新tzs显示信息失败", e);
        }
    }

    /**
     * 保存外部会签部门
     */
    private void saveExternalSignDept(String processSerialNumber, String deptType, List<String> deptIdList) {
        Integer index = 1;

        for (String deptId : deptIdList) {
            SignDeptInfo signDeptInfo = signDeptInfoRepository
                .findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber, deptType, deptId);

            if (signDeptInfo == null) {
                signDeptInfo = createExternalSignDeptInfo(processSerialNumber, deptType, deptId, index);
            }

            signDeptInfo.setOrderIndex(index);
            signDeptInfoRepository.save(signDeptInfo);
            index++;
        }
    }

    /**
     * 创建外部会签部门信息
     */
    private SignDeptInfo createExternalSignDeptInfo(String processSerialNumber, String deptType, String deptId,
        Integer index) {
        SignDeptInfo signDeptInfo = new SignDeptInfo();
        signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        signDeptInfo.setInputPerson(Y9FlowableHolder.getOrgUnit().getName());
        signDeptInfo.setInputPersonId(Y9FlowableHolder.getOrgUnitId());
        signDeptInfo.setOrderIndex(index);
        signDeptInfo.setDeptId(deptId);
        // 设置部门名称
        Optional<SignOutDept> signOutDept = signOutDeptRepository.findById(deptId);
        signDeptInfo.setDeptName(signOutDept.isPresent() ? signOutDept.get().getDeptName() : deptId);
        signDeptInfo.setProcessSerialNumber(processSerialNumber);
        signDeptInfo.setDeptType(deptType);
        return signDeptInfo;
    }

    @Override
    @Transactional
    public void saveSignDeptInfo(String id, String userName) {
        SignDeptInfo signDeptInfo = signDeptInfoRepository.findById(id).orElse(null);
        if (signDeptInfo != null) {
            signDeptInfo.setUserName(userName);
            signDeptInfo.setSignDate(Y9DateTimeUtils.formatCurrentDate());
            signDeptInfoRepository.save(signDeptInfo);
        }
    }

    @Override
    @Transactional
    public void updateSignDept(String processSerialNumber, String positionId, String type, String tzsDeptId) {
        try {
            SignDeptInfo signDeptInfo = signDeptInfoRepository
                .findByProcessSerialNumberAndDeptTypeAndDeptId(processSerialNumber, "0", tzsDeptId);

            if ("0".equals(type)) {
                handleTypeZero(signDeptInfo);
            } else if ("1".equals(type)) {
                handleTypeOne(processSerialNumber, positionId, tzsDeptId, signDeptInfo);
            }

            // 更新字段
            refresh(processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("更新会签部门信息失败，processSerialNumber: {}", processSerialNumber, e);
            throw new RuntimeException("更新会签部门信息失败", e);
        }
    }

    /**
     * 处理类型0的情况：tzs显示tzs司局名称
     */
    private void handleTypeZero(SignDeptInfo signDeptInfo) {
        if (signDeptInfo != null) {
            signDeptInfo.setDisplayDeptId(signDeptInfo.getDeptId());
            signDeptInfo.setDisplayDeptName(signDeptInfo.getDeptName());
            signDeptInfoRepository.save(signDeptInfo);
        }
    }

    /**
     * 处理类型1的情况：tzs显示起草司局名称
     */
    private void handleTypeOne(String processSerialNumber, String positionId, String tzsDeptId,
        SignDeptInfo signDeptInfo) {
        String actualPositionId = getPositionId(processSerialNumber, positionId);

        try {
            Department bureau =
                (Department)orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), actualPositionId).getData();

            SignDeptInfo updatedSignDeptInfo = signDeptInfo;
            if (signDeptInfo == null) {
                // 创建新的会签部门信息
                updatedSignDeptInfo = createNewSignDeptInfo(processSerialNumber, tzsDeptId);
            }

            // 更新显示信息
            updateDisplayInfo(updatedSignDeptInfo, bureau);
            signDeptInfoRepository.save(updatedSignDeptInfo);
        } catch (Exception e) {
            LOGGER.error("处理类型1会签部门信息失败，processSerialNumber: {}", processSerialNumber, e);
            throw new RuntimeException("处理类型1会签部门信息失败", e);
        }
    }

    /**
     * 获取实际的岗位ID
     */
    private String getPositionId(String processSerialNumber, String positionId) {
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        return (processParam != null) ? processParam.getStartor() : positionId;
    }

    /**
     * 创建新的会签部门信息
     */
    private SignDeptInfo createNewSignDeptInfo(String processSerialNumber, String tzsDeptId) {
        try {
            Department tzsbureau = departmentApi.get(Y9LoginUserHolder.getTenantId(), tzsDeptId).getData();

            SignDeptInfo signDeptInfo = new SignDeptInfo();
            signDeptInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            signDeptInfo.setInputPerson(Y9FlowableHolder.getOrgUnit().getName());
            signDeptInfo.setInputPersonId(Y9FlowableHolder.getOrgUnitId());
            signDeptInfo.setOrderIndex(tzsbureau.getTabIndex());
            signDeptInfo.setDeptId(tzsbureau.getId());
            signDeptInfo.setDeptName(
                StringUtils.isBlank(tzsbureau.getAliasName()) ? tzsbureau.getName() : tzsbureau.getAliasName());
            signDeptInfo.setProcessSerialNumber(processSerialNumber);
            signDeptInfo.setDeptType("0");

            return signDeptInfo;
        } catch (Exception e) {
            LOGGER.error("创建新的会签部门信息失败，processSerialNumber: {}", processSerialNumber, e);
            throw new RuntimeException("创建新的会签部门信息失败", e);
        }
    }

    /**
     * 更新显示信息
     */
    private void updateDisplayInfo(SignDeptInfo signDeptInfo, Department bureau) {
        if (bureau != null) {
            signDeptInfo.setDisplayDeptId(bureau.getId());
            signDeptInfo.setDisplayDeptName(
                StringUtils.isBlank(bureau.getAliasName()) ? bureau.getName() : bureau.getAliasName());
        }
    }
}
