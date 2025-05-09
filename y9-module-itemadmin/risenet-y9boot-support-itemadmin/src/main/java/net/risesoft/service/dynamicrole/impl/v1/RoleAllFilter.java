package net.risesoft.service.dynamicrole.impl.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 静态角色过滤器，绑定角色后，根据委办局不同，去查询角色里的所有人员
 */
@Service
@RequiredArgsConstructor
public class RoleAllFilter extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    private final RoleApi roleApi;

    private final PositionRoleApi positionRoleApi;

    @Override
    public List<Position> getPositionList(String processInstanceId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        if (dynamicRole.isUseProcessInstanceId()) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            userId = processInstance.getStartUserId();
        }
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, userId).getData();
        String roleId = dynamicRole.getRoleId();
        List<Position> orgUnitList =
            positionRoleApi.listPositionsByRoleId(Y9LoginUserHolder.getTenantId(), roleId).getData();
        // List<OrgUnit> orgUnitList = roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData();
        orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(bureau.getId()))
            .collect(Collectors.toList());
        return orgUnitList;
    }
}