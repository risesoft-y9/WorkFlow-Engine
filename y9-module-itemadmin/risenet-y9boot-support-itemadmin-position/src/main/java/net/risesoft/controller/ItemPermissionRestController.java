package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.entity.ItemPermission;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemPermissionService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
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
@RequestMapping("/vue/itemPerm")
public class ItemPermissionRestController {

    private final ItemPermissionService itemPermissionService;

    private final ProcessDefinitionApi processDefinitionManager;

    private final Y9Properties y9Conf;

    /**
     * 复制权限
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/copyPerm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyPerm(@RequestParam String itemId,
                                     @RequestParam String processDefinitionId) {
        itemPermissionService.copyPerm(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 删除权限绑定
     *
     * @param id 权限id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam String id) {
        itemPermissionService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId 流程定义key
     * @param itemId              事项id
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam String itemId,
                                                    @RequestParam String processDefinitionId) {
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        String freeFlowKey = y9Conf.getApp().getItemAdmin().getFreeFlowKey();
        /*
         * 自由流程额外添加一个办结角色，规定自由流的办结按钮控制
         */
        if (processDefinitionId.startsWith(freeFlowKey)) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("taskDefKey", SysVariables.FREEFLOWENDROLE);
            map.put("taskDefName", "办结");
            list.add(map);
        }
        List<ItemPermission> ipList;
        for (Map<String, Object> map : list) {
            String roleNames = "";
            ipList = itemPermissionService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                    (String) map.get("taskDefKey"));
            for (ItemPermission ip : ipList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = ip.getRoleName();
                } else {
                    roleNames += "、" + ip.getRoleName();
                }
            }
            map.put("roleNames", roleNames);
        }
        resMap.put("rows", list);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取权限绑定列表
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey          任务key
     * @return
     */
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getPerm(@RequestParam String itemId,
                                                       @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ItemPermission> itemPermissionList = itemPermissionService
                .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        for (ItemPermission o : itemPermissionList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", o.getId());
            map.put("roleId", o.getRoleId());
            map.put("roleName", o.getRoleName());
            map.put("roleType", o.getRoleType());
            list.add(map);
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 清空事项绑定的流程对应的流程定义的权限
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/removePerm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removePerm(@RequestParam String itemId,
                                       @RequestParam String processDefinitionId) {
        itemPermissionService.removePerm(itemId, processDefinitionId);
        return Y9Result.successMsg("清空成功");
    }

    /**
     * 保存权限
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey          任务key
     * @param roleId              角色id
     * @param roleType            角色类型
     * @return
     */
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(@RequestParam String itemId,
                                 @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey,
                                 @RequestParam String roleId, @RequestParam Integer roleType) {
        if (roleId.contains(PunctuationConsts.SEMICOLON)) {
            String[] roleIds = roleId.split(";");
            for (String roleIdTemp : roleIds) {
                itemPermissionService.save(itemId, processDefinitionId, taskDefKey, roleIdTemp, roleType);
            }
        } else {
            itemPermissionService.save(itemId, processDefinitionId, taskDefKey, roleId, roleType);
        }
        return Y9Result.successMsg("保存成功");
    }
}