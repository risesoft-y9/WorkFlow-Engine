package net.risesoft.controller;

import java.util.List;

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
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
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
        return itemRoleApi.findAllPermUser(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskDefKey, principalType, id,
            processInstanceId);
    }

}
