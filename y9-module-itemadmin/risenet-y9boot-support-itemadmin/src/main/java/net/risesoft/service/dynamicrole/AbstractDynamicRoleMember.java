package net.risesoft.service.dynamicrole;

import java.util.List;

import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.*;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public abstract class AbstractDynamicRoleMember {
    public Department getDepartment() {
        return null;
    }

    public Department getDepartment(String param) {
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

    public List<OrgUnit> getOrgUnitList(String param, DynamicRole dynamicRole) {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public List<Person> getPersonList() {
        return null;
    }

    public Position getPosition() {
        return null;
    }

    public Position getPosition(String param) {
        return null;
    }

    public List<Position> getPositionList(String param, DynamicRole dynamicRole) {
        return null;
    }

    public List<Position> getPositionList() {
        return null;
    }
}
