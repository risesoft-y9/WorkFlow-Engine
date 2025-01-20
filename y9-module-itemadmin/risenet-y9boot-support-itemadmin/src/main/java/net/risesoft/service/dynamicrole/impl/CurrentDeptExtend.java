package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前人/流程启动人所在委办局下面的综合部
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentDeptExtend extends AbstractDynamicRoleMember {

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    @Override
    public Department getDepartment(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Department department = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            orgUnitId = processInstance.getStartUserId();
        }
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, orgUnitId).getData();
        for (Department datum : departmentApi.listRecursivelyByParentId(tenantId, bureau.getId()).getData()) {
            if (StringUtils.isNotBlank(datum.getDeptGivenName()) && "综合部".equals(datum.getDeptGivenName())) {
                department = datum;
            }
        }
        return department;
    }

    @Override
    public List<OrgUnit> getOrgUnitList() {
        List<OrgUnit> orgUnitList = new ArrayList<>();
        orgUnitList.add(getDepartment(""));
        return orgUnitList;
    }

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        List<OrgUnit> orgUnitList = new ArrayList<>();
        orgUnitList.add(getDepartment(processInstanceId));
        return orgUnitList;
    }

}
