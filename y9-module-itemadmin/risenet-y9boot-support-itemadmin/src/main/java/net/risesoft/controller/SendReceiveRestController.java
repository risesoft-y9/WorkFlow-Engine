package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.vo.NodeTreeVO;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.service.ReceiveDeptAndPersonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/sendReceive", produces = MediaType.APPLICATION_JSON_VALUE)
public class SendReceiveRestController {

    private final ReceiveDeptAndPersonService receiveDeptAndPersonService;

    private final OrganizationApi organizationManager;

    private final OrgUnitApi orgUnitManager;

    private final PersonApi personApi;

    private final DepartmentApi departmentApi;

    private final ReceiveDepartmentRepository receiveDepartmentRepository;

    /**
     * 验证是否可以收文
     *
     * @param deptId 部门id
     * @return
     */
    @PostMapping(value = "/checkReceiveSend")
    public Y9Result<String> checkReceiveSend(@RequestParam String deptId) {
        ReceiveDepartment receiveDept = receiveDeptAndPersonService.findByDeptId(deptId);
        if (receiveDept != null) {
            return Y9Result.successMsg("true");
        } else {
            return Y9Result.failure("false");
        }
    }

    /**
     * 取消收发员
     *
     * @param id 人员id
     * @return
     */
    @PostMapping(value = "/delPerson")
    public Y9Result<String> delPerson(@RequestParam String id) {
        return receiveDeptAndPersonService.delPerson(id);
    }

    /**
     * 搜索部门树
     *
     * @param name 搜索词
     * @param deptId 部门id
     * @return
     */
    @GetMapping(value = "/deptTreeSearch")
    public Y9Result<List<NodeTreeVO>> deptTreeSearch(@RequestParam(required = false) String name,
        @RequestParam String deptId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<NodeTreeVO> item = new ArrayList<>();
        List<Person> personList = personApi.listRecursivelyByParentIdAndName(tenantId, deptId, name).getData();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (Person person : personList) {
            orgUnitList.add(person);
            Person p = personApi.get(tenantId, person.getId()).getData();
            this.recursionUpToOrg(tenantId, deptId, p.getParentId(), orgUnitList, false);
        }
        for (OrgUnit orgUnit : orgUnitList) {
            NodeTreeVO nodeTreeVO = new NodeTreeVO();
            nodeTreeVO.setId(orgUnit.getId());
            nodeTreeVO.setName(orgUnit.getName());
            nodeTreeVO.setOrgType(orgUnit.getOrgType().getValue());
            nodeTreeVO.setParentId(orgUnit.getParentId());
            nodeTreeVO.setIsParent(true);
            if (OrgTypeEnum.PERSON.equals(orgUnit.getOrgType())) {
                Person per = personApi.get(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                nodeTreeVO.setIsParent(false);
                nodeTreeVO.setSex(per.getSex().getValue());
                nodeTreeVO.setDuty(per.getDuty());
            }
            item.add(nodeTreeVO);
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取部门树（收发人员）
     *
     * @param id 展开部门id
     * @param deptId 部门id
     * @return
     */
    @GetMapping(value = "/getDeptTree")
    public Y9Result<List<NodeTreeVO>> getDeptTrees(@RequestParam(required = false) String id,
        @RequestParam(required = false) String deptId) {
        List<NodeTreeVO> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(deptId)) {
            Department dept = departmentApi.get(tenantId, deptId).getData();
            if (dept != null && dept.getId() != null) {
                NodeTreeVO nodeTreeVO = new NodeTreeVO();
                nodeTreeVO.setId(dept.getId());
                nodeTreeVO.setName(dept.getName());
                nodeTreeVO.setOrgType(dept.getOrgType().getValue());
                nodeTreeVO.setParentId(dept.getParentId());
                nodeTreeVO.setIsParent(true);
                item.add(nodeTreeVO);
            }
        }
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_ORG).getData();
            for (OrgUnit orgunit : orgList) {
                NodeTreeVO nodeTreeVO = new NodeTreeVO();
                nodeTreeVO.setId(orgunit.getId());
                nodeTreeVO.setName(orgunit.getName());
                nodeTreeVO.setOrgType(orgunit.getOrgType().getValue());
                nodeTreeVO.setParentId(id);

                if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                    nodeTreeVO.setIsParent(true);
                } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                    Person person = personApi.get(tenantId, orgunit.getId()).getData();
                    nodeTreeVO.setIsParent(false);
                    nodeTreeVO.setSex(person.getSex().getValue());
                    nodeTreeVO.setDuty(person.getDuty());
                } else {
                    continue;
                }
                item.add(nodeTreeVO);
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 组织机构列表
     *
     * @return
     */
    @GetMapping(value = "/getOrg")
    public Y9Result<List<Organization>> getOrg() {
        List<Organization> list = organizationManager.list(Y9LoginUserHolder.getTenantId()).getData();
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取部门树
     *
     * @param id 部门id
     * @return
     */
    @GetMapping(value = "/getOrgChildTree")
    public Y9Result<List<NodeTreeVO>> getOrgChildTree(@RequestParam(required = false) String id,
        OrgTreeTypeEnum treeType) {
        List<NodeTreeVO> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
            for (OrgUnit orgunit : orgList) {
                NodeTreeVO nodeTreeVO = new NodeTreeVO();
                nodeTreeVO.setId(orgunit.getId());
                nodeTreeVO.setName(orgunit.getName());
                nodeTreeVO.setOrgType(orgunit.getOrgType().getValue());
                nodeTreeVO.setParentId(id);
                nodeTreeVO.setGuidPath(orgunit.getGuidPath());
                if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                    nodeTreeVO.setIsParent(true);
                } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                    Person person = personApi.get(tenantId, orgunit.getId()).getData();
                    nodeTreeVO.setIsParent(false);
                    nodeTreeVO.setSex(person.getSex().getValue());
                    nodeTreeVO.setDuty(person.getDuty());
                } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                    nodeTreeVO.setIsParent(false);
                } else {
                    continue;
                }
                item.add(nodeTreeVO);
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取组织机构子节点(收发单位)
     *
     * @param id id
     * @param treeType 树类型
     * @return
     */
    @GetMapping(value = "/getOrgTree")
    public Y9Result<List<OrgUnit>> getOrgTree(@RequestParam String id, @RequestParam OrgTreeTypeEnum treeType) {
        List<OrgUnit> newOrgUnitList = new ArrayList<>();
        List<OrgUnit> orgUnitList = orgUnitManager.getSubTree(Y9LoginUserHolder.getTenantId(), id, treeType).getData();
        for (OrgUnit orgUnit : orgUnitList) {
            if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                orgUnit.setDn("false");
                ReceiveDepartment receiveDepartment = receiveDeptAndPersonService.findByDeptId(orgUnit.getId());
                List<Department> deptList =
                    departmentApi.listRecursivelyByParentId(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                orgUnit.setGuidPath("false");
                for (Department dept : deptList) {
                    orgUnit.setGuidPath("true");
                    ReceiveDepartment receiveDept = receiveDeptAndPersonService.findByDeptId(dept.getId());
                    if (receiveDept != null) {
                        orgUnit.setDn("true");
                        break;
                    }
                }
                orgUnit.setCustomId("false");
                orgUnit.setProperties("false");
                if (receiveDepartment != null) {
                    orgUnit.setCustomId("true");
                    Integer count = receiveDeptAndPersonService.countByDeptId(orgUnit.getId());
                    if (count > 0) {
                        orgUnit.setProperties("true");
                    }
                }
                newOrgUnitList.add(orgUnit);
            }
        }
        return Y9Result.success(newOrgUnitList, "获取成功");
    }

    public OrgUnit getParent(String tenantId, String parentId) {
        Organization parent = organizationManager.get(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentApi.get(tenantId, parentId).getData();
    }

    @RequestMapping(value = "/orderDeptList")
    public Y9Result<List<ReceiveDepartment>> orderDeptList() {
        List<ReceiveDepartment> list = receiveDepartmentRepository.findAllOrderByTabIndex();
        for (ReceiveDepartment receiveDeptAndPerson : list) {
            Department department =
                departmentApi.get(Y9LoginUserHolder.getTenantId(), receiveDeptAndPerson.getDeptId()).getData();
            receiveDeptAndPerson.setDeptName(department.getName());
        }
        // map.put("rows", list);
        return Y9Result.success(list);
    }

    /**
     * 查询组织架构人员
     *
     * @param treeType 树类型
     * @param name 搜索词
     * @return
     */
    @GetMapping(value = "/orgTreeSearch")
    public Y9Result<List<OrgUnit>> orgTreeSearch(@RequestParam OrgTreeTypeEnum treeType, @RequestParam String name) {
        List<OrgUnit> newOrgUnitList = new ArrayList<>();
        List<OrgUnit> orgUnitList =
            orgUnitManager.treeSearch(Y9LoginUserHolder.getTenantId(), name, treeType).getData();
        for (OrgUnit orgUnit : orgUnitList) {
            if (orgUnit.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
                orgUnit.setDn("false");
                ReceiveDepartment receiveDepartment = receiveDeptAndPersonService.findByDeptId(orgUnit.getId());
                List<Department> deptList =
                    departmentApi.listRecursivelyByParentId(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                orgUnit.setGuidPath("false");
                for (Department dept : deptList) {
                    orgUnit.setGuidPath("true");
                    ReceiveDepartment receiveDept = receiveDeptAndPersonService.findByDeptId(dept.getId());
                    if (receiveDept != null) {
                        orgUnit.setDn("true");
                        break;
                    }
                }
                orgUnit.setCustomId("false");
                orgUnit.setProperties("false");
                if (receiveDepartment != null) {
                    orgUnit.setCustomId("true");
                    Integer count = receiveDeptAndPersonService.countByDeptId(orgUnit.getId());
                    if (count > 0) {
                        orgUnit.setProperties("true");
                    }
                }
                newOrgUnitList.add(orgUnit);
            }
        }
        List<Organization> list = organizationManager.list(Y9LoginUserHolder.getTenantId()).getData();
        if (!list.isEmpty()) {
            newOrgUnitList.addAll(list);
        }
        return Y9Result.success(newOrgUnitList, "获取成功");
    }

    /**
     * 获取部门收发员列表
     *
     * @param deptId 部门id
     * @return
     */
    @GetMapping(value = "/personList")
    public Y9Result<List<Map<String, Object>>> personList(@RequestParam String deptId) {
        List<Map<String, Object>> personList = receiveDeptAndPersonService.personList(deptId);
        return Y9Result.success(personList, "获取成功");
    }

    public void recursionUpToOrg(String tenantId, String nodeId, String parentId, List<OrgUnit> orgUnitList,
        boolean isParent) {
        OrgUnit parent = getParent(tenantId, parentId);
        if (isParent) {
            parent.setDescription("parent");
        }
        if (orgUnitList.isEmpty()) {
            orgUnitList.add(parent);
        } else {
            boolean add = true;
            for (OrgUnit orgUnit : orgUnitList) {
                if (orgUnit.getId().equals(parent.getId())) {
                    add = false;
                    break;
                }
            }
            if (add) {
                orgUnitList.add(parent);
            }
        }
        if (parent.getOrgType().equals(OrgTypeEnum.DEPARTMENT)) {
            if (parent.getId().equals(nodeId)) {
                return;
            }
            recursionUpToOrg(tenantId, nodeId, parent.getParentId(), orgUnitList, true);
        }
    }

    /**
     * 设置或取消收发部门
     *
     * @param id 部门id
     * @param type 类型
     * @return
     */
    @PostMapping(value = "/saveOrCancelDept")
    public Y9Result<String> saveOrCancelDept(@RequestParam String id, @RequestParam String type) {
        String save = "save";
        if (type.equals(save)) {
            return receiveDeptAndPersonService.saveDepartment(id);
        } else {
            return receiveDeptAndPersonService.delDepartment(id);
        }
    }

    /**
     * 保存排序
     *
     * @param ids 部门id
     * @return
     */
    @RequestMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(String ids) {
        return receiveDeptAndPersonService.saveOrder(ids);
    }

    /**
     * 设置收发员
     *
     * @param deptId 部门id
     * @param ids 人员ids
     * @return
     */
    @PostMapping(value = "/savePerson")
    public Y9Result<String> savePerson(@RequestParam String deptId, @RequestParam String ids) {
        return receiveDeptAndPersonService.savePosition(deptId, ids);
    }

    /**
     * 查询组织架构人员
     *
     * @param treeType 树类型
     * @param name 搜索词
     * @return
     */
    @GetMapping(value = "/searchOrgTree")
    public Y9Result<List<OrgUnit>> searchOrgTree(@RequestParam OrgTreeTypeEnum treeType, @RequestParam String name) {
        List<OrgUnit> orgUnitList =
            orgUnitManager.treeSearch(Y9LoginUserHolder.getTenantId(), name, treeType).getData();
        return Y9Result.success(orgUnitList, "获取成功");
    }

    /**
     * 保存是否可以收文
     *
     * @param ids 人员ids
     * @param receive 是否收文
     * @return
     */
    @PostMapping(value = "/setReceive")
    public Y9Result<String> setReceive(@RequestParam boolean receive, @RequestParam String ids) {
        return receiveDeptAndPersonService.setReceive(receive, ids);
    }

    /**
     * 保存是否可以发文
     *
     * @param ids 人员ids
     * @param send 是否发文
     * @return
     */
    @PostMapping(value = "/setSend")
    public Y9Result<String> setSend(@RequestParam boolean send, @RequestParam String ids) {
        return receiveDeptAndPersonService.setSend(send, ids);
    }
}
