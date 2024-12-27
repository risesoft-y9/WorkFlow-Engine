package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前部门领导，如果当前人是部门领导，则找当前部门所在委办局下的其他部门的领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentDeptLeadersExtend extends AbstractDynamicRoleMember {

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(tenantId, orgUnit.getParentId(),
            DepartmentPropCategoryEnum.LEADER.getValue(), false).getData();
        boolean isLeader = leaders.stream().anyMatch(leader -> leader.getId().equals(orgUnitId));
        if (isLeader) {
            leaders.clear();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, orgUnitId).getData();
            List<OrgUnit> deptList =
                orgUnitApi.getSubTree(tenantId, bureau.getId(), OrgTreeTypeEnum.TREE_TYPE_DEPT).getData();
            deptList.stream().filter(dept -> !dept.getId().equals(orgUnit.getParentId())).forEach(dept -> {
                List<OrgUnit> deptLeaders = departmentApi.listDepartmentPropOrgUnits(tenantId, dept.getId(),
                    DepartmentPropCategoryEnum.LEADER.getValue(), false).getData();
                leaders.addAll(deptLeaders);
            });
        }
        return leaders;
    }

}
