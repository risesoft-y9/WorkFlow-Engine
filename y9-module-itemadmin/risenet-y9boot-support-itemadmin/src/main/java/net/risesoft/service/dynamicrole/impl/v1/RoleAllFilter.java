package net.risesoft.service.dynamicrole.impl.v1;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 静态角色过滤器，绑定角色后，根据委办局不同，去查询角色里的所有人员
 */
@Service
@RequiredArgsConstructor
public class RoleAllFilter  extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    private final RoleApi roleApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        if (dynamicRole.isUseProcessInstanceId()) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            userId = processInstance.getStartUserId();
        }
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, userId).getData();
        String roleId = dynamicRole.getRoleId();
        List<OrgUnit> orgUnitList = roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData();
        orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(bureau.getId())).collect(Collectors.toList());
        return orgUnitList;
    }
}