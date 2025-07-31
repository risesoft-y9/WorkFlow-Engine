package net.risesoft.service.dynamicrole.impl.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 根据当前任务受让人对角色中的人员进行筛选
 *
 * @author qinman
 * @date 2025/04/21
 */
@Service
@RequiredArgsConstructor
public class RoleFilterTask4SubProcess extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final HistoricTaskApi historicTaskApi;

    private final TaskApi taskApi;

    private final RoleApi roleApi;

    private final PositionRoleApi positionRoleApi;

    @Override
    public List<Position> getPositionList(String taskId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        String roleId = dynamicRole.getRoleId();
        List<Position> orgUnitList =
            positionRoleApi.listPositionsByRoleId(Y9LoginUserHolder.getTenantId(), roleId).getData();
        String assignee = "";
        if (StringUtils.isNotBlank(taskId)) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            assignee = task.getAssignee();
        }
        if (StringUtils.isNotBlank(assignee)) {
            userId = assignee;
        }
        OrgUnit personOrPosition = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Integer ranges = dynamicRole.getRanges().getValue();
        switch (ranges) {
            case 1:
                // 和[当前人或者子流程启动人]同部门
                orgUnitList =
                    orgUnitList.stream().filter(orgUnit -> orgUnit.getParentId().equals(personOrPosition.getParentId()))
                        .collect(Collectors.toList());
                break;
            case 2:
                // 和[当前人或者子流程启动人]同委办局
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, userId).getData();
                orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(bureau.getId()))
                    .collect(Collectors.toList());
                break;
            default:
                break;
        }
        return orgUnitList;
    }
}