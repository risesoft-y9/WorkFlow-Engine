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

import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.service.RoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发送选人接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/itemRole4Position")
public class ItemRoleApiImpl implements ItemRole4PositionApi {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private OrganizationApi organizationManager;

    /**
     * 获取抄送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 唯一标识
     * @param principalType 类型
     * @param processInstanceId 流程实例id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/findCsUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUser(String tenantId, String userId, String positionId, String id, Integer principalType, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findCsUser(id, principalType, processInstanceId);
        return listMap;
    }

    /**
     * 获取委办局
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/findCsUserBureau", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUserBureau(String tenantId, String userId, String positionId, Integer principalType) {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        if (principalType == ItemPrincipalTypeEnum.DEPT.getValue()) {
            OrgUnit orgunit = orgUnitManager.getBureau(tenantId, position.getParentId()).getData();
            Map<String, Object> map = new HashMap<String, Object>(16);
            String orgunitId = orgunit.getId();
            map.put("id", orgunitId);
            map.put("name", orgunit.getName());
            map.put("isPerm", true);
            map.put("orgType", orgunit.getOrgType());
            map.put("isParent", OrgTypeEnum.DEPARTMENT.equals(orgunit.getOrgType()) ? true : false);
            item.add(map);
        }
        return item;
    }

    /**
     * 抄送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param processInstanceId 流程实例Id
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/findCsUserSearch", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findCsUserSearch(String tenantId, String userId, String positionId, String name, Integer principalType, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findCsUserSearch(name, principalType, processInstanceId);
        return listMap;
    }

    /**
     * 获取发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 流程定义中节点Id
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param id 唯一标识
     * @param processInstanceId 流程实例Id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/findPermUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUser(String tenantId, String userId, String positionId, String itemId, String processDefinitionId, String taskDefKey, Integer principalType, String id, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findPermUser(itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
        return listMap;
    }

    /**
     * 发送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param name 人员名称
     * @param principalType 类型:2(部门)、3 (人员)、5(用户组)、6 (岗位)
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 流程定义中节点Id
     * @param processInstanceId 流程实例Id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/findPermUserByName", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUserByName(String tenantId, String userId, String positionId, String name, Integer principalType, String itemId, String processDefinitionId, String taskDefKey, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findPermUserByName(name, itemId, processDefinitionId, taskDefKey, principalType, processInstanceId);
        return listMap;
    }

    /**
     * 获取发送人（收发单位）
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 父节点id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/findPermUserSendReceive", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findPermUserSendReceive(String tenantId, String positionId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        listMap = roleService.findPermUserSendReceive(id);
        return listMap;
    }

    /**
     * 获取组织机构树
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 组织架构id
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @param name 人员名称
     *
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping(value = "/getOrgTree", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getOrgTree(String tenantId, String positionId, String id, OrgTreeTypeEnum treeType, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            List<Organization> org = organizationManager.list(tenantId).getData();
            if (org != null && org.size() > 0) {
                id = org.get(0).getId();
            }
        }
        List<OrgUnit> orgUnitList = new ArrayList<OrgUnit>();
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
            } else if (OrgTypeEnum.POSITION.equals(orgUnitList.get(i).getOrgType())) {
                Position person = positionManager.get(tenantId, orgUnitList.get(i).getId()).getData();
                map.put("person", "6:" + person.getId());
                map.put("name", person.getName());
                map.put("duty", person.getJobName());
            }
            item.add(map);
        }
        return item;
    }
}
