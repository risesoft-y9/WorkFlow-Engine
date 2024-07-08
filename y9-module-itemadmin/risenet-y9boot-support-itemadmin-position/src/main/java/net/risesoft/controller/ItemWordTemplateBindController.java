package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.WordTemplate;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemWordTemplateBindService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.WordTemplateService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemWordBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemWordTemplateBindController {

    private final WordTemplateService wordTemplateService;

    private final ItemWordTemplateBindService itemWordTemplateBindService;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    /**
     * 删除正文模板绑定
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteBind")
    public Y9Result<String> deleteBind(@RequestParam String id) {
        Map<String, Object> map = itemWordTemplateBindService.deleteBind(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 获取绑定信息
     *
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getTemplateBind")
    public Y9Result<Map<String, Object>> getTemplateBind(@RequestParam String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid(), tenantId = Y9LoginUserHolder.getTenantId();
        ProcessDefinitionModel processDefinition =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = processDefinition.getId();
        map.put("processDefinitionId", processDefinitionId);
        List<WordTemplate> templateList = wordTemplateService.findAll();
        ItemWordTemplateBind wordTemplateBind =
            itemWordTemplateBindService.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
        String tempName = "", bindId = "";
        if (wordTemplateBind != null) {
            for (WordTemplate wordTemplate : templateList) {
                if (wordTemplateBind.getTemplateId().equals(wordTemplate.getId())) {
                    tempName = wordTemplate.getFileName();
                    bindId = wordTemplateBind.getId();
                }
            }
        }
        map.put("tempName", tempName);
        map.put("bindId", bindId);
        map.put("templateList", templateList);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 保存绑定信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param templateId 模板id
     * @return
     */
    @PostMapping(value = "/save")
    public Y9Result<String> save(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String templateId) {
        Map<String, Object> map = itemWordTemplateBindService.save(itemId, processDefinitionId, templateId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }
}
