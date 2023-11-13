package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DynamicRoleMemberService {

    /**
     * 
     * Description:
     * 
     * @param dynamicRole
     * @return
     */
    Department getDepartment(DynamicRole dynamicRole);

    /**
     * Description:
     * 
     * @param dynamicRoleId
     * @return
     */
    List<OrgUnit> getOrgUnitList(String dynamicRoleId);

    /**
     * Description:
     * 
     * @param dynamicRoleId
     * @param processInstanceId
     * @return
     */
    List<OrgUnit> getOrgUnitList(String dynamicRoleId, String processInstanceId);
}
