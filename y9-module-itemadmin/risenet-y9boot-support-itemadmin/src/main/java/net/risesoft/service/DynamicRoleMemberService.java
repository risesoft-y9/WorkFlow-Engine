package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DynamicRoleMemberService {

    /**
     * Description:
     *
     * @param dynamicRole
     * @param processInstanceId
     * @return
     */
    List<OrgUnit> listByDynamicRoleIdAndProcessInstanceId(DynamicRole dynamicRole, String processInstanceId);

    /**
     * Description:
     *
     * @param dynamicRole
     * @param taskId
     * @return
     */
    List<Position> listByDynamicRoleIdAndTaskId(DynamicRole dynamicRole, String taskId);

    List<Position> listPositionByDynamicRoleIdAndProcessInstanceId(DynamicRole dynamicRole, String processInstanceId);
}
