package net.risesoft.service.dynamicrole.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.DynamicRole;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.service.dynamicrole.AbstractDynamicRoleMember;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 会签部门秘书
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
@RequiredArgsConstructor
public class SignDeptSecretary extends AbstractDynamicRoleMember {

    private final OrgUnitApi orgUnitApi;

    private final SignDeptInfoService signDeptInfoService;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final RoleApi roleApi;

    @Override
    public List<OrgUnit> getOrgUnitList(String processInstanceId, DynamicRole dynamicRole) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(processInstanceId)) {
            String roleId = dynamicRole.getRoleId();
            List<OrgUnit> orgUnitList = roleApi.listOrgUnitsById(tenantId, roleId, OrgTypeEnum.POSITION).getData();
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getOrgUnitId()).getData();
            // 排除本司局
            List<OrgUnit> orgUnitListFilter = orgUnitList.stream()
                .filter(orgUnit -> !orgUnit.getGuidPath().contains(bureau.getId())).collect(Collectors.toList());
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            List<SignDeptInfo> list = signDeptInfoService.getSignDeptList(officeDoneInfo.getProcessSerialNumber(), "0");
            // 获取会签司局
            List<OrgUnit> orgUnitListTemp = new ArrayList<>();
            list.forEach(signDeptInfo -> {
                orgUnitListFilter.forEach(orgUnit -> {
                    if (orgUnit.getGuidPath().contains(signDeptInfo.getDeptId())) {
                        orgUnitListTemp.add(orgUnit);
                    }
                });
            });
            return orgUnitListTemp;
        }
        return List.of();
    }
}