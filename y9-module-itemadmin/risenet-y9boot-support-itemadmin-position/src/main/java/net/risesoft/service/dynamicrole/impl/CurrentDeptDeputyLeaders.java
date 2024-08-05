package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
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

    private final DepartmentApi departmentManager;

    private final PositionApi positionApi;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Position position = positionApi.get(tenantId, positionId).getData();
        return departmentManager.listDepartmentPropOrgUnits(tenantId, position.getParentId(),
            DepartmentPropCategoryEnum.VICE_LEADER.getValue()).getData();
    }

}
