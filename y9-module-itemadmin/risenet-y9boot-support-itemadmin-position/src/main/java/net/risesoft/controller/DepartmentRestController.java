package net.risesoft.controller;

import com.google.common.collect.Maps;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/vue/department")
public class DepartmentRestController {

    private final OrgUnitApi orgUnitManager;

    private final DepartmentApi departmentManager;

    private final OrganizationApi organizationApi;

    /**
     * 查找部门及部门下的岗位
     *
     * @param id 部门表中的主键Id
     * @return
     */
    @RequestMapping(value = "/findDeptAndUserById", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findDeptAndUserById(@RequestParam(required = false) String id,
        @RequestParam(required = false) String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                map.put("id", orgUnit.getId());
                map.put("parentId", orgUnit.getParentId());
                map.put("name", orgUnit.getName());
                map.put("orgType", orgUnit.getOrgType());
                map.put("isParent", true);
                map.put("nocheck", true);
                items.add(map);
            }
        }
        /** items.addAll(genDeptTree(id)); */
        List<OrgUnit> employees = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<String, Object>(16);
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
     * @return
     */
    @RequestMapping(value = "/findDeptById", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findDeptById(@RequestParam(required = false) String id) {
        List<Map<String, Object>> items = findDeptById(id, false);
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 查找部门或委办局
     *
     * @param id 部门表中的主键Id
     * @param isBureau 如果为true则只查找委办局，如果为false则查找部门
     * @return
     */
    public List<Map<String, Object>> findDeptById(String id, boolean isBureau) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
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
            Map<String, Object> map = new HashMap<String, Object>(16);
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
                if (departmentManager.listByParentId(tenantId, department.getId()).getData().size() > 0) {
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
     * 生成部门树
     * @param deptGuid 部门主键Id
     * @return
     */
    public List<Map<String, Object>> genDeptTree(String deptGuid) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<Department> deptList =
            departmentManager.listByParentId(Y9LoginUserHolder.getTenantId(), deptGuid).getData();
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
        orgUnitList.addAll(deptList);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (OrgUnit entity : deptList) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", entity.getId());
            map.put("parentId", deptGuid);
            map.put("orgType", entity.getOrgType());
            map.put("name", entity.getName());
            map.put("nocheck", true);
            map.put("isParent", true);
            listMap.add(map);
        }
        items.addAll(listMap);
        return items;
    }

    /**
     * 查找部门及部门下的人员
     * @return
     */
    @RequestMapping(value = "/getOrgList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Organization>> getOrgList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Organization> list = organizationApi.list(tenantId).getData();
        return Y9Result.success(list, "获取成功");
    }

    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> getOrgTree(String id, OrgTreeTypeEnum treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }

    @RequestMapping(value = "/searchDept", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> searchDept(@RequestParam(required = false) String name) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees = orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_DEPT).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<String, Object>(16);
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

    @RequestMapping(value = "/searchDeptAndPosition", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> searchDeptAndPosition(@RequestParam(required = false) String name) {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees =
            orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION).getData();
        for (OrgUnit employee : employees) {
            Map<String, Object> map = new HashMap<String, Object>(16);
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

    @RequestMapping(value = "/treeSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> treeSearch(String name, OrgTreeTypeEnum treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.treeSearch(tenantId, name, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }
}
