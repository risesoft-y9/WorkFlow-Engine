package net.risesoft.service.dynamicrole;

import java.util.List;

import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public abstract class AbstractDynamicRoleMember {
    public List<OrgUnit> getOrgUnitList() {
        return null;
    }

    public List<OrgUnit> getOrgUnitList(String param) {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public Department getDepartment() {
        return null;
    }

    public Organization getOrg() {
        return null;
    }

    public Position getPosition() {
        return null;
    }

    public Position getPosition(String processInstanceId) {
        return null;
    }

    public List<Person> getPersonList() {
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

    public List<Position> getPositionList() {
        return null;
    }
}
