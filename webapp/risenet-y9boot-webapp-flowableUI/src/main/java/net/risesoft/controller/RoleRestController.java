package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.enums.platform.TreeTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/vue/rolePerson")
public class RoleRestController {

    @Autowired
    private ItemRoleApi itemRoleManager;

    @Autowired
    private DepartmentApi departmentApi;

    private List<String> addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        return userIds;
    }

    /**
     * 获取组织机构树
     *
     * @param id 父节点id
     * @param treeType 架构树类型
     * @param name 搜索词
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findAll(@RequestParam(required = false) String id,
        @RequestParam(required = true) TreeTypeEnum treeType, @RequestParam(required = false) String name) {
        List<Map<String, Object>> item = itemRoleManager.getOrgTree(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), id, treeType, name);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取抄送选人
     *
     * @param id 父节点id
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findCsUser", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findCsUser(@RequestParam(required = false) String id,
        @RequestParam(required = true) Integer principalType,
        @RequestParam(required = false) String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        item = itemRoleManager.findCsUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), id,
            principalType, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 抄送选人搜索
     *
     * @param name 搜索词
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findCsUserSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findCsUserSearch(@RequestParam(required = false) String name,
        @RequestParam(required = true) Integer principalType,
        @RequestParam(required = false) String processInstanceId) {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        item = itemRoleManager.findCsUserSearch(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), name,
            principalType, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取发送选人
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @param id 父节点id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findAllPermUser", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findPermUser(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = true) Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String id) {
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        item = itemRoleManager.findPermUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), itemId,
            processDefinitionId, taskDefKey, principalType, id, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 发送选人搜索
     *
     * @param name 搜索词
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findPermUserByName", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findPermUserByName(@RequestParam(required = false) String name,
        @RequestParam(required = true) Integer principalType, @RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = false) String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        item = itemRoleManager.findPermUserByName(tenantId, Y9LoginUserHolder.getPersonId(), name, principalType,
            itemId, processDefinitionId, taskDefKey, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    public List<Person> getAllPersonsByDeptId(String deptId) {
        List<Person> personList = new ArrayList<>();
        recursionAllPersons(deptId, personList);
        return personList;
    }

    /**
     * 获取发送人数
     *
     * @param userChoice 人员id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Integer> getUserCount(@RequestParam(required = true) String userChoice) {
        List<String> userIds = new ArrayList<String>();
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
        return Y9Result.success(userIds.size(), "获取成功");
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
