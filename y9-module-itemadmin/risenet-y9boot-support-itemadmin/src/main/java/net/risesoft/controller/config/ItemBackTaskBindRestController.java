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
import net.risesoft.entity.BackTaskConf;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemBackTaskConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * 退回配置
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemBackTaskBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemBackTaskBindRestController {

    private final ItemBackTaskConfService itemBackTaskConfService;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 获取退回任务绑定列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<BackTaskConf>> getBindList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        List<BackTaskConf> list = itemBackTaskConfService.listByTaskDefKey(itemId, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        List<TargetModel> list;
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        for (TargetModel targetModel : list) {
            String backTask = "";
            String taskDefKey = targetModel.getTaskDefKey();
            BackTaskConf conf = itemBackTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, taskDefKey);
            if (conf != null && StringUtils.isNotBlank(conf.getBackTaskDefKey())) {
                String backTaskDefKey = conf.getBackTaskDefKey();
                String[] taskDefKeys = backTaskDefKey.split(",");
                for (String key : taskDefKeys) {
                    String finalKey = key;
                    TargetModel target =
                        list.stream().filter(t -> t.getTaskDefKey().equals(finalKey)).findFirst().orElse(null);
                    String targetTaskDefName = target != null ? target.getTaskDefName() : "节点不存在";
                    key = targetTaskDefName + "(" + key + ")";
                    backTask = Y9Util.genCustomStr(backTask, key);
                }
            }
            targetModel.setRealTaskDefName(backTask);
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除绑定
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param removeTaskKey 删除任务key
     * @return
     */
    @PostMapping(value = "/removeBind")
    public Y9Result<String> removeBind(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam String[] removeTaskKey) {
        itemBackTaskConfService.removeBind(itemId, processDefinitionId, taskDefKey, removeTaskKey);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存退回任务绑定
     *
     * @param itemId 事项id
     * @param bindTaskDefKey 绑定任务key
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @PostMapping(value = "/saveBindTask")
    public Y9Result<String> saveBindTask(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey, @RequestParam String[] bindTaskDefKey) {
        itemBackTaskConfService.saveBindTask(itemId, processDefinitionId, taskDefKey, bindTaskDefKey);
        return Y9Result.successMsg("绑定成功");
    }

}
