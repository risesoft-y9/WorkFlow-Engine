package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.OrganWord;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOrganWordBindService;
import net.risesoft.service.OrganWordService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemOrganWordBind")
public class ItemOrganWordBindController {

    private final ItemOrganWordBindService itemOrganWordBindService;

    private final OrganWordService organWordService;

    private final ProcessDefinitionApi processDefinitionManager;

    /**
     * 复制按钮配置
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemOrganWordBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemOrganWordBind>> getBindList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOrganWordBind> list = itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
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
        List<TargetModel> list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false).getData();
        List<ItemOrganWordBind> bindList;
        for (TargetModel targetModel : list) {
            String bindNames = "";
            bindList = itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, targetModel.getTaskDefKey());
            for (ItemOrganWordBind cb : bindList) {
                if (StringUtils.isEmpty(bindNames)) {
                    bindNames = cb.getOrganWordName();
                } else {
                    bindNames += "、" + cb.getOrganWordName();
                }
            }
            targetModel.setBindNames(bindNames);
        }
        resMap.put("rows", list);
        // TODO
        return Y9Result.success(resMap, "获取成功");
    }

    @RequestMapping(value = "/getOrganWordList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getOrganWordList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        Map<String, Object> map = new HashMap<>(16);
        List<ItemOrganWordBind> bindList = itemOrganWordBindService
            .findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<OrganWord> owList = organWordService.findAll();
        List<OrganWord> owListTemp = new ArrayList<>();
        if (bindList.isEmpty()) {
            owListTemp = owList;
        } else {
            for (OrganWord cb : owList) {
                boolean isBind = false;
                for (ItemOrganWordBind bind : bindList) {
                    if (bind.getOrganWordCustom().equals(cb.getCustom())) {
                        isBind = true;
                        break;
                    }
                }
                if (!isBind) {
                    owListTemp.add(cb);
                }
            }
        }
        map.put("rows", owListTemp);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/removeBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeBind(String id) {
        itemOrganWordBindService.remove(id);
        return Y9Result.successMsg("删除成功");
    }

    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBind(@RequestParam String custom, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemOrganWordBindService.save(custom, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("绑定成功");
    }
}
