package net.risesoft.controller.config;

import java.util.ArrayList;
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
import net.risesoft.entity.TaskTimeConf;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.TaskTimeConfService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/taskTimeConfig", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskTimeConfRestController {

    private final TaskTimeConfService taskTimeConfService;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 复制任务时间配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @PostMapping(value = "/copyTaskConfig")
    public Y9Result<String> copyTaskConfig(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        taskTimeConfService.copyTaskConf(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TaskTimeConf>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        List<TaskTimeConf> resList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TargetModel> list = processDefinitionApi.getNodes(tenantId, processDefinitionId, false).getData();
        for (TargetModel targetModel : list) {
            if (StringUtils.isNotBlank(targetModel.getTaskDefKey())) {
                TaskTimeConf conf = taskTimeConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                    processDefinitionId, targetModel.getTaskDefKey());
                if (conf == null) {
                    conf = new TaskTimeConf();
                    conf.setId("");
                    conf.setTimeoutInterrupt(0);
                    conf.setLeastTime(0);
                }
                conf.setTaskType("单人");
                if (targetModel.getMultiInstance().equals(SysVariables.PARALLEL)) {
                    conf.setTaskType("并行");
                } else if (targetModel.getMultiInstance().equals(SysVariables.SEQUENTIAL)) {
                    conf.setTaskType("串行");
                }
                conf.setItemId(itemId);
                conf.setTaskDefName(targetModel.getTaskDefName());
                conf.setTaskDefKey(targetModel.getTaskDefKey());
                conf.setProcessDefinitionId(processDefinitionId);
                resList.add(conf);
            }
        }
        return Y9Result.success(resList, "获取成功");
    }

    /**
     * 保存任务基本配置
     *
     * @param conf 任务基本配置
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(TaskTimeConf conf) {
        taskTimeConfService.save(conf);
        return Y9Result.successMsg("保存成功");
    }
}
