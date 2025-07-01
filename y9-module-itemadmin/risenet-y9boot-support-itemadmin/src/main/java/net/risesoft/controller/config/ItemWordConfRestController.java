package net.risesoft.controller.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemWordConf;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemWordConfService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemWordConf", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemWordConfRestController {

    private final ItemWordConfService itemWordConfService;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 绑定角色
     *
     * @param roleIds 角色ids
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/bindRole")
    public Y9Result<String> bindRole(@RequestParam String roleIds, @RequestParam String id) {
        itemWordConfService.bindRole(id, roleIds);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteRole")
    public Y9Result<String> deleteRole(@RequestParam String roleId, @RequestParam String id) {
        itemWordConfService.deleteRole(id, roleId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取正文与角色绑定的数据集合
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemWordConf>> getBindList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemWordConf> oftrbList = itemWordConfService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        return Y9Result.success(oftrbList, "获取成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId 流程定义ID
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String processDefinitionId,
        @RequestParam String itemId) {
        List<TargetModel> list;
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        for (TargetModel targetModel : list) {
            StringBuilder names = new StringBuilder();
            List<ItemWordConf> bindList = itemWordConfService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, targetModel.getTaskDefKey());
            for (ItemWordConf bind : bindList) {
                if (StringUtils.isEmpty(names)) {
                    names.append(bind.getWordType());
                } else {
                    names.append("、" + bind.getWordType());
                }
            }
            targetModel.setBindNames(names.toString());
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String id) {
        itemWordConfService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 绑定正文组件
     *
     * @param wordType 正文类型
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(@RequestParam String wordType, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemWordConfService.save(wordType, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("保存成功");
    }
}
