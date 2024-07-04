package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.RuntimeApiClient;

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

    private final PositionApi positionManager;

    private final DepartmentApi departmentApi;

    private final RuntimeApiClient runtimeManager;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance =
                runtimeManager.getProcessInstance(tenantId, processInstanceId).getData();
            String userIdAndDeptId = processInstance.getStartUserId();
            if (StringUtils.isNotEmpty(userIdAndDeptId)) {
                String userId = userIdAndDeptId.split(":")[0];
                OrgUnit orgUnit = positionManager.get(tenantId, userId).getData();
                List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(),
                    DepartmentPropCategoryEnum.LEADER.getValue()).getData();
                orgUnitList.addAll(leaders);
            }
        } else {
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = positionManager.get(tenantId, positionId).getData();
            List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, position.getParentId(),
                DepartmentPropCategoryEnum.LEADER.getValue()).getData();
            orgUnitList.addAll(leaders);
        }
        return orgUnitList;
    }
}