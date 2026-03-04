package net.risesoft.service.dynamicrole;

import java.util.Collections;
import java.util.List;

import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;

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
        return Collections.emptyList();
    }

    public List<Department> getDeptList() {
        return Collections.emptyList();
    }

    public List<Group> getGroupList() {
        return Collections.emptyList();
    }

    public Organization getOrg() {
        return null;
    }

    public List<OrgUnit> getOrgUnitList() {
        return Collections.emptyList();
    }

    public List<OrgUnit> getOrgUnitList(String param) {
        return Collections.emptyList();
    }

    public List<OrgUnit> getOrgUnitList(String param, DynamicRole dynamicRole) {
        return Collections.emptyList();
    }

    public Person getPerson() {
        return null;
    }

    public List<Person> getPersonList() {
        return List.of();
    }

    public Position getPosition() {
        return null;
    }

    public Position getPosition(String param) {
        return null;
    }

    public List<Position> getPositionList(String param, DynamicRole dynamicRole) {
        return List.of();
    }

    public List<Position> getPositionList() {
        return Collections.emptyList();
    }
}
