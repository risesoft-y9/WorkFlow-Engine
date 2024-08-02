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

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.controller.vo.NodeTreeVO;
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

    private final DepartmentApi departmentApi;

    private final OrganizationApi organizationApi;

    /**
     * 查找部门及部门下的岗位
     *
     * @param id 部门表中的主键Id
     * @return Y9Result<List<NodeTreeVO>>
     */
    @GetMapping(value = "/findDeptAndUserById")
    public Y9Result<List<NodeTreeVO>> findDeptAndUserById(@RequestParam(required = false) String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<NodeTreeVO> items = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                NodeTreeVO map = new NodeTreeVO();
                map.setId(orgUnit.getId());
                map.setParentId(orgUnit.getParentId());
                map.setName(orgUnit.getName());
                map.setOrgType(orgUnit.getOrgType().getValue());
                map.setIsParent(true);
                map.setNocheck(true);
                items.add(map);
            }
        }
        List<OrgUnit> employees = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_POSITION).getData();
        for (OrgUnit employee : employees) {
            NodeTreeVO map = new NodeTreeVO();
            map.setId(employee.getId());
            map.setParentId(employee.getParentId());
            map.setName(employee.getName());
            map.setOrgType(employee.getOrgType().getValue());
            map.setIsParent(false);
            map.setNocheck(false);
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
    public Y9Result<List<NodeTreeVO>> findDeptById(@RequestParam(required = false) String id) {
        List<NodeTreeVO> items = findDeptById(id, false);
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 查找部门或委办局
     *
     * @param id 部门表中的主键Id
     * @param isBureau 如果为true则只查找委办局，如果为false则查找部门
     * @return List<Map<String, Object>>
     */
    public List<NodeTreeVO> findDeptById(String id, boolean isBureau) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<NodeTreeVO> items = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<Organization> list = organizationApi.list(tenantId).getData();
            for (Organization orgUnit : list) {
                NodeTreeVO node = new NodeTreeVO();
                node.setId(orgUnit.getId());
                node.setParentId(orgUnit.getParentId());
                node.setName(orgUnit.getName());
                node.setOrgType(orgUnit.getOrgType().getValue());
                node.setIsParent(true);
                node.setNocheck(true);
                items.add(node);
            }
        }
        List<Department> departments = departmentApi.listByParentId(tenantId, id).getData();
        for (Department department : departments) {
            NodeTreeVO node = new NodeTreeVO();
            node.setId(department.getId());
            node.setParentId(department.getParentId());
            node.setName(department.getName());
            node.setOrgType(department.getOrgType().getValue());
            node.setNocheck(false);

            if (isBureau) {
                node.setIsParent(!department.isBureau());
            } else {
                node.setIsParent(!departmentApi.listByParentId(tenantId, department.getId()).getData().isEmpty());
            }
            items.add(node);
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
    public Y9Result<List<NodeTreeVO>> searchDept(@RequestParam(required = false) String name) {
        List<NodeTreeVO> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees = orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_DEPT).getData();
        for (OrgUnit employee : employees) {
            NodeTreeVO node = new NodeTreeVO();
            node.setId(employee.getId());
            node.setParentId(employee.getParentId());
            node.setName(employee.getName());
            node.setOrgType(employee.getOrgType().getValue());
            node.setIsParent(false);
            node.setNocheck(false);
            items.add(node);
        }
        return Y9Result.success(items, "获取成功");
    }

    @GetMapping(value = "/searchDeptAndPosition")
    public Y9Result<List<NodeTreeVO>> searchDeptAndPosition(@RequestParam(required = false) String name) {
        List<NodeTreeVO> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<OrgUnit> employees =
            orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG_POSITION).getData();
        for (OrgUnit employee : employees) {
            NodeTreeVO node = new NodeTreeVO();
            node.setId(employee.getId());
            node.setParentId(employee.getParentId());
            node.setName(employee.getName());
            node.setOrgType(employee.getOrgType().getValue());
            node.setIsParent(false);
            node.setNocheck(false);
            items.add(node);
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
