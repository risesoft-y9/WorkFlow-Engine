package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemTaskConfService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/vue/itemTaskConfig")
public class ItemTaskConfRestController {

    @Autowired
    private ItemTaskConfService taskConfService;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    /**
     * 复制签收配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @RequestMapping(value = "/copyTaskConfig", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyTaskConfig(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        taskConfService.copyTaskConf(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        for (Map<String, Object> map : list) {
            if (map.get(SysVariables.MULTIINSTANCE) != null && !((String)map.get(SysVariables.MULTIINSTANCE)).equals(SysVariables.SEQUENTIAL)) {
                map.put("id", "");
                map.put("signTask", false);
                map.put("taskType", "单人");
                if (((String)map.get(SysVariables.MULTIINSTANCE)).equals(SysVariables.PARALLEL)) {
                    map.put("taskType", "并行");
                }
                String taskDefKey = (String)map.get("taskDefKey");
                ItemTaskConf confTemp = taskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId, processDefinitionId, taskDefKey);
                if (null != confTemp) {
                    map.put("id", confTemp.getId());
                    map.put("signTask", confTemp.getSignTask());
                }
                resList.add(map);
            }
        }
        resMap.put("rows", resList);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 保存任务基本配置
     *
     * @param entity
     * @return
     */
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(ItemTaskConf itemTaskConf) {
        taskConfService.save(itemTaskConf);
        return Y9Result.successMsg("保存成功");
    }
}
