package net.risesoft.service.dynamicrole.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前部门主管领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public class CurrentDeptManagers extends AbstractDynamicRoleMember {

    @Autowired
    private DepartmentApi departmentManager;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String deptId = Y9LoginUserHolder.getDeptId();
        return departmentManager
            .listDepartmentPropOrgUnits(tenantId, deptId, DepartmentPropCategoryEnum.MANAGER.getValue()).getData();
    }
}
