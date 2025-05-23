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
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 静态角色过滤器-特殊-针对子流程:如果是管理员打开的监控在办进行重定向，则显示[子流程流程启动人]同委办局下的人员
 * 如果是普通人员打开的待办发送，判断当前角色是否包含当前人，包含当前人则找角色下同委办局的人，不包含则找当前人同部门下的人员
 * 
 * 针对秘书也是处长的情况，不排除当前人
 * 
 * @author qinman
 * @date 2025/04/01
 */
@Service
@RequiredArgsConstructor
public class RoleFilterSpecial4SubProcessNotRemove extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final RoleApi roleApi;

    private final TaskApi taskApi;

    private final HistoricTaskApi historicTaskApi;

    private final PositionRoleApi positionRoleApi;

    @Override
    public List<Position> getPositionList(String taskId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String currentOrgUnitId = Y9LoginUserHolder.getOrgUnitId();
        if (StringUtils.isNotBlank(taskId)) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            String roleId = dynamicRole.getRoleId();
            List<Position> orgUnitList =
                positionRoleApi.listPositionsByRoleId(Y9LoginUserHolder.getTenantId(), roleId).getData();
            boolean openTodo = task.getAssignee().equals(currentOrgUnitId);
            if (openTodo) {
                OrgUnit currentOrgUnit = orgUnitApi.getOrgUnit(tenantId, currentOrgUnitId).getData();
                boolean match =
                    orgUnitList.stream().anyMatch(orgUnit -> orgUnit.getId().equals(currentOrgUnit.getId()));
                if (match) {
                    // 和[当前人]同委办局
                    OrgUnit bureau = orgUnitApi.getBureau(tenantId, currentOrgUnitId).getData();
                    orgUnitList = orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(bureau.getId()))
                        .collect(Collectors.toList());
                } else {
                    // 和[当前人]同部门
                    orgUnitList = orgUnitList.stream()
                        .filter(orgUnit -> orgUnit.getParentId().equals(currentOrgUnit.getParentId()))
                        .collect(Collectors.toList());
                }
            } else {
                // 管理员打开:[子流程流程启动人]同委办局
                String subStartUserId = "";
                List<HistoricTaskInstanceModel> hisTaskList = historicTaskApi
                    .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, task.getProcessInstanceId(), "")
                    .getData();
                for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                    if (hisTask.getExecutionId().equals(task.getExecutionId())) {
                        subStartUserId = hisTask.getAssignee();
                        break;
                    }
                }
                OrgUnit subStartBureau = orgUnitApi.getBureau(tenantId, subStartUserId).getData();
                orgUnitList =
                    orgUnitList.stream().filter(orgUnit -> orgUnit.getGuidPath().contains(subStartBureau.getId()))
                        .collect(Collectors.toList());
            }
            return orgUnitList;
        }
        return List.of();
    }
}