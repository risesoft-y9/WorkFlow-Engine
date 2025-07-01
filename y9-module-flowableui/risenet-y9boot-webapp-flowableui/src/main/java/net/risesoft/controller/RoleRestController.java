package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
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
@RequestMapping(value = "/vue/rolePerson", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleRestController {

    private final ItemRoleApi itemRoleApi;

    private final DepartmentApi departmentApi;

    private final PositionApi positionApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private void addUserIds(List<String> userIds, String userId) {
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
    }

    /**
     * 获取组织机构数据
     *
     * @param id 父节点id
     * @param treeType 架构树类型
     * @param name 搜索词
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping(value = "/getOrgTree")
    public Y9Result<List<ItemRoleOrgUnitModel>> findAll(@RequestParam(required = false) String id,
        @RequestParam OrgTreeTypeEnum treeType, @RequestParam(required = false) String name) {
        return itemRoleApi.getOrgTree(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), id, treeType,
            name);
    }

    /**
     * 获取抄送选人组织机构数据
     *
     * @param id 父节点id
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/findCsUser")
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam(required = false) String id,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId) {
        return itemRoleApi.findCsUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), id, principalType, processInstanceId);
    }

    /**
     * 抄送选人组织机构搜索
     *
     * @param name 搜索词
     * @param principalType 架构类型
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < ItemRoleOrgUnitModel>>
     */
    @GetMapping(value = "/findCsUserSearch")
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUserSearch(@RequestParam(required = false) String name,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId) {
        return itemRoleApi.findCsUserSearch(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), name, principalType, processInstanceId);
    }

    /**
     * 获取发送选人组织机构数据
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/findAllPermUser")
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        return itemRoleApi.findPermUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskDefKey, principalType, id,
            processInstanceId);
    }

    /**
     * 获取发送选人组织机构搜索
     *
     * @param name 搜索词
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<ItemRoleOrgUnitModel>>
     */
    @GetMapping(value = "/findPermUserByName")
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUserByName(@RequestParam(required = false) String name,
        @RequestParam Integer principalType, @RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = false) String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return itemRoleApi.findPermUserByName(tenantId, Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), name, principalType, itemId, processDefinitionId, taskDefKey,
            processInstanceId);
    }

    public List<Position> getAllPositionByDeptId(String deptId) {
        List<Position> list = new ArrayList<>();
        recursionAllPosition(deptId, list);
        return list;
    }

    /**
     * 获取发送选人组织机构数据
     *
     * @param roleId 角色id
     * @param principalType 选人类型
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/findByRoleId")
    public Y9Result<List<ItemRoleOrgUnitModel>> findByRoleId(@RequestParam @NotBlank String roleId,
        @RequestParam Integer principalType, @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        return itemRoleApi.findByRoleId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), roleId, principalType, id);
    }

    /**
     * 获取发送选择人数
     *
     * @param userChoice 人员id
     * @return Y9Result<Integer>
     */
    @GetMapping(value = "/getUserCount")
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

    /**
     * 加签-获取加签的人-排除正在会签和减签的部门的秘书
     *
     * @param roleId 角色id
     * @param principalType 选人类型
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/addSignDept")
    public Y9Result<List<ItemRoleOrgUnitModel>> addSignDept(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String roleId, @RequestParam Integer principalType,
        @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = Y9LoginUserHolder.getPersonId(),
            positionId = Y9LoginUserHolder.getPositionId();
        // 正在会签和减签的部门
        List<SignDeptDetailModel> sddList =
            signDeptDetailApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData()
                .stream()
                .filter(ssd -> ssd.getStatus().equals(SignDeptDetailStatusEnum.DOING.getValue())
                    || ssd.getStatus().equals(SignDeptDetailStatusEnum.DELETED.getValue()))
                .collect(Collectors.toList());
        List<ItemRoleOrgUnitModel> list = itemRoleApi
            .findByRoleId(tenantId, personId, positionId, roleId, principalType, id).getData()
            .stream()
            .filter(itemRoleOrgUnitModel -> sddList.stream()
                .noneMatch(ssd -> itemRoleOrgUnitModel.getGuidPath().contains(ssd.getDeptId())))
            .collect(Collectors.toList());
        return Y9Result.success(list);
    }
}
