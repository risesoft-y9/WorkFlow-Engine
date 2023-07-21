package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当前组织架构(人员不包括用户组、岗位里面的人员)
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public class CurrentOrg extends AbstractDynamicRoleMember {

	@Autowired
	private OrgUnitApi orgUnitManager;

	@Override
	public Organization getOrg() {
		String tenantId = Y9LoginUserHolder.getTenantId();
		Organization org = orgUnitManager.getOrganization(tenantId, Y9LoginUserHolder.getPersonId());
		return org;
	}

	@Override
	public List<OrgUnit> getOrgUnitList() {
		List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
		orgUnitList.add(getOrg());
		return orgUnitList;
	}

}
