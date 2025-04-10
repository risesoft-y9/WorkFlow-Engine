package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.DynamicRole;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.util.BeanFactory;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class DynamicRoleMemberServiceImpl implements DynamicRoleMemberService {

    @Override
    public List<OrgUnit> listByDynamicRoleIdAndTaskId(DynamicRole dynamicRole, String taskId) {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)Y9Context.getAc();
        DefaultListableBeanFactory beanFactory = BeanFactory.getBeanFactory(applicationContext);
        String classFullPath = dynamicRole.getClassPath();
        beanFactory = BeanFactory.addBean(beanFactory, classFullPath);
        AbstractDynamicRoleMember dynamicRoleMemberService =
            (AbstractDynamicRoleMember)beanFactory.getBean(classFullPath);
        return dynamicRoleMemberService.getOrgUnitList(taskId);
    }

    @Override
    public List<OrgUnit> listByDynamicRoleIdAndProcessInstanceId(DynamicRole dynamicRole, String processInstanceId) {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)Y9Context.getAc();
        DefaultListableBeanFactory beanFactory = BeanFactory.getBeanFactory(applicationContext);
        String classFullPath = dynamicRole.getClassPath();
        beanFactory = BeanFactory.addBean(beanFactory, classFullPath);
        AbstractDynamicRoleMember dynamicRoleMemberService =
            (AbstractDynamicRoleMember)beanFactory.getBean(classFullPath);
        List<OrgUnit> orgUnitList;
        if (null == dynamicRole.getKinds() || dynamicRole.getKinds().equals(DynamicRoleKindsEnum.NONE.getValue())) {
            if (dynamicRole.isUseProcessInstanceId()) {
                orgUnitList = dynamicRoleMemberService.getOrgUnitList(processInstanceId);
            } else {
                orgUnitList = dynamicRoleMemberService.getOrgUnitList();
            }
        } else {
            orgUnitList = dynamicRoleMemberService.getOrgUnitList(processInstanceId, dynamicRole);
        }
        return orgUnitList;
    }

}
