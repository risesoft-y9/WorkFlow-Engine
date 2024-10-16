package net.risesoft.controller.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemStartNodeRole;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemStartNodeRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemStartNodeRole", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemStartNodeRoleController {

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    private final ProcessDefinitionApi processDefinitionApi;

    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemStartNodeRoleService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @return Y9Result<List<ItemStartNodeRole>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<ItemStartNodeRole>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemStartNodeRole> oldList =
            itemStartNodeRoleService.listByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        if (oldList.isEmpty()) {
            String startNode =
                processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
            List<TargetModel> nodeList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode).getData();
            for (TargetModel targetModel : nodeList) {
                itemStartNodeRoleService.initRole(itemId, processDefinitionId, targetModel.getTaskDefKey(),
                    targetModel.getTaskDefName());
            }
            oldList = itemStartNodeRoleService.listByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        }
        List<Role> roleList;
        for (ItemStartNodeRole isnr : oldList) {
            String roleNames = "";
            roleList = itemStartNodeRoleService.listRoleByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, isnr.getTaskDefKey());
            for (Role role : roleList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getName();
                } else {
                    roleNames += "、" + role.getName();
                }
            }
            isnr.setRoleNames(roleNames);
        }
        return Y9Result.success(oldList, "获取成功");
    }

    @GetMapping(value = "/getNodeList")
    public Y9Result<List<ItemStartNodeRole>> getNodeList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemStartNodeRole> oldList =
            itemStartNodeRoleService.listByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        if (oldList.isEmpty()) {
            String startNode =
                processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId).getData();
            List<TargetModel> nodeList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode).getData();
            for (TargetModel targetModel : nodeList) {
                itemStartNodeRoleService.initRole(itemId, processDefinitionId, targetModel.getTaskDefKey(),
                    targetModel.getTaskDefName());
            }
            oldList = itemStartNodeRoleService.listByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        }
        return Y9Result.success(oldList, "获取成功");
    }

    /**
     * 获取按钮绑定角色列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey 任务节点key
     * @return Y9Result<List<Role>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<Role>> list(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey) {
        List<Role> roleList = itemStartNodeRoleService.listRoleByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        return Y9Result.success(roleList, "获取成功");
    }

    /**
     * 移除按钮与角色的绑定
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey 任务节点key
     * @param roleIds 角色id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam String roleIds) {
        itemStartNodeRoleService.removeRole(itemId, processDefinitionId, taskDefKey, roleIds);
        return Y9Result.successMsg("删除成功");
    }

    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        itemStartNodeRoleService.saveOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存按钮角色
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey 任务节点key
     * @param roleIds 角色id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveRole")
    public Y9Result<String> saveRole(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam String roleIds) {
        itemStartNodeRoleService.saveRole(itemId, processDefinitionId, taskDefKey, roleIds);
        return Y9Result.successMsg("保存成功");
    }
}