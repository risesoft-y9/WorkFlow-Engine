package net.risesoft.service.dynamicrole.impl.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

/**
 * 静态角色过滤器-特殊:静态角色不包含当前人，则找角色内本处室人，包含当前人，则找本委办局内的人
 *
 * @author qinman
 * @date 2025/04/01
 */
@Service
@RequiredArgsConstructor
public class RoleFilterSpecial extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    private final RoleApi roleApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        OrgUnit currentOrgUnit = orgUnitApi.getOrgUnit(tenantId, userId).getData();
        if (dynamicRole.isUseProcessInstanceId()) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            userId = processInstance.getStartUserId();
        }
        OrgUnit personOrPosition = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, userId).getData();
        String roleId = dynamicRole.getRoleId();
        List<OrgUnit> orgUnitList = roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData();
        boolean match = orgUnitList.stream().anyMatch(orgUnit -> orgUnit.getId().equals(currentOrgUnit.getId()));
        if (match) {
            // 和[当前人或者流程启动人]同委办局
            orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(bureau.getId()))
                .collect(Collectors.toList());
        } else {
            // 和[当前人或者流程启动人]同部门
            orgUnitList =
                orgUnitList.stream().filter(orgUnit -> orgUnit.getParentId().equals(personOrPosition.getParentId()))
                    .collect(Collectors.toList());
        }
        orgUnitList.remove(currentOrgUnit);
        return orgUnitList;
    }
}