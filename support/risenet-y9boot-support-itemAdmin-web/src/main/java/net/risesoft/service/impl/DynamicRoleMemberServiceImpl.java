package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import net.risesoft.entity.DynamicRole;
import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.util.BeanFactory;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "dynamicRoleMemberService")
public class DynamicRoleMemberServiceImpl implements DynamicRoleMemberService {

	@Autowired
	private DynamicRoleService dynamicRoleService;

	@Override
	public Department getDepartment(DynamicRole dynamicRole) {
		Department department = new Department();
		String classFullPath = dynamicRole.getClassPath();
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Y9Context.getAc();
		DefaultListableBeanFactory beanFactory = BeanFactory.getBeanFactory(applicationContext);

		beanFactory = BeanFactory.addBean(beanFactory, classFullPath);
		AbstractDynamicRoleMember dynamicRoleMemberService = (AbstractDynamicRoleMember) beanFactory.getBean(classFullPath);
		department = dynamicRoleMemberService.getDepartment();
		return department;
	}

	@Override
	public List<OrgUnit> getOrgUnitList(String dynamicRoleId) {
		DynamicRole dynamicRole = dynamicRoleService.findOne(dynamicRoleId);
		List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
		String classFullPath = dynamicRole.getClassPath();
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Y9Context.getAc();
		DefaultListableBeanFactory beanFactory = BeanFactory.getBeanFactory(applicationContext);

		beanFactory = BeanFactory.addBean(beanFactory, classFullPath);
		AbstractDynamicRoleMember dynamicRoleMemberService = (AbstractDynamicRoleMember) beanFactory.getBean(classFullPath);
		orgUnitList = dynamicRoleMemberService.getOrgUnitList();
		return orgUnitList;
	}

	@Override
	public List<OrgUnit> getOrgUnitList(String dynamicRoleId, String processInstanceId) {
		DynamicRole dynamicRole = dynamicRoleService.findOne(dynamicRoleId);
		List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
		String classFullPath = dynamicRole.getClassPath();
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) Y9Context.getAc();
		DefaultListableBeanFactory beanFactory = BeanFactory.getBeanFactory(applicationContext);

		beanFactory = BeanFactory.addBean(beanFactory, classFullPath);
		AbstractDynamicRoleMember dynamicRoleMemberService = (AbstractDynamicRoleMember) beanFactory.getBean(classFullPath);
		if (dynamicRole.isUseProcessInstanceId()) {
			orgUnitList = dynamicRoleMemberService.getOrgUnitList(processInstanceId);
		} else {
			orgUnitList = dynamicRoleMemberService.getOrgUnitList();
		}
		return orgUnitList;
	}

}
