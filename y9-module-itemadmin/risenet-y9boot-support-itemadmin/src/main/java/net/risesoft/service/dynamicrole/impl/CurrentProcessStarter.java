package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前流程启动人
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentProcessStarter extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final RuntimeApi runtimeApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
            String userIdAndDeptId = processInstance.getStartUserId();
            if (StringUtils.isNotEmpty(userIdAndDeptId)) {
                String userId = userIdAndDeptId.split(":")[0];
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                orgUnitList.add(orgUnit);
            }
        }
        return orgUnitList;
    }
}