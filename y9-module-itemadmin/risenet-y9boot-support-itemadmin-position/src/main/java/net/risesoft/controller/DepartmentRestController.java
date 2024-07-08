package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/department", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentRestController {

    private final OrgUnitApi orgUnitManager;

    private final DepartmentApi departmentManager;

    private final OrganizationApi organizationApi;

    /**
     * 查找部门及部门下的岗位
     *
     * @param id 部门表中的主键Id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/findDeptAndUserById")
    public Y9Result<List<Map<String, Object>>> findDeptAndUserById(@RequestParam(required = false) String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", orgUnit.getId());
                map.put("parentId", orgUnit.getParentId());
                map.put("name", orgUnit.getName());
                map.put("orgType", orgUnit.getOrgType());
                map.put("isParent", true);
                map.put("nocheck", true);
                items.add(map);
            }
        }
        List<OrgUnit> employees = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", employee.getId());
            map.put("parentId", employee.getParentId());
            map.put("name", employee.getName());
            map.put("orgType", employee.getOrgType());
            map.put("isParent", false);
            map.put("nocheck", false);
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 查找部门
     *
     * @param id 部门表中的主键Id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/findDeptById")
    public Y9Result<List<Map<String, Object>>> findDeptById(@RequestParam(required = false) String id) {
        List<Map<String, Object>> items = findDeptById(id, false);
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 查找部门或委办局
     *
     * @param id 部门表中的主键Id
     * @param isBureau 如果为true则只查找委办局，如果为false则查找部门
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> findDeptById(String id, boolean isBureau) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", orgUnit.getId());
                map.put("parentId", orgUnit.getParentId());
                map.put("name", orgUnit.getName());
                map.put("orgType", orgUnit.getOrgType());
                map.put("isParent", true);
                map.put("nocheck", true);
                items.add(map);
            }
        }
        List<Department> departments = departmentManager.listByParentId(tenantId, id).getData();
        for (Department department : departments) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", department.getId());
            map.put("parentId", department.getParentId());
            map.put("name", department.getName());
            map.put("orgType", department.getOrgType());
            map.put("nocheck", false);
            if (isBureau) {
                if (department.isBureau()) {
                    map.put("isParent", false);
                } else {
                    map.put("isParent", true);
                }
            } else {
                if (!departmentManager.listByParentId(tenantId, department.getId()).getData().isEmpty()) {
                    map.put("isParent", true);
                } else {
                    map.put("isParent", false);
                }
            }
            items.add(map);
        }
        return items;
    }

    /**
     * 查找部门及部门下的人员
     *
     * @return Y9Result<List<Organization>>
     */
    @GetMapping(value = "/getOrgList")
    public Y9Result<List<Organization>> getOrgList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Organization> list = organizationApi.list(tenantId).getData();
        return Y9Result.success(list, "获取成功");
    }

    @GetMapping(value = "/getOrgTree")
    public Y9Result<List<OrgUnit>> getOrgTree(String id, OrgTreeTypeEnum treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }

    @GetMapping(value = "/searchDept")
    public Y9Result<List<Map<String, Object>>> searchDept(@RequestParam(required = false) String name) {
        List<Map<String, Object>> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees = orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_DEPT).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", employee.getId());
            map.put("parentId", employee.getParentId());
            map.put("name", employee.getName());
            map.put("orgType", employee.getOrgType());
            map.put("isParent", false);
            map.put("nocheck", false);
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }

    @GetMapping(value = "/searchDeptAndPosition")
    public Y9Result<List<Map<String, Object>>> searchDeptAndPosition(@RequestParam(required = false) String name) {
        List<Map<String, Object>> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees =
            orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", employee.getId());
            map.put("parentId", employee.getParentId());
            map.put("name", employee.getName());
            map.put("orgType", employee.getOrgType());
            map.put("isParent", false);
            map.put("nocheck", false);
            items.add(map);
        }
        return Y9Result.success(items, "获取成功");
    }

    @GetMapping(value = "/treeSearch")
    public Y9Result<List<OrgUnit>> treeSearch(String name, OrgTreeTypeEnum treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.treeSearch(tenantId, name, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }
}
