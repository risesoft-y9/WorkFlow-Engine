package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.controller.vo.RoleTreeVO;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.App;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/role", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleRestController {

    private final RoleApi roleManager;

    private final SystemApi systemEntityManager;

    private final AppApi appApi;

    /**
     * 获取系统角色
     *
     * @param id 节点id
     * @return
     */
    @GetMapping(value = "/findRole")
    public Y9Result<List<RoleTreeVO>> findAll(@RequestParam(required = false) String id) {
        List<RoleTreeVO> listMap = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            System system = systemEntityManager.getByName(Y9Context.getSystemName()).getData();
            List<App> appList = appApi.listBySystemId(system.getId()).getData();
            for (App app : appList) {
                RoleTreeVO map = new RoleTreeVO();
                map.setId(app.getId());
                map.setName(app.getName());
                map.setParentId(app.getId());
                map.setIsParent(true);
                map.setOrgType("App");
                listMap.add(map);
            }
        } else {
            List<Role> listRole = roleManager.listRoleByParentId(id).getData();
            if (listRole != null) {
                for (Role role : listRole) {
                    RoleTreeVO map = new RoleTreeVO();
                    map.setId(role.getId());
                    map.setName(role.getName());
                    map.setParentId(id);
                    map.setGuidPath(role.getGuidPath());
                    if (RoleTypeEnum.ROLE.equals(role.getType())) {
                        map.setIsParent(false);
                        map.setOrgType("role");
                    } else {
                        List<Role> list = roleManager.listRoleByParentId(role.getId()).getData();
                        boolean isP = false;
                        if (list != null) {
                            isP = !list.isEmpty();
                        }
                        if (isP) {
                            map.setChkDisabled(true);
                        }
                        map.setIsParent(isP);
                        map.setOrgType("folder");
                    }
                    listMap.add(map);
                }
            }
        }
        return Y9Result.success(listMap, "获取成功");
    }
}
