package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemStartNodeRole;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemStartNodeRoleService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemStartNodeRole")
public class ItemStartNodeRoleController {

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    private final ProcessDefinitionApi processDefinitionApi;

    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        itemStartNodeRoleService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemStartNodeRole> oldList =
            itemStartNodeRoleService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        if (oldList.isEmpty()) {
            String startNode = processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId);
            List<Map<String, String>> nodeList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode);
            for (Map<String, String> map : nodeList) {
                itemStartNodeRoleService.initRole(itemId, processDefinitionId, map.get("taskDefKey"),
                    map.get("taskDefName"));
            }
            oldList = itemStartNodeRoleService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        List<Role> roleList;
        List<String> roleIdList = new ArrayList<>();
        Map<String, Object> mapTemp;
        for (ItemStartNodeRole isnr : oldList) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("id", isnr.getId());
            mapTemp.put("processDefinitionId", isnr.getProcessDefinitionId());
            mapTemp.put("itemId", isnr.getItemId());
            mapTemp.put("taskDefKey", isnr.getTaskDefKey());
            mapTemp.put("taskDefName", isnr.getTaskDefName());
            mapTemp.put("tabIndex", isnr.getTabIndex());

            String roleNames = "";
            roleList = itemStartNodeRoleService.getRoleList(itemId, processDefinitionId, isnr.getTaskDefKey());
            for (Role role : roleList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getName();
                } else {
                    roleNames += "、" + role.getName();
                }
                roleIdList.add(role.getId());
            }
            mapTemp.put("roleNames", roleNames);
            mapTemp.put("roleIds", roleIdList);
            rows.add(mapTemp);
        }
        resMap.put("rows", rows);
        return Y9Result.success(resMap, "获取成功");
    }

    @RequestMapping(value = "/getNodeList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemStartNodeRole>> getNodeList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemStartNodeRole> oldList =
            itemStartNodeRoleService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        if (oldList.isEmpty()) {
            String startNode = processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId);
            List<Map<String, String>> nodeList =
                processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNode);
            for (Map<String, String> map : nodeList) {
                itemStartNodeRoleService.initRole(itemId, processDefinitionId, map.get("taskDefKey"),
                    map.get("taskDefName"));
            }
            oldList = itemStartNodeRoleService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        }
        return Y9Result.success(oldList, "获取成功");
    }

    /**
     * 获取按钮绑定角色列表
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey    任务节点key
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Role>> list(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        List<Role> roleList = itemStartNodeRoleService.getRoleList(itemId, processDefinitionId, taskDefKey);
        return Y9Result.success(roleList, "获取成功");
    }

    /**
     * 移除按钮与角色的绑定
     * @param itemId 事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey 任务节点key
     * @param roleIds 角色id
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey,
        @RequestParam String roleIds) {
        itemStartNodeRoleService.removeRole(itemId, processDefinitionId, taskDefKey, roleIds);
        return Y9Result.successMsg("删除成功");
    }

    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        itemStartNodeRoleService.saveOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存按钮角色
     * @param itemId  事项id
     * @param processDefinitionId 流程定义ID
     * @param taskDefKey 任务节点key
     * @param roleIds    角色id
     * @return
     */
    @RequestMapping(value = "/saveRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveRole(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey,
        @RequestParam String roleIds) {
        itemStartNodeRoleService.saveRole(itemId, processDefinitionId, taskDefKey, roleIds);
        return Y9Result.successMsg("保存成功");
    }
}