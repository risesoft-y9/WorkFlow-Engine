package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Position;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前部门领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
public class CurrentDeptLeaders extends AbstractDynamicRoleMember {

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private PositionApi positionApi;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Position position = positionApi.getPosition(tenantId, positionId);
        return departmentManager.listLeaders(tenantId, position.getParentId());
    }

}
