package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前人员所在委办局和当前历程参与过的所有的委办局
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentBureau extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamService processParamService;

    @Override
    public List<OrgUnit> getOrgUnitList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, user.getParentId()).getData();
        orgUnitList.add(orgUnit);
        return orgUnitList;
    }

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, user.getParentId()).getData();
        if (StringUtils.isNotBlank(processInstanceId)) {
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            if (null != processParam && StringUtils.isNotBlank(processParam.getBureauIds())) {
                String[] bureauIds = processParam.getBureauIds().split(SysVariables.SEMICOLON);
                for (String bureauId : bureauIds) {
                    if (!bureauId.equals(orgUnit.getId())) {
                        orgUnitList.add(orgUnitApi.getOrgUnit(tenantId, bureauId).getData());
                    }
                }
            }
        }
        orgUnitList.add(orgUnit);
        return orgUnitList;
    }
}