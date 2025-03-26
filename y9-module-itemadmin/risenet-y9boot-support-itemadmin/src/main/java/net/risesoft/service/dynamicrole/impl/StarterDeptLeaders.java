package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前流程的启动人员所在部门领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
@RequiredArgsConstructor
public class StarterDeptLeaders extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final DepartmentApi departmentApi;

    private final RuntimeApi runtimeApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            String userId = processInstance.getStartUserId();
            if (StringUtils.isNotEmpty(userId)) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(),
                    DepartmentPropCategoryEnum.LEADER.getValue(), false).getData();
                orgUnitList.addAll(leaders);
            }
        } else {
            String userId = Y9LoginUserHolder.getOrgUnitId();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(),
                DepartmentPropCategoryEnum.LEADER.getValue(), false).getData();
            orgUnitList.addAll(leaders);
        }
        return orgUnitList;
    }
}