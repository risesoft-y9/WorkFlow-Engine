package net.risesoft.controller.mobile.v1;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织人员接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/org")
public class MobileV1OrgController {

    private final PersonApi personApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final DepartmentApi departmentApi;

    private final TodoTaskApi todoTaskApi;

    private final Entrust4PositionApi entrust4PositionApi;

    private List<String> addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        return userIds;
    }

    public List<Position> getAllPersonsByDeptId(String deptId) {
        List<Position> personList = new ArrayList<>();
        recursionAllPersons(deptId, personList);
        return personList;
    }

    /**
     * 获取组织架构
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param id         父节点id
     */
    @RequestMapping(value = "/getOrg")
    public Y9Result<List<Map<String, Object>>> getOrg(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String id) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
            if (StringUtils.isBlank(id)) {// 只获取当前组织架构
                Organization org = orgUnitApi.getOrganization(tenantId, userId).getData();
                String orgId = org.getId();
                List<Department> deptList = departmentApi.listByParentId(tenantId, orgId).getData();
                List<Person> personList = personApi.listByParentId(tenantId, orgId).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
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
                        continue;
                    }
                    item.add(m);
                }
            } else {// 展开部门
                List<Department> deptList = departmentApi.listByParentId(tenantId, id).getData();
                List<Person> personList = personApi.listByParentId(tenantId, id).getData();
                for (Department dept : deptList) {
                    Map<String, Object> m = new HashMap<String, Object>(16);
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
                        continue;
                    }
                    item.add(m);
                }
            }
            return Y9Result.success(item, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

    /**
     * 获取人员岗位列表信息
     *
     * @param tenantId 租户id
     * @param userId   人员id
     */
    @RequestMapping(value = "/getPositionList")
    public Y9Result<List<Map<String, Object>>> getPositionList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId) {
        try {
            List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
            Y9LoginUserHolder.setTenantId(tenantId);
            List<Position> list = positionApi.listByPersonId(tenantId, userId).getData();
            for (Position p : list) {
                Map<String, Object> map0 = new HashMap<String, Object>(16);
                map0.put("id", p.getId());
                map0.put("name", p.getName());
                long todoCount = 0;
                todoCount = todoTaskApi.countByReceiverId(tenantId, p.getId());
                map0.put("todoCount", todoCount);
                resList.add(map0);

                // 获取当前岗被委托记录
                try {
                    List<EntrustModel> list1 = entrust4PositionApi.getMyEntrustList(tenantId, p.getId());
                    for (EntrustModel model : list1) {
                        if (model.getUsed().equals(1)) {// 使用中的委托，将委托岗位加入岗位列表
                            Map<String, Object> map1 = new HashMap<String, Object>(16);
                            String positionId = model.getOwnerId();
                            Position position = positionApi.get(tenantId, positionId).getData();
                            if (position != null) {
                                map1.put("id", position.getId());
                                map1.put("name", position.getName());
                                long todoCount1 = 0;
                                todoCount1 = todoTaskApi.countByReceiverId(tenantId, position.getId());
                                map1.put("todoCount", todoCount1);
                                resList.add(map1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Y9Result.success(resList, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

    /**
     * 获取发送人数
     *
     * @param tenantId   租户id
     * @param userChoice 选择人员id
     */
    @RequestMapping(value = "/getUserCount")
    public Y9Result<Integer> getUserCount(@RequestHeader("auth-tenantId") String tenantId, String userChoice) {
        List<String> userIds = new ArrayList<String>();
        Y9LoginUserHolder.setTenantId(tenantId);
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
                } else if (principalType == 2) {// 选取的是部门，获取部门下的所有人员
                    List<Position> personList = this.getAllPersonsByDeptId(s2[1]);
                    for (Position pTemp : personList) {
                        if (userIds.size() > 100) {
                            break;
                        }
                        userIds = this.addUserIds(userIds, pTemp.getId());
                    }
                }
            }
        }
        return Y9Result.success(userIds.size(), "获取成功");
    }

    /**
     * 获取人员信息
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     */
    @RequestMapping(value = "/getUserInfo")
    public Y9Result<Map<String, Object>> getUserInfo(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
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
            return Y9Result.success(map, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
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