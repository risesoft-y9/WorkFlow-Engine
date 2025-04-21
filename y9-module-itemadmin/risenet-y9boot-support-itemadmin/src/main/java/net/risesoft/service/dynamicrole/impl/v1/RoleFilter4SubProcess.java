package net.risesoft.service.dynamicrole.impl.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 根据子流程启动人对角色中的人员进行筛选
 *
 * @author qinman
 * @date 2025/04/21
 */
@Service
@RequiredArgsConstructor
public class RoleFilter4SubProcess extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final HistoricTaskApi historicTaskApi;

    private final TaskApi taskApi;

    private final RoleApi roleApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String taskId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        String roleId = dynamicRole.getRoleId();
        List<OrgUnit> orgUnitList = roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData();
        String assignee = "";
        if (StringUtils.isNotBlank(taskId)) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            List<HistoricTaskInstanceModel> hisTaskList = historicTaskApi
                .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, task.getProcessInstanceId(), "").getData();
            for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                if (hisTask.getExecutionId().equals(task.getExecutionId())) {
                    assignee = hisTask.getAssignee();
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(assignee)) {
            userId = assignee;
        }
        OrgUnit personOrPosition = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Integer ranges = dynamicRole.getRanges();
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