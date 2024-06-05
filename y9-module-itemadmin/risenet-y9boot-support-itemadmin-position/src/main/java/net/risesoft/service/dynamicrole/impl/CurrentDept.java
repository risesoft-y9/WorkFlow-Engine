package net.risesoft.service.dynamicrole.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前部门(只是部门)
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentDept extends AbstractDynamicRoleMember {

    private final DepartmentApi departmentManager;

    private final PositionApi positionApi;

    @Override
    public Department getDepartment() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Position position = positionApi.get(tenantId, positionId).getData();
        return departmentManager.get(tenantId, position.getParentId()).getData();
    }

    @Override
    public List<OrgUnit> getOrgUnitList() {
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        orgUnitList.add(getDepartment());
        return orgUnitList;
    }

}
