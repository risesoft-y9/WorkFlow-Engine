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
import net.risesoft.entity.Entrust;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.EntrustService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/entrust")
public class EntrustController {

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private SpmApproveItemRepository spmApproveItemRepository;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private OrganizationApi organizationManager;

    /**
     * 委办局树搜索
     *
     * @param name 搜索词
     * @return
     */
    @RequestMapping(value = "/deptTreeSearch", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> deptTreeSearch(@RequestParam String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = new ArrayList<>();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        OrgUnit orgUnit = personManager.getBureau(tenantId, Y9LoginUserHolder.getPersonId()).getData();
        if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())) {
            List<Person> personList =
                departmentManager.listAllPersonsByDisabledAndName(tenantId, orgUnit.getId(), false, name).getData();
            for (Person person : personList) {
                orgUnitList.add(person);
                Person p = personManager.getPerson(tenantId, person.getId()).getData();
                this.recursionUpToOrg(tenantId, orgUnit.getId(), p.getParentId(), orgUnitList, false);
            }
        } else {
            orgUnitList = orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
        }
        for (OrgUnit orgUnit0 : orgUnitList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", orgUnit0.getId());
            map.put("name", orgUnit0.getName());
            map.put("orgType", orgUnit0.getOrgType());
            map.put("parentId", orgUnit0.getParentId());
            map.put("isParent", true);
            map.put("guidpath", orgUnit0.getGuidPath());
            if (OrgTypeEnum.PERSON.equals(orgUnit0.getOrgType())) {
                Person per = personManager.getPerson(Y9LoginUserHolder.getTenantId(), orgUnit0.getId()).getData();
                map.put("sex", per.getSex());
                map.put("duty", per.getDuty());
                map.put("isParent", false);
            }
            item.add(map);
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取部门树
     *
     * @param id 部门id
     * @return
     */
    @RequestMapping(value = "/getDeptChildTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getDeptChildTree(@RequestParam(required = false) String id) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList = new ArrayList<>();
            orgList = orgUnitManager.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
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
                    Person person = personManager.getPerson(tenantId, orgunit.getId()).getData();
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

    @RequestMapping(value = "/getDeptTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getDeptTree() {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrgUnit orgUnit = personManager.getBureau(tenantId, Y9LoginUserHolder.getPersonId()).getData();
        if (orgUnit != null && orgUnit.getId() != null) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", orgUnit.getId());
            map.put("parentId", orgUnit.getParentId());
            map.put("name", orgUnit.getName());
            map.put("orgType", orgUnit.getOrgType());
            item.add(map);
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取委托信息
     *
     * @param id 委托id
     * @return
     */
    @RequestMapping(value = "/getEntrustInfo", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getEntrustInfo(String id) {
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotEmpty(id)) {
            Entrust entrust = entrustService.findOne(id);
            if (entrust.getItemId().equals(Entrust.ITEMID4ALL)) {
                entrust.setItemName(Entrust.ITEMNAME4ALL);
            } else {
                SpmApproveItem item = spmApproveItemRepository.findById(entrust.getItemId()).orElse(null);
                entrust.setItemName(item != null ? item.getName() : "事项不存在");
            }
            map.put("entrust", entrust);
        }
        List<SpmApproveItem> itemList = spmApproveItemRepository.findAll();
        List<SpmApproveItem> list = new ArrayList<>();
        Integer count = entrustService.getCountByOwnerIdAndItemId(Y9LoginUserHolder.getPersonId(), Entrust.ITEMID4ALL);
        if (count == 0) {
            SpmApproveItem item = new SpmApproveItem();
            item.setId(Entrust.ITEMID4ALL);
            item.setName(Entrust.ITEMNAME4ALL);
            list.add(item);
        }
        for (SpmApproveItem item : itemList) {
            Integer count1 = entrustService.getCountByOwnerIdAndItemId(Y9LoginUserHolder.getPersonId(), item.getId());
            if (count1 == 0) {
                list.add(item);
            }
        }
        return Y9Result.success(map, "获取成功");
    }

    public OrgUnit getParent(String tenantId, String nodeId, String parentId) {
        Organization parent = organizationManager.getOrganization(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentManager.getDepartment(tenantId, parentId).getData();
    }

    /**
     * 委托列表
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Entrust>> list() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        List<Entrust> entrustList = new ArrayList<>();
        entrustList = entrustService.list(userInfo.getPersonId());
        return Y9Result.success(entrustList, "获取成功");
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
     * 删除委托对象实体
     *
     * @param id
     */
    @RequestMapping(value = "/removeEntrust", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeEntrust(String id) {
        entrustService.removeEntrust(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或者更新委托对象实体
     *
     * @param entrust
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(Entrust entrust) {
        try {
            entrustService.saveOrUpdate(entrust);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
