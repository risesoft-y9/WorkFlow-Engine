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
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.template.ItemWordTemplateBind;
import net.risesoft.entity.template.WordTemplate;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemWordTemplateBindService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.template.WordTemplateService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/vue/itemWordBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemWordTemplateBindController {

    private final WordTemplateService wordTemplateService;

    private final ItemWordTemplateBindService itemWordTemplateBindService;

    private final ItemService itemService;

    private final RepositoryApi repositoryApi;

    private final OrgUnitApi orgUnitApi;

    /**
     * 清除绑定模板状态
     *
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    @PostMapping(value = "/clearBindStatus")
    public Y9Result<String> clearBindStatus(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        try {
            itemWordTemplateBindService.clearBindStatus(itemId, processDefinitionId);
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
            Y9Result.failure("更新绑定状态失败");
        }
        return Y9Result.successMsg("更新绑定状态成功");
    }

    /**
     * 删除正文模板绑定
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteBind")
    public Y9Result<String> deleteBind(@RequestParam String id) {
        itemWordTemplateBindService.deleteBind(id);
        return Y9Result.successMsg("删除正文模板绑定成功");
    }

    /**
     * 获取绑定正文模板列表数据
     *
     * @param itemId
     * @return
     */
    @GetMapping(value = "/getBindWordTemplateList")
    public Y9Page<Map<String, Object>> getBindWordTemplateList(@RequestParam(required = false) String itemId, int page,
        int rows) {
        try {
            List<Map<String, Object>> listMap = new ArrayList<>();
            Item item = itemService.findById(itemId);
            List<ItemWordTemplateBind> list = itemWordTemplateBindService.listByItemIdOrderByBindValueAsc(itemId);
            for (ItemWordTemplateBind itemWordTemplateBind : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", itemWordTemplateBind.getId());
                map.put("itemId", itemWordTemplateBind.getItemId());
                map.put("itemName", item.getName());
                map.put("templateId", itemWordTemplateBind.getTemplateId());
                WordTemplate word = wordTemplateService.findById(itemWordTemplateBind.getTemplateId());
                map.put("templateName", "");
                map.put("bindStatus", itemWordTemplateBind.getBindStatus());
                map.put("bindValue", itemWordTemplateBind.getBindValue());
                map.put("processDefinitionId", itemWordTemplateBind.getProcessDefinitionId());
                if (null != word && StringUtils.isNotBlank(word.getId())) {
                    map.put("templateName", word.getFileName());
                    listMap.add(map);
                }
            }
            return Y9Page.success(page, list.size() / rows, list.size(), listMap);
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
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
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid(), tenantId = Y9LoginUserHolder.getTenantId();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = processDefinition.getId();
        map.put("processDefinitionId", processDefinitionId);
        List<WordTemplate> templateList = wordTemplateService.listAll();
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
     * 获取正文模板
     *
     * @param fileName
     * @param page
     * @param rows
     * @return
     */
    @GetMapping(value = "/getWordTemplateList")
    public Y9Page<Map<String, Object>> getWordTemplateList(@RequestParam(required = false) String fileName, int page,
        int rows) {
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            String personId = Y9LoginUserHolder.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            List<WordTemplate> list = null;
            String bureauId = orgUnitApi.getBureau(tenantId, personId).getData().getId();
            if (StringUtils.isNotBlank(fileName)) {
                list = wordTemplateService.listByBureauIdAndFileNameContainingOrderByUploadTimeDesc(bureauId, fileName);
            } else {
                list = wordTemplateService.listByBureauIdOrderByUploadTimeDesc(bureauId);
            }
            for (WordTemplate wordTemplate : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", wordTemplate.getId());
                map.put("fileName", wordTemplate.getFileName());
                map.put("fileSize", wordTemplate.getFileSize());
                map.put("fileUrl", wordTemplate.getFilePath());
                map.put("personName", wordTemplate.getPersonName());
                items.add(map);
            }
            return Y9Page.success(page, list.size() / rows, list.size(), items);
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
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
        return itemWordTemplateBindService.save(itemId, processDefinitionId, templateId);
    }

    /**
     * 保存绑定信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param templateId 模板id
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String[] templateId) {
        return itemWordTemplateBindService.save(itemId, processDefinitionId, templateId);
    }

    /**
     * 保存绑定值
     *
     * @param id
     * @param bindValue
     * @return
     */
    @PostMapping(value = "/saveTemplateValue")
    public Y9Result<String> saveTemplateValue(@RequestParam String id, @RequestParam String itemId,
        @RequestParam String bindValue) {
        return itemWordTemplateBindService.saveTemplateValue(id, itemId, bindValue);
    }

    /**
     * 设置绑定模板状态
     *
     * @param id
     * @param itemId
     * @return
     */
    @PostMapping(value = "/updateBindStatus")
    public Y9Result<String> updateBindStatus(@RequestParam String id, @RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        try {
            itemWordTemplateBindService.updateBindStatus(id, itemId, processDefinitionId);
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
            Y9Result.failure("更新绑定状态失败");
        }
        return Y9Result.successMsg("更新绑定状态成功");
    }
}
