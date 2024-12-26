package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 会签部门秘书
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
@RequiredArgsConstructor
public class SignDeptSecretary extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final DepartmentApi departmentApi;

    private final RuntimeApi runtimeApi;

    private final SignDeptInfoService signDeptInfoService;

    private final OfficeDoneInfoService officeDoneInfoService;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
            OrgUnit position = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            List<SignDeptInfo> list = signDeptInfoService.getSignDeptList(officeDoneInfo.getProcessSerialNumber(), "0");
            for (SignDeptInfo signDeptInfo : list) {
                if (position.getDn().split(",o=")[0].contains(signDeptInfo.getDeptName())) {// 排除本司局
                    continue;
                }
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, signDeptInfo.getDeptId()).getData();
                if (orgUnit != null) {
                    orgUnitList.add(orgUnit);
                    List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getId(),
                        DepartmentPropCategoryEnum.SECRETARY.getValue(), false).getData();
                    for (OrgUnit leader : leaders) {
                        leader.setParentId(orgUnit.getId());
                        orgUnitList.add(leader);
                    }
                }
            }
        }
        return orgUnitList;
    }
}