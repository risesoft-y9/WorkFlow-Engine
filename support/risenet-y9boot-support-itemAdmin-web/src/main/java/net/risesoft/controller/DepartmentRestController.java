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

import com.google.common.collect.Maps;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.OrganizationApi;
import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping("/vue/department")
public class DepartmentRestController {

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private OrganizationApi organizationApi;

    @Autowired
    private DepartmentApi departmentManager;

    /**
     * 查找部门及部门下的人员
     *
     * @param id 部门表中的主键Id
     * @return
     */
    @RequestMapping(value = "/findDeptAndUserById", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findDeptAndUserById(@RequestParam(required = false) String id) {
        String personId = Y9LoginUserHolder.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            OrgUnit orgUnit = orgUnitManager.getOrganization(tenantId, personId).getData();
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", orgUnit.getId());
            map.put("parentId", orgUnit.getParentId());
            map.put("name", orgUnit.getName());
            map.put("orgType", orgUnit.getOrgType());
            map.put("isParent", true);
            map.put("nocheck", true);
            items.add(map);
            id = orgUnit.getId();
        }
        items.addAll(genDeptTree(id));
        List<Person> employees = departmentManager.listPersons(tenantId, id).getData();
        for (Person employee : employees) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", employee.getId());
            map.put("parentId", employee.getParentId());
            map.put("name", employee.getName());
            map.put("sex", employee.getSex());
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
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findDeptById(@RequestParam(required = false) String id) {
        List<Map<String, Object>> items = new ArrayList<>();
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
        String personId = Y9LoginUserHolder.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            OrgUnit orgUnit = orgUnitManager.getOrganization(tenantId, personId).getData();
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", orgUnit.getId());
            map.put("parentId", orgUnit.getParentId());
            map.put("name", orgUnit.getName());
            map.put("orgType", orgUnit.getOrgType());
            map.put("isParent", true);
            map.put("nocheck", true);
            items.add(map);
            id = orgUnit.getId();
        }
        List<Department> departments = departmentManager.listSubDepartments(tenantId, id).getData();
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
                if (departmentManager.listSubDepartments(tenantId, department.getId()).getData().size() > 0) {
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
     *
     * @param deptGuidList
     * @param parentId 父节点Guid，由于角色、用户组、动态角色都调用该方法，所以parentId可能会是角色、用户组、动态角色的Id
     * @param principalType 主体类型，由于角色、用户组、动态角色都调用该方法，所以主体类型可能会是角色、用户组、动态角色的Id，如果不使用该字段，则将其设置为0
     * @param noCheck 是否显示前面的复选框，true表示不显示，false表示显示
     * @return
     */
    public List<Map<String, Object>> genDeptTree(String deptGuid) {
        List<Map<String, Object>> items = new ArrayList<>();
        List<Department> deptList =
            departmentManager.listSubDepartments(Y9LoginUserHolder.getTenantId(), deptGuid).getData();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        orgUnitList.addAll(deptList);
        List<Map<String, Object>> listMap = new ArrayList<>();
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
     *
     * @param id 部门表中的主键Id
     * @return
     */
    @RequestMapping(value = "/getOrgList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Organization>> getOrgList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Organization> list = organizationApi.listAllOrganizations(tenantId).getData();
        return Y9Result.success(list, "获取成功");
    }

    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<OrgUnit>> getOrgTree(String id, String treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }

    @RequestMapping(value = "/treeSearch", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<OrgUnit>> treeSearch(String name, String treeType) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> list = orgUnitManager.treeSearch(tenantId, name, treeType).getData();
        return Y9Result.success(list, "获取成功");
    }
}
