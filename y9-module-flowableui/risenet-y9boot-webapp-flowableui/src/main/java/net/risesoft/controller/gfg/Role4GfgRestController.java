package net.risesoft.controller.gfg;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.pojo.Y9Result;
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
@RequestMapping(value = "/vue/rolePerson/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class Role4GfgRestController {

    private final ItemRoleApi itemRoleApi;

    private final SignDeptInfoApi signDeptInfoApi;

    private final OrgUnitApi orgUnitApi;

    /**
     * 获取司内会签选人组织机构数据
     *
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/findCsUser")
    public Y9Result<List<ItemRoleOrgUnitModel>> findCsUser(@RequestParam(required = false) String id) {
        return itemRoleApi.findCsUser4Bureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), id);
    }

    /**
     * 获取发送选人组织机构数据
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param id 父节点id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/findAllPermUser")
    public Y9Result<List<ItemRoleOrgUnitModel>> findPermUser(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String id) {
        if (StringUtils.isBlank(id)) {
            id = "";
        }
        return itemRoleApi.findAllPermUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskDefKey, principalType, id,
            processInstanceId, taskId);
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
     * 获取发送选人组织机构数据
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
        List<ItemRoleOrgUnitModel> list = itemRoleApi.findByRoleId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), roleId, principalType, id).getData();
        List<SignDeptModel> signDeptList =
            signDeptInfoApi.getSignDeptList(Y9LoginUserHolder.getTenantId(), "0", processSerialNumber).getData();
        list = list.stream()
            .filter(item -> signDeptList.stream()
                .noneMatch(signDept -> signDept.getDeptId()
                    .equals(orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), item.getId()).getData().getId())))
            .collect(Collectors.toList());
        return Y9Result.success(list);
    }
}
