package net.risesoft.service.dynamicrole.impl.v1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 子流程的启动人
 *
 * @author qinman
 * @date 2025/03/31
 */
@Service
@RequiredArgsConstructor
public class Starter4SubProcess extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final HistoricTaskApi historicTaskApi;

    private final TaskApi taskApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String taskId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(taskId)) {
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            List<HistoricTaskInstanceModel> hisTaskList = historicTaskApi
                .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, task.getProcessInstanceId(), "").getData();
            String assignee = "";
            for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                if (hisTask.getExecutionId().equals(task.getExecutionId())) {
                    assignee = hisTask.getAssignee();
                    break;
                }
            }
            if (StringUtils.isNotBlank(assignee)) {
                OrgUnit position = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                if (null != position) {
                    orgUnitList.add(position);
                }
            }
        }
        return orgUnitList;
    }
}