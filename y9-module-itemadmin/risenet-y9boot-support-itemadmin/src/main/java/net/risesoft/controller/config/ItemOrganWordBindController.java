package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemOrganWordBind;
import net.risesoft.entity.OrganWord;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OrganWordService;
import net.risesoft.service.config.ItemOrganWordBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemOrganWordBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemOrganWordBindController {

    private final ItemOrganWordBindService itemOrganWordBindService;

    private final OrganWordService organWordService;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 复制按钮配置
     *
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemOrganWordBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemOrganWordBind>> getBindList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOrganWordBind> list = itemOrganWordBindService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
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
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TargetModel> list = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        List<ItemOrganWordBind> bindList;
        for (TargetModel targetModel : list) {
            StringBuilder bindNames = new StringBuilder();
            bindList = itemOrganWordBindService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, targetModel.getTaskDefKey());
            for (ItemOrganWordBind cb : bindList) {
                if (StringUtils.isEmpty(bindNames)) {
                    bindNames.append(cb.getOrganWordName());
                } else {
                    bindNames.append("、" + cb.getOrganWordName());
                }
            }
            targetModel.setBindNames(bindNames.toString());
        }
        return Y9Result.success(list, "获取成功");
    }

    @GetMapping(value = "/getOrganWordList")
    public Y9Result<List<OrganWord>> getOrganWordList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOrganWordBind> bindList = itemOrganWordBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        List<OrganWord> owList = organWordService.listAll();
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
        return Y9Result.success(owListTemp, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/removeBind")
    public Y9Result<String> removeBind(String id) {
        itemOrganWordBindService.remove(id);
        return Y9Result.successMsg("删除成功");
    }

    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(@RequestParam String custom, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemOrganWordBindService.save(custom, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("绑定成功");
    }
}
