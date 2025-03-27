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
 * 当前流程的启动人员所在部门领导
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
@RequiredArgsConstructor
public class RoleFilter extends AbstractDynamicRoleMember {

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
        Integer ranges = dynamicRole.getRanges();
        switch (ranges) {
            case 1:
                // 和[当前人或者流程启动人]同部门
                orgUnitList =
                    orgUnitList.stream().filter(orgUnit -> orgUnit.getParentId().equals(personOrPosition.getParentId()))
                        .collect(Collectors.toList());
                break;
            case 2:
                // 和[当前人或者流程启动人]同委办局
                orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().equals(bureau.getId()))
                    .collect(Collectors.toList());
                break;
            default:
                break;
        }
        orgUnitList.remove(currentOrgUnit);
        return orgUnitList;
    }
}