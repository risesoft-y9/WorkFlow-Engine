package net.risesoft.controller.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 人员接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mobile/org")
public class MobileOrgController {

    protected final Logger log = LoggerFactory.getLogger(MobileOrgController.class);

    private final PersonApi personApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final DepartmentApi departmentApi;

    private final TodoTaskApi todoTaskApi;

    private final Entrust4PositionApi entrust4PositionApi;

    private void addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
    }

    public List<Position> getAllPersonsByDeptId(String deptId) {
        List<Position> personList = new ArrayList<>();
        recursionAllPersons(deptId, personList);
        return personList;
    }

    /**
     * 获取组织架构
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 父节点id
     */
    @ResponseBody
    @RequestMapping(value = "/getOrg")
    public void getOrg(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam(required = false) String id, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Map<String, Object>> item = new ArrayList<>();
            if (StringUtils.isBlank(id)) {// 只获取当前组织架构
                Organization org = orgUnitApi.getOrganization(tenantId, userId).getData();
                String orgId = org.getId();
                List<Department> deptList = departmentApi.listByParentId(tenantId, orgId).getData();
                List<Person> personList = personApi.listByParentId(tenantId, orgId).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<>(16);
                    m.put("id", dept.getId());
                    m.put("pId", dept.getParentId());
                    m.put("name", dept.getName());
                    m.put("isParent", true);
                    m.put("orgType", dept.getOrgType());
                    if (item.contains(m)) {
                        continue;// 去重
                    }
                    item.add(m);
                }
                for (Person person : personList) {
                    Map<String, Object> m = new HashMap<>(16);
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
                        continue;
                    }
                    item.add(m);
                }
            } else {// 展开部门
                List<Department> deptList = departmentApi.listByParentId(tenantId, id).getData();
                List<Person> personList = personApi.listByParentId(tenantId, id).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<>(16);
                    m.put("id", dept.getId());
                    m.put("pId", id);
                    m.put("name", dept.getName());
                    m.put("isParent", true);
                    m.put("orgType", dept.getOrgType());
                    if (item.contains(m)) {
                        continue;
                    }
                    item.add(m);
                }
                for (Person person : personList) {
                    Map<String, Object> m = new HashMap<>(16);
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
                        continue;
                    }
                    item.add(m);
                }
            }
            map.put("rows", item);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("获取组织架构发生异常", e);
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取人员岗位列表信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     */
    @ResponseBody
    @RequestMapping(value = "/getPositionList")
    public void getPositionList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            List<Map<String, Object>> resList = new ArrayList<>();
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Position> list = positionApi.listByPersonId(tenantId, userId).getData();
            for (Position p : list) {
                Map<String, Object> map0 = new HashMap<>(16);
                map0.put("id", p.getId());
                map0.put("name", p.getName());
                long todoCount;
                todoCount = todoTaskApi.countByReceiverId(tenantId, p.getId());
                map0.put("todoCount", todoCount);
                resList.add(map0);

                // 获取当前岗被委托记录
                try {
                    List<EntrustModel> list1 = entrust4PositionApi.getMyEntrustList(tenantId, p.getId()).getData();
                    for (EntrustModel model : list1) {
                        if (model.getUsed().equals(1)) {// 使用中的委托，将委托岗位加入岗位列表
                            Map<String, Object> map1 = new HashMap<>(16);
                            String positionId = model.getOwnerId();
                            Position position = positionApi.get(tenantId, positionId).getData();
                            if (position != null) {
                                map1.put("id", position.getId());
                                map1.put("name", position.getName());
                                long todoCount1;
                                todoCount1 = todoTaskApi.countByReceiverId(tenantId, position.getId());
                                map1.put("todoCount", todoCount1);
                                resList.add(map1);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("获取人员岗位列表信息发生异常", e);
                }
            }
            map.put("positionList", resList);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("获取人员岗位列表信息发生异常", e);
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取发送人数
     *
     * @param tenantId 租户id
     * @param userChoice 选择人员id
     */
    @ResponseBody
    @RequestMapping(value = "/getUserCount")
    public void getUserCount(@RequestHeader("auth-tenantId") String tenantId, @RequestParam String userChoice,
        HttpServletResponse response) {
        List<String> userIds = new ArrayList<>();
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                if (userIds.size() > 100) {
                    break;
                }
                String[] s2 = s.split(SysVariables.COLON);
                int principalType = Integer.parseInt(s2[0]);
                if (principalType == 3) {
                    this.addUserIds(userIds, s2[1]);
                } else if (principalType == 2) {// 选取的是部门，获取部门下的所有人员
                    List<Position> personList = this.getAllPersonsByDeptId(s2[1]);
                    for (Position pTemp : personList) {
                        if (userIds.size() > 100) {
                            break;
                        }
                        this.addUserIds(userIds, pTemp.getId());
                    }
                }
            }
        }
        map.put("count", userIds.size());
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取人员信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     */
    @ResponseBody
    @RequestMapping(value = "/getUserInfo")
    public void getUserInfo(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Position position = positionApi.get(tenantId, positionId).getData();
            Person person = personApi.get(tenantId, userId).getData();
            OrgUnit orgUnit = orgUnitApi.getBureau(tenantId, positionId).getData();
            OrgUnit orgUnit1 = orgUnitApi.getParent(tenantId, positionId).getData();
            Y9LoginUserHolder.setPerson(person);
            map.put("bureauName", orgUnit != null ? orgUnit.getName() : "");
            map.put("deptName", orgUnit1 != null ? orgUnit1.getName() : "");
            map.put("person", person);
            map.put("position", position);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("获取人员信息发生异常", e);
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    private void recursionAllPersons(String parentID, List<Position> personList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        personList.addAll(positionApi.listByParentId(tenantId, parentID).getData());
        if (personList.size() < 101) {
            List<Department> deptList = departmentApi.listByParentId(tenantId, parentID).getData();
            for (Department dept : deptList) {
                recursionAllPersons(dept.getId(), personList);
            }
        }
    }
}