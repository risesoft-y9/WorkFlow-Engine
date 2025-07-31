package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.consts.PunctuationConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ItemPermission;
import net.risesoft.enums.ItemPermissionEnum;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemPermissionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemPerm", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemPermissionRestController {

    private final ItemPermissionService itemPermissionService;

    private final ProcessDefinitionApi processDefinitionApi;

    private final Y9ItemAdminProperties y9ItemAdminProperties;

    /**
     * 复制权限
     *
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/copyPerm")
    public Y9Result<String> copyPerm(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemPermissionService.copyPerm(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 删除权限绑定
     *
     * @param id 权限id
     * @return
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam String id) {
        itemPermissionService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId 流程定义key
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TargetModel> list = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        String freeFlowKey = y9ItemAdminProperties.getFreeFlowKey();
        /*
         * 自由流程额外添加一个办结角色，规定自由流的办结按钮控制
         */
        if (processDefinitionId.startsWith(freeFlowKey)) {
            TargetModel targetModel = new TargetModel();
            targetModel.setTaskDefKey(SysVariables.FREE_FLOW_END_ROLE);
            targetModel.setTaskDefName("办结");
            list.add(targetModel);
        }
        List<ItemPermission> ipList;
        for (TargetModel targetModel : list) {
            String roleNames = "";
            ipList = itemPermissionService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                targetModel.getTaskDefKey());
            for (ItemPermission ip : ipList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = ip.getRoleName();
                } else {
                    roleNames += "、" + ip.getRoleName();
                }
            }
            targetModel.setRoleNames(roleNames);
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取权限绑定列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<Map<String, Object>>> getPerm(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ItemPermission> itemPermissionList = itemPermissionService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
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
    @PostMapping(value = "/removePerm")
    public Y9Result<String> removePerm(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemPermissionService.removePerm(itemId, processDefinitionId);
        return Y9Result.successMsg("清空成功");
    }

    /**
     * 保存权限
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param roleId 角色id
     * @param roleType 角色类型
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> save(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String roleId,
        @RequestParam ItemPermissionEnum roleType) {
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