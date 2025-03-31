package net.risesoft.service.dynamicrole.impl.v1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.service.ActRuDetailService;
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

    private final ActRuDetailService actRuDetailService;

    private final HistoricTaskApi historicTaskApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ActRuDetail actRuDetail = actRuDetailService.findByProcessInstanceIdAndAssignee(processInstanceId, userId);
            List<HistoricTaskInstanceModel> hisTaskList = historicTaskApi
                .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "").getData();
            String assignee = "";
            for (HistoricTaskInstanceModel hisTask : hisTaskList) {
                if (hisTask.getExecutionId().equals(actRuDetail.getExecutionId())) {
                    assignee = hisTask.getAssignee();
                    break;
                }
            }
            if (StringUtils.isNotBlank(assignee)) {
                OrgUnit position = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                if (null != position) {
                    orgUnitList.add(position);
                }
            }
        }
        return orgUnitList;
    }
}