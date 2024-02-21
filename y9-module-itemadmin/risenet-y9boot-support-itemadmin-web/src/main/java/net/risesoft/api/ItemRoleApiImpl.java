package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.service.RoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/itemRole")
public class ItemRoleApiImpl implements ItemRoleApi {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private OrganizationApi organizationManager;

    @Override
    @GetMapping(value = "/findCsUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUser(String tenantId, String userId, String id, Integer principalType,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findCsUser(id, principalType, processInstanceId);
        return listMap;
    }

    @Override
    @GetMapping(value = "/findCsUserBureau", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUserBureau(String tenantId, String userId, Integer principalType) {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        if (principalType == ItemPrincipalTypeEnum.DEPT.getValue()) {
            OrgUnit orgunit = personManager.getBureau(tenantId, userId).getData();
            Map<String, Object> map = new HashMap<String, Object>(16);
            String orgunitId = orgunit.getId();
            map.put("id", orgunitId);
            map.put("name", orgunit.getName());
            map.put("isPerm", true);
            map.put("orgType", orgunit.getOrgType());
            map.put("isParent", OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()) ? true : false);
            item.add(map);
        } else {
            // TODO
        }
        return item;
    }

    @Override
    @GetMapping(value = "/findCsUserSearch", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUserSearch(String tenantId, String userId, String name,
        Integer principalType, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findCsUserSearch(name, principalType, processInstanceId);
        return listMap;
    }

    @Override
    @GetMapping(value = "/findPermUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUser(String tenantId, String userId, String itemId,
        String processDefinitionId, String taskDefKey, Integer principalType, String id, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap =
            roleService.findPermUser(itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
        return listMap;
    }

    @Override
    @GetMapping(value = "/findPermUserByName", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUserByName(String tenantId, String userId, String name,
        Integer principalType, String itemId, String processDefinitionId, String taskDefKey, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findPermUserByName(name, itemId, processDefinitionId, taskDefKey, principalType,
            processInstanceId);
        return listMap;
    }

    @Override
    @GetMapping(value = "/findPermUserSendReceive", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUserSendReceive(String tenantId, String userId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findPermUserSendReceive(id);
        return listMap;
    }

    @Override
    @GetMapping(value = "/getOrgTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getOrgTree(String tenantId, String userId, String id, OrgTreeTypeEnum treeType,
        String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            List<Organization> org = organizationManager.listAllOrganizations(tenantId).getData();
            if (org != null && org.size() > 0) {
                id = org.get(0).getId();
            }
        }
        List<OrgUnit> orgUnitList;
        if (StringUtils.isNotBlank(name)) {
            orgUnitList = orgUnitManager.treeSearch(tenantId, name, treeType).getData();
        } else {
            orgUnitList = orgUnitManager.getSubTree(tenantId, id, treeType).getData();
        }
        for (int i = 0; i < orgUnitList.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", orgUnitList.get(i).getId());
            map.put("customID", orgUnitList.get(i).getCustomId());
            map.put("name", orgUnitList.get(i).getName());
            map.put("orgType", orgUnitList.get(i).getOrgType());
            map.put("parentID", orgUnitList.get(i).getParentId());
            map.put("DN", orgUnitList.get(i).getDn());
            if (OrgTypeEnum.DEPARTMENT.equals(orgUnitList.get(i).getOrgType())) {
                map.put("isParent", true);
            } else if (OrgTypeEnum.PERSON.equals(orgUnitList.get(i).getOrgType())) {
                person = personManager.getPerson(tenantId, orgUnitList.get(i).getId()).getData();
                if (person.getDisabled()) {
                    continue;
                }
                map.put("person", "3:" + person.getId());
                if (person.getDuty() != null && !"".equals(person.getDuty())) {
                    map.put("name", person.getName() + "(" + person.getDuty() + ")");
                } else {
                    map.put("name", person.getName());
                }
                map.put("sex", person.getSex());
                map.put("duty", person.getDuty());
            }
            item.add(map);
        }
        return item;
    }
}
