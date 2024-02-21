package net.risesoft.controller.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/org")
public class MobileOrgController {

    protected final Logger log = LoggerFactory.getLogger(MobileOrgController.class);

    @Autowired
    private PersonApi personApi;

    @Autowired
    private OrganizationApi organizationApi;

    @Autowired
    private OrgUnitApi orgUnitApi;

    @Autowired
    private DepartmentApi departmentApi;

    private List<String> addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        return userIds;
    }

    public List<Person> getAllPersonsByDeptId(String deptId) {
        List<Person> personList = new ArrayList<>();
        recursionAllPersons(deptId, personList);
        return personList;
    }

    /**
     * 获取组织架构
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 父节点id
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/getOrg")
    public void getOrg(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        String id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
            if (StringUtils.isBlank(id)) {
                Organization org = orgUnitApi.getOrganization(tenantId, userId).getData();
                String orgId = org.getId();
                List<Department> deptList = organizationApi.listDepartments(tenantId, orgId).getData();
                List<Person> personList = organizationApi.listPersons(tenantId, orgId).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
                    m.put("id", dept.getId());
                    m.put("pId", dept.getParentId());
                    m.put("name", dept.getName());
                    m.put("isParent", true);
                    m.put("orgType", dept.getOrgType());
                    if (item.contains(m)) {
                        continue;
                    }
                    item.add(m);
                }
                for (Person person : personList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
                    m.put("id", person.getId());
                    m.put("pId", person.getParentId());
                    m.put("name", person.getName());
                    m.put("loginName", person.getLoginName());
                    m.put("email", person.getEmail());
                    m.put("mobile", person.getMobile());
                    m.put("sex", person.getSex());
                    m.put("duty", person.getDuty());
                    m.put("isParent", false);
                    m.put("orgType", person.getOrgType());
                    if (item.contains(m)) {
                        continue;// 去重
                    }
                    item.add(m);
                }
            } else {// 展开部门
                List<Department> deptList = organizationApi.listDepartments(tenantId, id).getData();
                List<Person> personList = organizationApi.listPersons(tenantId, id).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
                    m.put("id", dept.getId());
                    m.put("pId", id);
                    m.put("name", dept.getName());
                    m.put("isParent", true);
                    m.put("orgType", dept.getOrgType());
                    if (item.contains(m)) {
                        continue;// 去重
                    }
                    item.add(m);
                }
                for (Person person : personList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
                    m.put("id", person.getId());
                    m.put("pId", id);
                    m.put("name", person.getName());
                    m.put("loginName", person.getLoginName());
                    m.put("email", person.getEmail());
                    m.put("mobile", person.getMobile());
                    m.put("sex", person.getSex());
                    m.put("duty", person.getDuty());
                    m.put("isParent", false);
                    m.put("orgType", person.getOrgType());
                    if (item.contains(m)) {
                        continue;// 去重
                    }
                    item.add(m);
                }
            }
            map.put("rows", item);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取发送人数
     *
     * @param tenantId 租户id
     * @param userChoice 选择人员id
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/getUserCount")
    public void getUserCount(@RequestHeader("auth-tenantId") String tenantId, String userChoice,
        HttpServletRequest request, HttpServletResponse response) {
        List<String> userIds = new ArrayList<String>();
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                if (userIds.size() > 100) {
                    break;
                }
                String[] s2 = s.split(SysVariables.COLON);
                Integer principalType = Integer.parseInt(s2[0]);
                if (principalType == 3) {
                    userIds = this.addUserIds(userIds, s2[1]);
                } else if (principalType == 2) {
                    List<Person> personList = this.getAllPersonsByDeptId(s2[1]);
                    for (Person pTemp : personList) {
                        if (userIds.size() > 100) {
                            break;
                        }
                        if (!pTemp.getDisabled()) {
                            userIds = this.addUserIds(userIds, pTemp.getId());
                        }
                    }
                }
            }
        }
        map.put("count", userIds.size());
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取人员信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo")
    public void getUserInfo(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            OrgUnit orgUnit = personApi.getBureau(tenantId, userId).getData();
            OrgUnit orgUnit1 = personApi.getParent(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map.put("bureauName", orgUnit != null ? orgUnit.getName() : "");
            map.put("deptName", orgUnit1 != null ? orgUnit1.getName() : "");
            map.put("person", person);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    private void recursionAllPersons(String parentId, List<Person> personList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        personList.addAll(departmentApi.listPersonsByDisabled(tenantId, parentId, false).getData());
        boolean b = personList.size() < 101;
        if (b) {
            List<Department> deptList = departmentApi.listSubDepartments(tenantId, parentId).getData();
            for (Department dept : deptList) {
                recursionAllPersons(dept.getId(), personList);
            }
        }
    }
}