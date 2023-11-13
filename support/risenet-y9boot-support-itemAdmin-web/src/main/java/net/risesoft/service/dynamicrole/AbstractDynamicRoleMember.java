package net.risesoft.service.dynamicrole;

import java.util.List;

import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public abstract class AbstractDynamicRoleMember {
    public Department getDepartment() {
        return null;
    }

    public List<OrgUnit> getDepartmentList() {
        return null;
    }

    public List<Department> getDeptList() {
        return null;
    }

    public List<Group> getGroupList() {
        return null;
    }

    public Organization getOrg() {
        return null;
    }

    public List<OrgUnit> getOrgUnitList() {
        return null;
    }

    public List<OrgUnit> getOrgUnitList(String param) {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public List<OrgUnit> getPersonList() {
        return null;
    }

    public Position getPosition() {
        return null;
    }

    public Position getPosition(String processInstanceId) {
        return null;
    }

    public List<Position> getPositionList() {
        return null;
    }
}
