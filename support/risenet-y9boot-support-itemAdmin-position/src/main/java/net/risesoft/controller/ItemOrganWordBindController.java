package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.OrganWord;
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
@RequestMapping(value = "/vue/itemOrganWordBind")
public class ItemOrganWordBindController {

    @Autowired
    private ItemOrganWordBindService itemOrganWordBindService;

    @Autowired
    private OrganWordService organWordService;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    /**
     * 复制按钮配置
     *
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        itemOrganWordBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    @ResponseBody
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemOrganWordBind>> getBindList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOrganWordBind> list = itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        List<ItemOrganWordBind> bindList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String bindNames = "";
            bindList = itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, (String)map.get("taskDefKey"));
            for (ItemOrganWordBind cb : bindList) {
                if (StringUtils.isEmpty(bindNames)) {
                    bindNames = cb.getOrganWordName();
                } else {
                    bindNames += "、" + cb.getOrganWordName();
                }
            }
            map.put("bindNames", bindNames);
        }
        resMap.put("rows", list);
        return Y9Result.success(resMap, "获取成功");
    }

    @ResponseBody
    @RequestMapping(value = "/getOrganWordList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getOrganWordList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<ItemOrganWordBind> bindList = itemOrganWordBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<OrganWord> owList = organWordService.findAll();
        List<OrganWord> owListTemp = new ArrayList<>();
        if (bindList.isEmpty()) {
            owListTemp = owList;
        } else {
            for (OrganWord cb : owList) {
                Boolean isBind = false;
                for (ItemOrganWordBind bind : bindList) {
                    if (bind.getOrganWordCustom().equals(cb.getCustom())) {
                        isBind = true;
                        continue;
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
     * @param bindId 绑定id
     * @return
     */
    @RequestMapping(value = "/removeBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeBind(String id) {
        itemOrganWordBindService.remove(id);
        return Y9Result.successMsg("删除成功");
    }

    @ResponseBody
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBind(@RequestParam(required = true) String custom, @RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemOrganWordBindService.save(custom, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("绑定成功");
    }
}
