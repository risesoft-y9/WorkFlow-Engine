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
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.controller.vo.NodeTreeVO;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/role", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleRestController {

    private final RoleApi roleApi;

    private final PositionRoleApi positionRoleApi;

    private final SystemApi systemApi;

    private final AppApi appApi;

    /**
     * 获取系统角色
     *
     * @param id 节点id
     * @return Y9Result<List<NodeTreeVO>>
     */
    @GetMapping(value = "/findRole")
    public Y9Result<List<NodeTreeVO>> findRole(@RequestParam(required = false) String id) {
        try {
            List<NodeTreeVO> listMap = new ArrayList<>();
            if (StringUtils.isBlank(id)) {
                buildRootNodes(listMap);
            } else {
                buildChildNodes(listMap, id);
            }
            return Y9Result.success(listMap, "获取成功");
        } catch (Exception e) {
            return Y9Result.failure("获取角色列表失败");
        }
    }

    /**
     * 构建根节点
     */
    private void buildRootNodes(List<NodeTreeVO> listMap) {
        System system = systemApi.getByName(Y9Context.getSystemName()).getData();
        // 添加公共角色节点
        NodeTreeVO publicRoleMap = new NodeTreeVO();
        publicRoleMap.setId(InitDataConsts.TOP_PUBLIC_ROLE_ID);
        publicRoleMap.setName("公共角色");
        publicRoleMap.setParentId(null);
        publicRoleMap.setIsParent(true);
        publicRoleMap.setOrgType("App");
        listMap.add(publicRoleMap);
        // 添加应用节点
        List<App> appList = appApi.listBySystemId(system.getId()).getData();
        for (App app : appList) {
            NodeTreeVO map = createNodeTreeVO(app.getId(), app.getName(), app.getId(), true, "App");
            listMap.add(map);
        }
    }

    /**
     * 构建子节点
     */
    private void buildChildNodes(List<NodeTreeVO> listMap, String parentId) {
        List<Role> listRole = roleApi.listRoleByParentId(parentId).getData();
        if (listRole != null) {
            for (Role role : listRole) {
                NodeTreeVO map = createNodeTreeVO(role.getId(), role.getName(), parentId, false, null);
                map.setGuidPath(role.getGuidPath());

                if (RoleTypeEnum.ROLE.equals(role.getType())) {
                    map.setIsParent(false);
                    map.setOrgType("role");
                } else {
                    List<Role> childRoles = roleApi.listRoleByParentId(role.getId()).getData();
                    boolean hasChildren = childRoles != null && !childRoles.isEmpty();

                    if (hasChildren) {
                        map.setChkDisabled(true);
                    }
                    map.setIsParent(hasChildren);
                    map.setOrgType("folder");
                }
                listMap.add(map);
            }
        }
    }

    /**
     * 创建NodeTreeVO对象的通用方法
     */
    private NodeTreeVO createNodeTreeVO(String id, String name, String parentId, boolean isParent, String orgType) {
        NodeTreeVO node = new NodeTreeVO();
        node.setId(id);
        node.setName(name);
        node.setParentId(parentId);
        node.setIsParent(isParent);
        if (orgType != null) {
            node.setOrgType(orgType);
        }
        return node;
    }

    @GetMapping(value = "/findRoleMember")
    public Y9Result<List<NodeTreeVO>> findRoleMember(@RequestParam String roleId) {
        List<NodeTreeVO> listMap = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Position> list = positionRoleApi.listPositionsByRoleId(tenantId, roleId).getData();
        for (Position position : list) {
            NodeTreeVO map = new NodeTreeVO();
            map.setId(position.getId());
            map.setName(position.getName());
            map.setParentId(roleId);
            map.setGuidPath(position.getGuidPath());
            map.setOrgType(position.getOrgType().getValue());
            map.setIsParent(false);
            map.setDn(position.getDn());
            listMap.add(map);
        }
        return Y9Result.success(listMap, "获取成功");
    }
}
