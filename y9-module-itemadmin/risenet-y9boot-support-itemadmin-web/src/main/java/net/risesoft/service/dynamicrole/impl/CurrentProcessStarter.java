package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.RuntimeApiClient;

/**
 * 当前流程的启动人员
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public class CurrentProcessStarter extends AbstractDynamicRoleMember {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private RuntimeApiClient runtimeManager;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessInstanceModel processInstance = runtimeManager.getProcessInstance(tenantId, processInstanceId);
            String userIdAndDeptId = processInstance.getStartUserId();
            if (StringUtils.isNotEmpty(userIdAndDeptId)) {
                String userId = userIdAndDeptId.split(":")[0];
                OrgUnit orgUnit = personManager.get(tenantId, userId).getData();
                orgUnitList.add(orgUnit);
            }
        }
        return orgUnitList;
    }
}