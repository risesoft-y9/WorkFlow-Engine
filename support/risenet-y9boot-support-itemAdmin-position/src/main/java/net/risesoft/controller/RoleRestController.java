package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.permission.RoleApi;
import net.risesoft.api.resource.AppApi;
import net.risesoft.api.resource.SystemApi;
import net.risesoft.model.App;
import net.risesoft.model.Role;
import net.risesoft.model.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/vue/role")
public class RoleRestController {

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private SystemApi systemEntityManager;

    @Autowired
    private AppApi appApi;

    /**
     * 获取系统角色
     *
     * @param id 节点id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findRole", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findAll(@RequestParam(required = false) String id) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            System system = systemEntityManager.getByName(Y9Context.getSystemName()).getData();
            List<App> appList = appApi.listBySystemId(system.getId()).getData();
            for (App app : appList) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("id", app.getId());
                map.put("name", app.getName());
                map.put("parentId", app.getId());
                map.put("isParent", true);
                map.put("orgType", "App");
                listMap.add(map);
            }
        } else {
            List<Role> listRole = roleManager.listRoleByParentId(id).getData();
            if (listRole != null) {
                for (Role role : listRole) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    map.put("id", role.getId());
                    map.put("name", role.getName());
                    map.put("parentId", id);
                    map.put("guidPath", role.getGuidPath());
                    if ("role".equals(role.getType())) {
                        map.put("isParent", false);
                        map.put("orgType", "role");
                    } else {
                        List<Role> list = roleManager.listRoleByParentId(role.getId()).getData();
                        boolean isP = false;
                        if (list != null) {
                            isP = list.size() > 0 ? true : false;
                        }
                        if (isP) {
                            map.put("chkDisabled", true);
                        }
                        map.put("isParent", isP);
                        map.put("orgType", "folder");
                    }
                    listMap.add(map);
                }
            }
        }
        return Y9Result.success(listMap, "获取成功");
    }
}
