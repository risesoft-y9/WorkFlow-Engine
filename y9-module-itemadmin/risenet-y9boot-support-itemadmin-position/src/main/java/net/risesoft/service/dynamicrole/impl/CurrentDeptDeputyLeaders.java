package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前部门分管领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentDeptDeputyLeaders extends AbstractDynamicRoleMember {

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        return departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(),
            DepartmentPropCategoryEnum.VICE_LEADER.getValue()).getData();
    }

}
