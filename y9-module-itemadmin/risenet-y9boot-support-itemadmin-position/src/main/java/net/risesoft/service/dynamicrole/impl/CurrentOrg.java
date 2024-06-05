package net.risesoft.service.dynamicrole.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 当前组织架构(人员不包括用户组、岗位里面的人员)
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class CurrentOrg extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitManager;

    @Override
    public Organization getOrg() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Organization org = orgUnitManager.getOrganization(tenantId, Y9LoginUserHolder.getPositionId()).getData();
        return org;
    }

    @Override
    public List<OrgUnit> getOrgUnitList() {
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        orgUnitList.add(getOrg());
        return orgUnitList;
    }

}
