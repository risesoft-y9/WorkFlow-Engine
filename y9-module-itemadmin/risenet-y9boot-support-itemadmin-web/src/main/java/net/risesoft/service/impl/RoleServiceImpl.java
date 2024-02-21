package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.customgroup.CustomGroupApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.OrganizationApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.RoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemPermission;
import net.risesoft.entity.ReceiveDepartment;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ReceiveDepartmentRepository;
import net.risesoft.service.DynamicRoleMemberService;
import net.risesoft.service.ItemPermissionService;
import net.risesoft.service.RoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private ItemPermissionService itemPermissionService;

    @Autowired
    private CustomGroupApi customGroupApi;

    @Autowired
    private DynamicRoleMemberService dynamicRoleMemberService;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private OrganizationApi organizationManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private ReceiveDepartmentRepository receiveDepartmentRepository;

    @Override
    public List<Map<String, Object>> bureauTreeSearch(String name, String nodeId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = new ArrayList<>();
        List<Person> personList =
            departmentManager.listAllPersonsByDisabledAndName(tenantId, nodeId, false, name).getData();
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (int i = 0; i < personList.size(); i++) {
            orgUnitList.add(personList.get(i));
            Person p = personManager.getPerson(tenantId, personList.get(i).getId()).getData();
            this.recursionUpToOrg(tenantId, nodeId, p.getParentId(), orgUnitList, false);
        }
        for (int i = 0; i < orgUnitList.size(); i++) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", orgUnitList.get(i).getId());
            map.put("customId", orgUnitList.get(i).getCustomId());
            map.put("name", orgUnitList.get(i).getName());
            map.put("orgType", orgUnitList.get(i).getOrgType());
            map.put("parentId", orgUnitList.get(i).getParentId());
            map.put("dn", orgUnitList.get(i).getDn());
            if (OrgTypeEnum.PERSON.equals(orgUnitList.get(i).getOrgType())) {
                Person per =
                    personManager.getPerson(Y9LoginUserHolder.getTenantId(), orgUnitList.get(i).getId()).getData();
                map.put("sex", per.getSex());
                map.put("duty", per.getDuty());
            }
            item.add(map);
        }
        return item;
    }

    @Override
    public List<Map<String, Object>> findCsUser(String id, Integer principalType, String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            if (StringUtils.isBlank(id) || UtilConsts.NULL.equals(id)) {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    Organization organization = orgUnitManager.getOrganization(tenantId, userId).getData();
                    List<Department> deptList =
                        organizationManager.listDepartments(tenantId, organization.getId()).getData();
                    for (Department dept : deptList) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", dept.getId());
                        map.put("parentID", id);
                        map.put("name", dept.getName());
                        map.put("isParent", true);
                        map.put("orgType", dept.getOrgType());
                        map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                        item.add(map);
                    }
                } else {
                    List<CustomGroup> grouplist = customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                    for (CustomGroup customGroup : grouplist) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", customGroup.getId());
                        map.put("pId", customGroup.getId());
                        map.put("name", customGroup.getGroupName());
                        map.put("isPerm", true);
                        map.put("isParent", true);
                        map.put("orgType", "customGroup");
                        map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                }
            } else {
                if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                    List<OrgUnit> orgList = new ArrayList<>();
                    orgList.addAll(departmentManager.listSubDepartments(tenantId, id).getData());
                    orgList.addAll(departmentManager.listPersonsByDisabled(tenantId, id, false).getData());
                    for (OrgUnit orgunit : orgList) {
                        Map<String, Object> map = new HashMap<>(16);
                        String orgunitId = orgunit.getId();
                        map.put("id", orgunitId);
                        map.put("pId", id);
                        map.put("name", orgunit.getName());
                        map.put("isPerm", true);
                        map.put("orgType", orgunit.getOrgType());
                        map.put("isParent", OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()));
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                            Person person = personManager.getPerson(tenantId, orgunit.getId()).getData();
                            map.put("sex", person.getSex());
                            map.put("duty", person.getDuty());
                            map.put("person", "3:" + person.getId() + ":" + id);
                            map.put("personType", person.isOriginal());
                            map.put("principalType", ItemPermissionEnum.USER.getValue());
                            if (person.getDuty() != null && !"".equals(person.getDuty())) {
                                map.put("name", person.getName() + "(" + person.getDuty() + ")");
                            } else {
                                map.put("name", person.getName());
                            }
                        }
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, id, OrgTypeEnum.PERSON)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Person person =
                                personManager.getPerson(tenantId, customGroupMember.getMemberId()).getData();
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("id", customGroupMember.getMemberId());
                            map.put("pId", id);
                            map.put("name", customGroupMember.getMemberName());
                            map.put("isPerm", true);
                            map.put("isParent", false);
                            map.put("orgType", person.getOrgType());
                            map.put("sex", person.getSex());
                            map.put("duty", person.getDuty());
                            map.put("person", "3:" + person.getId() + ":" + customGroupMember.getParentId());
                            map.put("principalType", ItemPermissionEnum.USER.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<Map<String, Object>> findCsUserSearch(String name, Integer principalType, String processInstanceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String personId = userInfo.getPersonId();
        List<Map<String, Object>> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            Organization organization = orgUnitManager.getOrganization(tenantId, personId).getData();
            List<Department> deptList = organizationManager.listDepartments(tenantId, organization.getId()).getData();
            List<OrgUnit> orgUnitListTemp = new ArrayList<>();
            for (OrgUnit orgUnitTemp : deptList) {
                orgUnitListTemp.addAll(orgUnitManager
                    .treeSearchByDn(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_ORG, orgUnitTemp.getDn()).getData());
            }
            for (OrgUnit orgUnitTemp : orgUnitListTemp) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", orgUnitTemp.getId());
                map.put("name", orgUnitTemp.getName());
                map.put("pId", orgUnitTemp.getParentId());
                map.put("orgType", orgUnitTemp.getOrgType());
                if (OrgTypeEnum.DEPARTMENT.equals(orgUnitTemp.getOrgType())) {
                    map.put("isParent", true);
                } else if (OrgTypeEnum.ORGANIZATION.equals(orgUnitTemp.getOrgType())) {
                    map.put("isParent", true);
                    continue;
                } else if (OrgTypeEnum.PERSON.equals(orgUnitTemp.getOrgType())) {
                    Person p = personManager.getPerson(Y9LoginUserHolder.getTenantId(), orgUnitTemp.getId()).getData();
                    if (Boolean.TRUE.equals(p.getDisabled())) {
                        continue;
                    }
                    map.put("person", "3:" + p.getId() + ":" + p.getParentId());
                    if (p.getDuty() != null && !"".equals(p.getDuty())) {
                        map.put("name", p.getName() + "(" + p.getDuty() + ")");
                    } else {
                        map.put("name", p.getName());
                    }
                    map.put("personType", p.isOriginal());
                    map.put("sex", p.getSex());
                    map.put("duty", p.getDuty());
                    map.put("isParent", false);
                }
                if (!item.contains(map)) {
                    item.add(map);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist = customGroupApi
                    .listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
                for (CustomGroup customGroup : grouplist) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", customGroup.getId());
                    map.put("pId", customGroup.getId());
                    map.put("name", customGroup.getGroupName());
                    map.put("isParent", true);
                    map.put("orgType", "customGroup");
                    map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getUserInfo().getPersonId(), customGroup.getId(), OrgTypeEnum.PERSON)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Person person1 =
                                personManager.getPerson(tenantId, customGroupMember.getMemberId()).getData();
                            if (person1 != null && person1.getName().contains(name)) {
                                Map<String, Object> map0 = new HashMap<>(16);
                                map0.put("id", customGroupMember.getMemberId());
                                map0.put("pId", customGroup.getId());
                                map0.put("isParent", false);
                                map0.put("orgType", person1.getOrgType());
                                map0.put("sex", person1.getSex());
                                map0.put("duty", person1.getDuty());
                                map0.put("person", "3:" + person1.getId() + ":" + person1.getParentId());
                                map0.put("principalType", ItemPermissionEnum.USER.getValue());
                                if (person1.getDuty() != null && !"".equals(person1.getDuty())) {
                                    map0.put("name", person1.getName() + "(" + person1.getDuty() + ")");
                                } else {
                                    map0.put("name", person1.getName());
                                }
                                if (item.contains(map0)) {
                                    continue;// 去重
                                }
                                item.add(map0);
                                b = true;
                            }
                        }
                        if (b) {
                            item.add(map);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return item;
    }

    @Override
    public List<Map<String, Object>> findPermUser(String itemId, String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            List<ItemPermission> list = itemPermissionService
                .findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId, processDefinitionId, taskDefKey);
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                if (StringUtils.isBlank(id)) {
                    List<OrgUnit> deptList = new ArrayList<>();
                    for (ItemPermission o : list) {
                        if (o.getRoleType() == 1) {
                            deptList.addAll(roleManager
                                .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                            deptList.addAll(roleManager
                                .listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                        }
                        if (o.getRoleType() == 2) {
                            deptList.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                        }
                        if (o.getRoleType() == 4) {
                            List<OrgUnit> orgUnitList =
                                dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                            for (OrgUnit orgUnit : orgUnitList) {
                                if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())
                                    || OrgTypeEnum.ORGANIZATION.equals(orgUnit.getOrgType())) {
                                    deptList.add(orgUnit);
                                }
                            }
                        }
                    }
                    for (OrgUnit org : deptList) {
                        if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                            List<Department> deptList1 =
                                organizationManager.listDepartments(tenantId, org.getId()).getData();
                            List<Person> personList1 = organizationManager.listPersons(tenantId, org.getId()).getData();
                            for (Department dept : deptList1) {
                                Map<String, Object> map = new HashMap<>(16);
                                map.put("id", dept.getId());
                                map.put("parentId", id);
                                map.put("name", dept.getName());
                                map.put("isPerm", true);
                                map.put("isParent", true);
                                map.put("orgType", dept.getOrgType());
                                map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                                if (item.contains(map)) {
                                    continue;// 去重
                                }
                                item.add(map);
                            }
                            for (Person p : personList1) {
                                Map<String, Object> map = new HashMap<>(16);
                                Person person = personManager.getPerson(tenantId, p.getId()).getData();
                                map.put("id", person.getId());
                                map.put("parentId", id);
                                map.put("sex", person.getSex());
                                map.put("duty", person.getDuty());
                                map.put("person", "3:" + person.getId());
                                map.put("principalType", ItemPermissionEnum.USER.getValue());
                                map.put("name", person.getName());
                                if (person.getDuty() != null && !"".equals(person.getDuty())) {
                                    map.put("name", person.getName() + "(" + person.getDuty() + ")");
                                }
                                map.put("isParent", false);
                                map.put("orgType", person.getOrgType());
                                if (item.contains(map)) {
                                    continue;
                                }
                                item.add(map);
                            }
                        } else {
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("id", org.getId());
                            map.put("parentId", id);
                            map.put("name", org.getName());
                            map.put("isPerm", true);
                            map.put("isParent", true);
                            map.put("orgType", org.getOrgType() == null ? OrgTypeEnum.ORGANIZATION.getEnName()
                                : org.getOrgType().getEnName());
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                } else {// 取部门下的部门或人员
                    List<OrgUnit> orgList = new ArrayList<>();
                    orgList.addAll(departmentManager.listSubDepartments(tenantId, id).getData());
                    orgList.addAll(departmentManager.listPersonsByDisabled(tenantId, id, false).getData());
                    for (OrgUnit orgunit : orgList) {
                        Map<String, Object> map = new HashMap<>(16);
                        String orgunitId = orgunit.getId();
                        map.put("id", orgunitId);
                        map.put("parentId", id);
                        map.put("name", orgunit.getName());
                        map.put("isPerm", true);
                        map.put("orgType", orgunit.getOrgType());
                        map.put("isParent", OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()));
                        if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                            map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                        } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                            Person person = personManager.getPerson(tenantId, orgunit.getId()).getData();
                            map.put("sex", person.getSex());
                            map.put("duty", person.getDuty());
                            map.put("personType", person.isOriginal());
                            map.put("person", "3:" + person.getId());
                            map.put("principalType", ItemPermissionEnum.USER.getValue());
                            if (person.getDuty() != null && !"".equals(person.getDuty())) {
                                map.put("name", person.getName() + "(" + person.getDuty() + ")");
                            } else {
                                map.put("name", person.getName());
                            }
                        }
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                }
            } else if (ItemPrincipalTypeEnum.PERSON.getValue().equals(principalType)) {
                List<OrgUnit> personList = new ArrayList<>();
                for (ItemPermission o : list) {
                    if (o.getRoleType() == 1) {
                        personList.addAll(roleManager.listPersonsById(tenantId, o.getRoleId()).getData());
                    }
                    if (o.getRoleType() == 3) {
                        personList.add(orgUnitManager.getOrgUnit(tenantId, o.getRoleId()).getData());
                    }
                    if (o.getRoleType() == 4) {
                        List<OrgUnit> orgUnitList =
                            dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                        for (OrgUnit orgUnit : orgUnitList) {
                            if (OrgTypeEnum.PERSON.equals(orgUnit.getOrgType())) {
                                personList.add(orgUnit);
                            }
                        }
                    }
                }
                for (OrgUnit person : personList) {
                    Person person1 = personManager.getPerson(tenantId, person.getId()).getData();
                    if (Boolean.TRUE.equals(person1.getDisabled())) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", person.getId());
                    map.put("parentId", person1.getParentId());
                    map.put("person", "3:" + person1.getId());
                    if (person1.getDuty() != null && !"".equals(person1.getDuty())) {
                        map.put("name", person1.getName() + "(" + person1.getDuty() + ")");
                    } else {
                        map.put("name", person1.getName());
                    }
                    map.put("isPerm", true);
                    map.put("isParent", false);
                    map.put("orgType", person1.getOrgType());
                    map.put("sex", person1.getSex());
                    map.put("duty", person1.getDuty());
                    map.put("principalType", ItemPermissionEnum.USER.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    item.add(map);
                }
            } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
                List<CustomGroup> customGrouplist =
                    customGroupApi.listCustomGroupByPersonId(tenantId, userId).getData();
                if (StringUtils.isBlank(id)) {
                    for (CustomGroup customGroup : customGrouplist) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", customGroup.getId());
                        map.put("parentId", id);
                        map.put("name", customGroup.getGroupName());
                        map.put("isPerm", true);
                        map.put("isParent", true);
                        map.put("orgType", "customGroup");
                        map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                } else {
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId, userId, id, OrgTypeEnum.PERSON)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Person person =
                                personManager.getPerson(tenantId, customGroupMember.getMemberId()).getData();
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("id", customGroupMember.getMemberId());
                            map.put("parentId", id);
                            map.put("name", customGroupMember.getMemberName());
                            map.put("isPerm", true);
                            map.put("isParent", false);
                            map.put("orgType", person.getOrgType());
                            map.put("sex", person.getSex());
                            map.put("duty", person.getDuty());
                            map.put("person", "3:" + person.getId());
                            map.put("principalType", ItemPermissionEnum.USER.getValue());
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public List<Map<String, Object>> findPermUserByName(String processDefinitionId, String taskDefKey,
        Integer principalType, String id, String name) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
                // 取部门下的人员
                List<OrgUnit> orgList = new ArrayList<>();
                List<Person> personList = new ArrayList<>();
                personList
                    .addAll(departmentManager.listAllPersonsByDisabledAndName(tenantId, id, false, name).getData());
                for (int i = 0; i < personList.size(); i++) {
                    orgList.add(personList.get(i));
                    Person p = personManager.getPerson(tenantId, personList.get(i).getId()).getData();
                    this.recursionUpToOrg(tenantId, id, p.getParentId(), orgList, false);
                }
                for (OrgUnit orgunit : orgList) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", orgunit.getId());
                    map.put("parentID", orgunit.getParentId());
                    map.put("pId", orgunit.getParentId());
                    map.put("DN", orgunit.getDn());
                    map.put("name", orgunit.getName());
                    map.put("isPerm", true);
                    map.put("orgType", orgunit.getOrgType());
                    map.put("isParent",
                        OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())
                            || OrgTypeEnum.GROUP.equals(orgunit.getOrgType())
                            || OrgTypeEnum.POSITION.equals(orgunit.getOrgType()));
                    if (OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType())) {
                        map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                    } else if (OrgTypeEnum.GROUP.equals(orgunit.getOrgType())) {
                        map.put("principalType", ItemPermissionEnum.GROUP.getValue());
                    } else if (OrgTypeEnum.PERSON.equals(orgunit.getOrgType())) {
                        Person person =
                            personManager.getPerson(Y9LoginUserHolder.getTenantId(), orgunit.getId()).getData();
                        map.put("sex", person.getSex());
                        map.put("duty", person.getDuty());
                        map.put("personType", person.isOriginal());
                        map.put("principalType", ItemPermissionEnum.USER.getValue());
                    } else if (OrgTypeEnum.POSITION.equals(orgunit.getOrgType())) {
                        map.put("principalType", ItemPermissionEnum.POSITION.getValue());
                    }
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    item.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;

    }

    @Override
    public List<Map<String, Object>> findPermUserByName(String name, String itemId, String processDefinitionId,
        String taskDefKey, Integer principalType, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemPermission> list = itemPermissionService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyExtra(itemId,
            processDefinitionId, taskDefKey);
        List<Map<String, Object>> item = new ArrayList<>();
        if (ItemPrincipalTypeEnum.DEPT.getValue().equals(principalType)) {
            List<OrgUnit> deptList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    deptList.addAll(
                        roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.DEPARTMENT).getData());
                    deptList.addAll(
                        roleManager.listOrgUnitsById(tenantId, o.getRoleId(), OrgTypeEnum.ORGANIZATION).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList =
                        dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (OrgTypeEnum.DEPARTMENT.equals(orgUnit.getOrgType())
                            || OrgTypeEnum.ORGANIZATION.equals(orgUnit.getOrgType())) {
                            deptList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 2) {
                    Department dept = departmentManager.getDepartment(tenantId, o.getRoleId()).getData();
                    if (dept != null) {
                        deptList.add(dept);
                    }
                }
            }
            for (OrgUnit org : deptList) {
                if (OrgTypeEnum.DEPARTMENT.equals(org.getOrgType())) {
                    List<Person> personList =
                        departmentManager.listAllPersonsByDisabledAndName(tenantId, org.getId(), false, name).getData();
                    List<OrgUnit> orgUnitList = new ArrayList<>();
                    for (int i = 0; i < personList.size(); i++) {
                        orgUnitList.add(personList.get(i));
                        Person p = personManager.getPerson(tenantId, personList.get(i).getId()).getData();
                        recursionUpToOrg1(tenantId, org.getId(), p.getParentId(), orgUnitList, false);
                    }
                    for (int i = 0; i < orgUnitList.size(); i++) {
                        if (!OrgTypeEnum.ORGANIZATION.equals(orgUnitList.get(i).getOrgType())) {
                            Map<String, Object> map = new HashMap<>(16);
                            map.put("id", orgUnitList.get(i).getId());
                            map.put("customId", orgUnitList.get(i).getCustomId());
                            map.put("name", orgUnitList.get(i).getName());
                            map.put("orgType", orgUnitList.get(i).getOrgType());
                            map.put("pId", orgUnitList.get(i).getParentId());
                            map.put("dn", orgUnitList.get(i).getDn());
                            if (OrgTypeEnum.PERSON.equals(orgUnitList.get(i).getOrgType())) {
                                Person per = personManager
                                    .getPerson(Y9LoginUserHolder.getTenantId(), orgUnitList.get(i).getId()).getData();
                                map.put("sex", per.getSex());
                                map.put("duty", per.getDuty());
                                map.put("person", "3:" + per.getId());
                                map.put("personType", per.isOriginal());
                                if (per.getDuty() != null && !"".equals(per.getDuty())) {
                                    map.put("name", per.getName() + "(" + per.getDuty() + ")");
                                } else {
                                    map.put("name", per.getName());
                                }
                            }
                            if (item.contains(map)) {
                                continue;// 去重
                            }
                            item.add(map);
                        }
                    }
                } else if (OrgTypeEnum.ORGANIZATION.equals(org.getOrgType())) {
                    // 租户组织机构树查询，会查询多个组织机构
                    List<OrgUnit> orgUnitList =
                        orgUnitManager.treeSearch(tenantId, name, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
                    for (int i = 0; i < orgUnitList.size(); i++) {
                        if (OrgTypeEnum.ORGANIZATION.equals(orgUnitList.get(i).getOrgType())) {
                            continue;// 不显示组织机构
                        }
                        // 一个租户下有多个组织架构，如果不是在当前组织机构下的都排除
                        if (!orgUnitList.get(i).getGuidPath().contains(org.getId())) {
                            continue;
                        }
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("id", orgUnitList.get(i).getId());
                        map.put("customId", orgUnitList.get(i).getCustomId());
                        map.put("name", orgUnitList.get(i).getName());
                        map.put("orgType", orgUnitList.get(i).getOrgType());
                        map.put("pId", orgUnitList.get(i).getParentId());
                        map.put("dn", orgUnitList.get(i).getDn());
                        if (OrgTypeEnum.PERSON.equals(orgUnitList.get(i).getOrgType())) {
                            Person per = personManager
                                .getPerson(Y9LoginUserHolder.getTenantId(), orgUnitList.get(i).getId()).getData();
                            map.put("sex", per.getSex());
                            map.put("duty", per.getDuty());
                            map.put("person", "3:" + per.getId());
                            map.put("personType", per.isOriginal());
                            if (per.getDuty() != null && !"".equals(per.getDuty())) {
                                map.put("name", per.getName() + "(" + per.getDuty() + ")");
                            } else {
                                map.put("name", per.getName());
                            }
                        }
                        if (item.contains(map)) {
                            continue;// 去重
                        }
                        item.add(map);
                    }
                }
            }
        } else if (ItemPrincipalTypeEnum.PERSON.getValue().equals(principalType)) {
            List<OrgUnit> personList = new ArrayList<>();
            for (ItemPermission o : list) {
                if (o.getRoleType() == 1) {
                    personList.addAll(roleManager.listPersonsById(tenantId, o.getRoleId()).getData());
                }
                if (o.getRoleType() == 4) {
                    List<OrgUnit> orgUnitList =
                        dynamicRoleMemberService.getOrgUnitList(o.getRoleId(), processInstanceId);
                    for (OrgUnit orgUnit : orgUnitList) {
                        if (OrgTypeEnum.PERSON.equals(orgUnit.getOrgType())) {
                            personList.add(orgUnit);
                        }
                    }
                }
                if (o.getRoleType() == 3) {
                    Person per = personManager.getPerson(Y9LoginUserHolder.getTenantId(), o.getRoleId()).getData();
                    if (per != null && !per.getDisabled()) {
                        personList.add(per);
                    }
                }
            }
            for (int i = 0; i < personList.size(); i++) {
                if (personList.get(i).getName().contains(name)) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", personList.get(i).getId());
                    map.put("customId", personList.get(i).getCustomId());
                    map.put("name", personList.get(i).getName());
                    map.put("orgType", personList.get(i).getOrgType());
                    map.put("pId", personList.get(i).getParentId());
                    map.put("DN", personList.get(i).getDn());
                    if (OrgTypeEnum.PERSON.equals(personList.get(i).getOrgType())) {
                        Person per = personManager.getPerson(Y9LoginUserHolder.getTenantId(), personList.get(i).getId())
                            .getData();
                        map.put("sex", per.getSex());
                        map.put("duty", per.getDuty());
                        map.put("person", "3:" + per.getId());
                        if (per.getDuty() != null && !"".equals(per.getDuty())) {
                            map.put("name", per.getName() + "(" + per.getDuty() + ")");
                        } else {
                            map.put("name", per.getName());
                        }
                    }
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    item.add(map);
                }
            }
        } else if (ItemPrincipalTypeEnum.CUSTOMGROUP.getValue().equals(principalType)) {
            try {
                List<CustomGroup> grouplist = customGroupApi
                    .listCustomGroupByPersonId(tenantId, Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
                for (CustomGroup customGroup : grouplist) {
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", customGroup.getId());
                    map.put("pId", customGroup.getId());
                    map.put("name", customGroup.getGroupName());
                    map.put("isParent", true);
                    map.put("orgType", "customGroup");
                    map.put("principalType", ItemPermissionEnum.CUSTOMGROUP.getValue());
                    if (item.contains(map)) {
                        continue;// 去重
                    }
                    boolean b = false;
                    List<CustomGroupMember> customGroupMemberList = customGroupApi
                        .listCustomGroupMemberByGroupIdAndMemberType(tenantId,
                            Y9LoginUserHolder.getUserInfo().getPersonId(), customGroup.getId(), OrgTypeEnum.PERSON)
                        .getData();
                    if (null != customGroupMemberList && !customGroupMemberList.isEmpty()) {
                        for (CustomGroupMember customGroupMember : customGroupMemberList) {
                            Person person =
                                personManager.getPerson(tenantId, customGroupMember.getMemberId()).getData();
                            if (person != null && person.getName().contains(name)) {
                                Map<String, Object> map0 = new HashMap<>(16);
                                map0.put("id", customGroupMember.getMemberId());
                                map0.put("pId", customGroup.getId());
                                map0.put("isParent", false);
                                map0.put("orgType", person.getOrgType());
                                map0.put("sex", person.getSex());
                                map0.put("duty", person.getDuty());
                                map0.put("person", "3:" + person.getId());
                                map0.put("principalType", ItemPermissionEnum.USER.getValue());
                                if (person.getDuty() != null && !"".equals(person.getDuty())) {
                                    map0.put("name", person.getName() + "(" + person.getDuty() + ")");
                                } else {
                                    map0.put("name", person.getName());
                                }
                                if (item.contains(map0)) {
                                    continue;// 去重
                                }
                                item.add(map0);
                                b = true;
                            }
                        }
                        if (b) {
                            item.add(map);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    @Override
    public List<Map<String, Object>> findPermUserSendReceive(String id) {
        List<Map<String, Object>> item = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (StringUtils.isBlank(id)) {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findAll();
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department =
                        departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", receiveDepartment.getDeptId());
                    map.put("pId", receiveDepartment.getParentId());
                    map.put("name", department.getName());
                    map.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    map.put("isParent", count > 0);
                    map.put("orgType", "Department");
                    map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                    item.add(map);
                }
            } else {
                List<ReceiveDepartment> list = receiveDepartmentRepository.findByParentIdOrderByTabIndex(id);
                for (ReceiveDepartment receiveDepartment : list) {
                    Department department =
                        departmentManager.getDepartment(tenantId, receiveDepartment.getDeptId()).getData();
                    if (department == null || department.getId() == null) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>(16);
                    map.put("id", receiveDepartment.getDeptId());
                    map.put("pId", receiveDepartment.getParentId());
                    map.put("name", department.getName());
                    map.put("isPerm", true);
                    Integer count = receiveDepartmentRepository.countByParentId(receiveDepartment.getDeptId());
                    map.put("isParent", count > 0);
                    map.put("orgType", "Department");
                    map.put("principalType", ItemPermissionEnum.DEPARTMENT.getValue());
                    item.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public OrgUnit getParent(String tenantId, String parentId) {
        Organization parent = organizationManager.getOrganization(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentManager.getDepartment(tenantId, parentId).getData();
    }

    @Override
    public OrgUnit getParent(String tenantId, String nodeId, String parentId) {
        Organization parent = organizationManager.getOrganization(tenantId, parentId).getData();
        return parent.getId() != null ? parent : departmentManager.getDepartment(tenantId, parentId).getData();
    }

    @Override
    public void recursionUpToOrg(String tenantId, String nodeId, String parentId, List<OrgUnit> orgUnitList,
        boolean isParent) {
        OrgUnit parent = getParent(tenantId, nodeId, parentId);
        if (isParent) {
            parent.setDescription("parent");
        }
        if (orgUnitList.isEmpty()) {
            orgUnitList.add(parent);
        } else {
            String add = UtilConsts.TRUE;
            for (int i = 0; i < orgUnitList.size(); i++) {
                if (orgUnitList.get(i).getId().equals(parent.getId())) {
                    add = "false";
                    break;
                }
            }
            if (UtilConsts.TRUE.equals(add)) {
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

    private void recursionUpToOrg1(String tenantId, String nodeId, String parentId, List<OrgUnit> orgUnitList,
        boolean isParent) {
        OrgUnit parent = getParent(tenantId, parentId);
        if (isParent) {
            parent.setDescription("parent");
        }
        if (orgUnitList.isEmpty()) {
            orgUnitList.add(parent);
        } else {
            String add = UtilConsts.TRUE;
            for (int i = 0; i < orgUnitList.size(); i++) {
                if (orgUnitList.get(i).getId().equals(parent.getId())) {
                    add = "false";
                    break;
                }
            }
            if (UtilConsts.TRUE.equals(add)) {
                orgUnitList.add(parent);
            }
        }
        if (OrgTypeEnum.DEPARTMENT.equals(parent.getOrgType())) {
            if (parent.getId().equals(nodeId)) {
                return;
            }
            recursionUpToOrg1(tenantId, nodeId, parent.getParentId(), orgUnitList, true);
        }
    }
}
