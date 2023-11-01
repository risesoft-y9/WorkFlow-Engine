package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前部门(只是部门)
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public class CurrentDept extends AbstractDynamicRoleMember {

    @Autowired
    private DepartmentApi departmentManager;

    @Override
    public Department getDepartment() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String deptId = Y9LoginUserHolder.getDeptId();
        return departmentManager.getDepartment(tenantId, deptId).getData();
    }

    @Override
    public List<OrgUnit> getOrgUnitList() {
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        orgUnitList.add(getDepartment());
        return orgUnitList;
    }

}
