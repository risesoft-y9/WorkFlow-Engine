package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发送选人
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/vue/rolePerson")
public class RoleRestController {

    private final ItemRole4PositionApi itemRole4PositionApi;

    private final DepartmentApi departmentApi;

    private final PositionApi positionApi;

    private void addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
    }

    /**
     * 获取组织机构树
     *
     * @param id 父节点id
     * @param treeType 架构树类型
     * @param name 搜索词
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getOrgTree", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findAll(@RequestParam(required = false) String id,
        @RequestParam OrgTreeTypeEnum treeType, @RequestParam(required = false) String name) {
        List<Map<String, Object>> item;
        item = itemRole4PositionApi.getOrgTree(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), id,
            treeType, name);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 获取抄送选人
     *
     * @param id 父节点id
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/findCsUser", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findCsUser(@RequestParam(required = false) String id,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId) {
        List<Map<String, Object>> item;
        item = itemRole4PositionApi.findCsUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), id, principalType, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    /**
     * 抄送选人搜索
     *
     * @param name 搜索词
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/findCsUserSearch", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findCsUserSearch(@RequestParam(required = false) String name,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId) {
        List<Map<String, Object>> item;
        item = itemRole4PositionApi.findCsUserSearch(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), name, principalType, processInstanceId);
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
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/findAllPermUser", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findPermUser(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String id) {
        List<Map<String, Object>> item;
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        item = itemRole4PositionApi.findPermUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskDefKey, principalType, id,
            processInstanceId);
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
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/findPermUserByName", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> findPermUserByName(@RequestParam(required = false) String name,
        @RequestParam Integer principalType, @RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = false) String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> item = itemRole4PositionApi.findPermUserByName(tenantId,
            Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), name, principalType, itemId,
            processDefinitionId, taskDefKey, processInstanceId);
        return Y9Result.success(item, "获取成功");
    }

    public List<Position> getAllPositionByDeptId(String deptId) {
        List<Position> list = new ArrayList<>();
        recursionAllPosition(deptId, list);
        return list;
    }

    /**
     * 获取发送人数
     *
     * @param userChoice 人员id
     * @return Y9Result<Integer>
     */
    @RequestMapping(value = "/getUserCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Integer> getUserCount(@RequestParam @NotBlank String userChoice) {
        List<String> userIds = new ArrayList<>();
        if (StringUtils.isNotBlank(userChoice)) {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String s : userChoices) {
                if (userIds.size() > 100) {
                    break;
                }
                String[] s2 = s.split(SysVariables.COLON);
                int principalType = Integer.parseInt(s2[0]);
                if (principalType == 6) {
                    this.addUserIds(userIds, s2[1]);
                } else if (principalType == 2) {// 选取的是部门，获取部门下的所有人员
                    List<Position> list = this.getAllPositionByDeptId(s2[1]);
                    for (Position pTemp : list) {
                        if (userIds.size() > 100) {
                            break;
                        }
                        this.addUserIds(userIds, pTemp.getId());
                    }
                }
            }
        }
        return Y9Result.success(userIds.size(), "获取成功");
    }

    private void recursionAllPosition(String parentID, List<Position> list) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        list.addAll(positionApi.listByParentId(tenantId, parentID).getData());
        if (list.size() < 101) {
            List<Department> deptList = departmentApi.listByParentId(tenantId, parentID).getData();
            for (Department dept : deptList) {
                recursionAllPosition(dept.getId(), list);
            }
        }
    }
}
