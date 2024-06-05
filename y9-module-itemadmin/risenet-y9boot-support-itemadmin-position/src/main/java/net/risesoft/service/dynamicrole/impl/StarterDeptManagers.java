package net.risesoft.service.dynamicrole.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import y9.client.rest.processadmin.RuntimeApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前流程的启动人员所在部门主管领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
@RequiredArgsConstructor
public class StarterDeptManagers extends AbstractDynamicRoleMember {

    private final PositionApi positionManager;

    private final DepartmentApi departmentApi;

    private final RuntimeApiClient runtimeManager;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance = runtimeManager.getProcessInstance(tenantId, processInstanceId);
            String userIdAndDeptId = processInstance.getStartUserId();
            if (StringUtils.isNotEmpty(userIdAndDeptId)) {
                String userId = userIdAndDeptId.split(":")[0];
                OrgUnit orgUnit = positionManager.get(tenantId, userId).getData();
                List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(), DepartmentPropCategoryEnum.MANAGER.getValue()).getData();
                orgUnitList.addAll(leaders);
            }
        } else {
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = positionManager.get(tenantId, positionId).getData();
            List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, position.getParentId(), DepartmentPropCategoryEnum.MANAGER.getValue()).getData();
            orgUnitList.addAll(leaders);
        }
        return orgUnitList;
    }
}