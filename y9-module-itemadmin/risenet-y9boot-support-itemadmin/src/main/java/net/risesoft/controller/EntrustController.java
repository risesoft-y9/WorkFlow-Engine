package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.vo.NodeTreeVO;
import net.risesoft.entity.Item;
import net.risesoft.entity.entrust.Entrust;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.EntrustService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/entrust", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustController {

    private final EntrustService entrustService;

    private final ItemRepository itemRepository;

    private final PersonApi personApi;

    private final DepartmentApi departmentApi;

    private final OrgUnitApi orgUnitApi;

    private final OrganizationApi organizationApi;

    /**
     * 委办局树搜索
     *
     * @param name 搜索词
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/deptTreeSearch")
    public Y9Result<List<NodeTreeVO>> deptTreeSearch(@RequestParam String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<NodeTreeVO> item = new ArrayList<>();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getPersonId()).getData();
        if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())) {
            List<Person> personList =
                personApi.listRecursivelyByParentIdAndName(tenantId, orgUnit.getId(), name).getData();
            for (Person person : personList) {
                orgUnitList.add(person);
                Person p = personApi.get(tenantId, person.getId()).getData();
                this.recursionUpToOrg(tenantId, orgUnit.getId(), p.getParentId(), orgUnitList, false);
            }
        } else {
            orgUnitList = orgUnitApi.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
        }
        for (OrgUnit orgUnit0 : orgUnitList) {
            NodeTreeVO map = new NodeTreeVO();
            map.setId(orgUnit0.getId());
            map.setName(orgUnit0.getName());
            map.setOrgType(orgUnit0.getOrgType().getValue());
            map.setParentId(orgUnit0.getParentId());
            map.setIsParent(true);
            if (OrgTypeEnum.PERSON.equals(orgUnit0.getOrgType())) {
                Person per = personApi.get(Y9LoginUserHolder.getTenantId(), orgUnit0.getId()).getData();
                map.setSex(per.getSex().getValue());
                map.setDuty(per.getDuty());
                map.setIsParent(false);
            }
            item.add(map);
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取部门树
     *
     * @param id 部门id
     * @return Y9Result<List < NodeTreeVO>>
     */
    @GetMapping(value = "/getDeptTree")
    public Y9Result<List<NodeTreeVO>> getDeptTree(@RequestParam(required = false) String id) {
        List<NodeTreeVO> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(id)) {
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getPersonId()).getData();
            if (orgUnit != null && orgUnit.getId() != null) {
                NodeTreeVO map = new NodeTreeVO();
                id = orgUnit.getId();
                map.setId(id);
                map.setName(orgUnit.getName());
                map.setOrgType(orgUnit.getOrgType().getValue());
                map.setParentId(orgUnit.getParentId());
                map.setIsParent(true);
                item.add(map);
            }
        }
        if (StringUtils.isNotBlank(id)) {
            List<OrgUnit> orgList;
            orgList = orgUnitApi.getSubTree(tenantId, id, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
            for (OrgUnit orgunit : orgList) {
                NodeTreeVO map = new NodeTreeVO();

                map.setId(orgunit.getId());
                map.setName(orgunit.getName());
                map.setOrgType(orgunit.getOrgType().getValue());
                map.setParentId(orgunit.getParentId());

                if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                    map.setIsParent(true);
                } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                    Person person = personApi.get(tenantId, orgunit.getId()).getData();
                    map.setSex(person.getSex().getValue());
                    map.setDuty(person.getDuty());
                    map.setIsParent(false);
                } else {
                    continue;
                }
                item.add(map);
            }
        }
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取委托信息
     *
     * @param id 委托id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getEntrustInfo")
    public Y9Result<Map<String, Object>> getEntrustInfo(String id) {
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotEmpty(id)) {
            Entrust entrust = entrustService.getById(id);
            if (entrust.getItemId().equals(Entrust.ITEMID4ALL)) {
                entrust.setItemName(Entrust.ITEMNAME4ALL);
            } else {
                Item item = itemRepository.findById(entrust.getItemId()).orElse(null);
                entrust.setItemName(item != null ? item.getName() : "事项不存在");
            }
            map.put("entrust", entrust);
        }
        List<Item> itemList = itemRepository.findAll();
        List<Item> list = new ArrayList<>();
        Integer count = entrustService.getCountByOwnerIdAndItemId(Y9LoginUserHolder.getPersonId(), Entrust.ITEMID4ALL);
        if (count == 0) {
            Item item = new Item();
            item.setId(Entrust.ITEMID4ALL);
            item.setName(Entrust.ITEMNAME4ALL);
            list.add(item);
        }
        for (Item item : itemList) {
            Integer count1 = entrustService.getCountByOwnerIdAndItemId(Y9LoginUserHolder.getPersonId(), item.getId());
            if (count1 == 0) {
                list.add(item);
            }
        }
        map.put("itemList", list);
        return Y9Result.success(map, "获取成功");
    }

    public OrgUnit getParent(String tenantId, String parentId) {
        Organization parent = organizationApi.get(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentApi.get(tenantId, parentId).getData();
    }

    /**
     * 委托列表
     *
     * @return Y9Result<List < Entrust>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<Entrust>> list() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        List<Entrust> entrustList = entrustService.list(person.getPersonId());
        return Y9Result.success(entrustList, "获取成功");
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
     * 删除委托对象实体
     *
     * @param id 委托id
     */
    @PostMapping(value = "/removeEntrust")
    public Y9Result<String> removeEntrust(String id) {
        entrustService.removeEntrust(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或者更新委托对象实体
     *
     * @param entrust 委托对象实体
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(Entrust entrust) {
        try {
            entrustService.saveOrUpdate(entrust);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
        }
        return Y9Result.failure("保存失败");
    }

}
