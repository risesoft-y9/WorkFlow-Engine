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

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
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
 * @date 2022/12/22
 */
@RestController
@RequestMapping("/vue/sendReceive")
public class SendReceiveRestController {

    @Autowired
    private ReceiveDeptAndPersonService receiveDeptAndPersonService;

    @Autowired
    private OrganizationApi organizationManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private ReceiveDepartmentRepository receiveDepartmentRepository;

    /**
     * 验证是否可以收文
     *
     * @param id 人员ids
     * @param receive 是否收文
     * @return
     */
    @RequestMapping(value = "/checkReceiveSend", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
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
    @RequestMapping(value = "/delPerson", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> delPerson(@RequestParam String id) {
        Map<String, Object> map = receiveDeptAndPersonService.delPerson(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 搜索部门树
     *
     * @param name 搜索词
     * @param deptId 部门id
     * @return
     */
    @RequestMapping(value = "/deptTreeSearch", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> deptTreeSearch(@RequestParam(required = false) String name,
        @RequestParam String deptId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = new ArrayList<>();
        List<Person> personList = personManager.listRecursivelyByParentIdAndName(tenantId, deptId, name).getData();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (Person person : personList) {
            orgUnitList.add(person);
            Person p = personManager.get(tenantId, person.getId()).getData();
            this.recursionUpToOrg(tenantId, deptId, p.getParentId(), orgUnitList, false);
        }
        for (OrgUnit orgUnit : orgUnitList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", orgUnit.getId());
            map.put("name", orgUnit.getName());
            map.put("orgType", orgUnit.getOrgType());
            map.put("parentId", orgUnit.getParentId());
            map.put("isParent", true);
            if (OrgTypeEnum.PERSON.equals(orgUnit.getOrgType())) {
                Person per = personManager.get(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                map.put("sex", per.getSex());
                map.put("duty", per.getDuty());
                map.put("isParent", false);
            }
            item.add(map);
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
    @RequestMapping(value = "/getDeptTree", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getDeptTrees(@RequestParam(required = false) String id,
        @RequestParam(required = false) String deptId) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(deptId)) {
            Department dept = departmentManager.get(tenantId, deptId).getData();
            if (dept != null && dept.getId() != null) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", dept.getId());
                map.put("parentId", dept.getParentId());
                map.put("name", dept.getName());
                map.put("isParent", true);
                map.put("orgType", dept.getOrgType());
                item.add(map);
            }
        }
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList = new ArrayList<>();
            orgList = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_ORG).getData();
            for (OrgUnit orgunit : orgList) {
                Map<String, Object> map = new HashMap<>(16);
                String orgunitId = orgunit.getId();
                map.put("id", orgunitId);
                map.put("parentId", id);
                map.put("name", orgunit.getName());
                map.put("orgType", orgunit.getOrgType());
                if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                    map.put("isParent", true);
                } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                    Person person = personManager.get(tenantId, orgunit.getId()).getData();
                    map.put("isParent", false);
                    map.put("sex", person.getSex());
                    map.put("duty", person.getDuty());
                } else {
                    continue;
                }
                item.add(map);
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 组织机构列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getOrg", method = RequestMethod.GET, produces = "application/json")
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
    @RequestMapping(value = "/getOrgChildTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getOrgChildTree(@RequestParam(required = false) String id,
        OrgTreeTypeEnum treeType) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList = new ArrayList<>();
            orgList = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
            for (OrgUnit orgunit : orgList) {
                Map<String, Object> map = new HashMap<>(16);
                String orgUnitId = orgunit.getId();
                map.put("id", orgUnitId);
                map.put("parentId", id);
                map.put("name", orgunit.getName());
                map.put("orgType", orgunit.getOrgType());
                map.put("guidPath", orgunit.getGuidPath());
                if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                    map.put("isParent", true);
                } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                    Person person = personManager.get(tenantId, orgunit.getId()).getData();
                    map.put("isParent", false);
                    map.put("sex", person.getSex());
                    map.put("duty", person.getDuty());
                } else {
                    continue;
                }
                item.add(map);
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
    @ResponseBody
    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> getOrgTree(@RequestParam String id, @RequestParam OrgTreeTypeEnum treeType) {
        List<OrgUnit> newOrgUnitList = new ArrayList<>();
        List<OrgUnit> orgUnitList = orgUnitManager.getSubTree(Y9LoginUserHolder.getTenantId(), id, treeType).getData();
        for (OrgUnit orgUnit : orgUnitList) {
            if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())) {
                // 该部门下没有收发部门
                orgUnit.setDn("false");
                ReceiveDepartment receiveDepartment = receiveDeptAndPersonService.findByDeptId(orgUnit.getId());
                List<Department> deptList = departmentManager
                    .listRecursivelyByParentId(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                // 用来判断下面是否有子部门
                orgUnit.setGuidPath("false");
                // 判断该部门下是否有收发部门
                for (Department dept : deptList) {
                    orgUnit.setGuidPath(UtilConsts.TRUE);
                    ReceiveDepartment receiveDept = receiveDeptAndPersonService.findByDeptId(dept.getId());
                    // 该部门下有收发部门
                    if (receiveDept != null) {
                        orgUnit.setDn(UtilConsts.TRUE);
                        break;
                    }
                }
                // 是否是收发部门，true为是
                orgUnit.setCustomId("false");
                // 是否已配置收发员，true为是
                orgUnit.setProperties("false");
                if (receiveDepartment != null) {
                    // 是否是收发部门，true为是
                    orgUnit.setCustomId(UtilConsts.TRUE);
                    Integer count = receiveDeptAndPersonService.countByDeptId(orgUnit.getId());
                    if (count > 0) {
                        // 是否已配置收发员，true为是
                        orgUnit.setProperties(UtilConsts.TRUE);
                    }
                }
                newOrgUnitList.add(orgUnit);
            }
        }
        return Y9Result.success(newOrgUnitList, "获取成功");
    }

    public OrgUnit getParent(String tenantId, String nodeId, String parentId) {
        Organization parent = organizationManager.get(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentManager.get(tenantId, parentId).getData();
    }

    @RequestMapping(value = "/orderDeptList")
    @ResponseBody
    public Map<String, Object> orderDeptList() {
        Map<String, Object> map = new HashMap<>(16);
        List<ReceiveDepartment> list = receiveDepartmentRepository.findAllOrderByTabIndex();
        for (ReceiveDepartment receiveDeptAndPerson : list) {
            Department department =
                departmentManager.get(Y9LoginUserHolder.getTenantId(), receiveDeptAndPerson.getDeptId()).getData();
            receiveDeptAndPerson.setDeptName(department.getName());
        }
        map.put("rows", list);
        return map;
    }

    /**
     * 查询组织架构人员
     *
     * @param treeType 树类型
     * @param name 搜索词
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/orgTreeSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> orgTreeSearch(@RequestParam OrgTreeTypeEnum treeType, @RequestParam String name) {
        List<OrgUnit> newOrgUnitList = new ArrayList<>();
        List<OrgUnit> orgUnitList =
            orgUnitManager.treeSearch(Y9LoginUserHolder.getTenantId(), name, treeType).getData();
        for (OrgUnit orgUnit : orgUnitList) {
            if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())) {
                // 该部门下没有收发部门
                orgUnit.setDn("false");
                ReceiveDepartment receiveDepartment = receiveDeptAndPersonService.findByDeptId(orgUnit.getId());
                List<Department> deptList = departmentManager
                    .listRecursivelyByParentId(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
                // 用来判断下面是否有子部门
                orgUnit.setGuidPath("false");
                // 判断该部门下是否有收发部门
                for (Department dept : deptList) {
                    orgUnit.setGuidPath(UtilConsts.TRUE);
                    ReceiveDepartment receiveDept = receiveDeptAndPersonService.findByDeptId(dept.getId());
                    if (receiveDept != null) {
                        // 该部门下有收发部门
                        orgUnit.setDn(UtilConsts.TRUE);
                        break;
                    }
                }
                // 是否是收发部门，true为是
                orgUnit.setCustomId("false");
                // 是否已配置收发员，true为是
                orgUnit.setProperties("false");
                if (receiveDepartment != null) {
                    // 是否是收发部门，true为是
                    orgUnit.setCustomId(UtilConsts.TRUE);
                    Integer count = receiveDeptAndPersonService.countByDeptId(orgUnit.getId());
                    if (count > 0) {
                        // 是否已配置收发员，true为是
                        orgUnit.setProperties(UtilConsts.TRUE);
                    }
                }
                newOrgUnitList.add(orgUnit);
            }
        }
        List<Organization> list = organizationManager.list(Y9LoginUserHolder.getTenantId()).getData();
        if (list.size() > 0) {
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
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/personList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> personList(@RequestParam String deptId) {
        Map<String, Object> map = receiveDeptAndPersonService.personList(deptId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((List<Map<String, Object>>)map.get("rows"), "获取成功");
        }
        return Y9Result.failure("获取失败");
    }

    public void recursionUpToOrg(String tenantId, String nodeId, String parentId, List<OrgUnit> orgUnitList,
        boolean isParent) {
        OrgUnit parent = getParent(tenantId, nodeId, parentId);
        if (isParent) {
            parent.setDescription("parent");
        }
        if (orgUnitList.size() == 0) {
            orgUnitList.add(parent);
        } else {
            String add = UtilConsts.TRUE;
            for (OrgUnit orgUnit : orgUnitList) {
                if (orgUnit.getId().equals(parent.getId())) {
                    add = "false";
                    break;
                }
            }
            if (add == UtilConsts.TRUE) {
                orgUnitList.add(parent);
            }
        }
        if (OrgTypeEnum.DEPARTMENT.equals(parent.getOrgType())) {
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
    @RequestMapping(value = "/saveOrCancelDept", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveOrCancelDept(@RequestParam String id, @RequestParam String type) {
        Map<String, Object> map = new HashMap<>(16);
        boolean b = "save".equals(type);
        if (b) {
            map = receiveDeptAndPersonService.saveDepartment(id);
        } else {
            map = receiveDeptAndPersonService.delDepartment(id);
        }
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存排序
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/saveOrder")
    @ResponseBody
    public Map<String, Object> saveOrder(String ids) {
        Map<String, Object> map = new HashMap<>(16);
        map = receiveDeptAndPersonService.saveOrder(ids);
        return map;
    }

    /**
     * 设置收发员
     *
     * @param deptId 部门id
     * @param ids 人员ids
     * @return
     */
    @RequestMapping(value = "/savePerson", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> savePerson(@RequestParam String deptId, @RequestParam String ids) {
        Map<String, Object> map = receiveDeptAndPersonService.savePerson(deptId, ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 查询组织架构人员
     *
     * @param treeType 树类型
     * @param name 搜索词
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/searchOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<OrgUnit>> searchOrgTree(@RequestParam OrgTreeTypeEnum treeType, @RequestParam String name) {
        List<OrgUnit> orgUnitList =
            orgUnitManager.treeSearch(Y9LoginUserHolder.getTenantId(), name, treeType).getData();
        return Y9Result.success(orgUnitList, "获取成功");
    }

    /**
     * 保存是否可以收文
     *
     * @param id 人员ids
     * @param receive 是否收文
     * @return
     */
    @RequestMapping(value = "/setReceive", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> setReceive(@RequestParam boolean receive, @RequestParam String ids) {
        Map<String, Object> map = receiveDeptAndPersonService.setReceive(receive, ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("保存成功");
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存是否可以发文
     *
     * @param id 人员ids
     * @param send 是否发文
     * @return
     */
    @RequestMapping(value = "/setSend", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> setSend(@RequestParam boolean send, @RequestParam String ids) {
        Map<String, Object> map = receiveDeptAndPersonService.setSend(send, ids);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("保存成功");
        }
        return Y9Result.failure("保存失败");
    }

}
