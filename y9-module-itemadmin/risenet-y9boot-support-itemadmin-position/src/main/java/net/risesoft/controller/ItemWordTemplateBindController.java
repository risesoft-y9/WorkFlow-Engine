package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

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

    /**
     * 保存绑定信息
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param templateId 模板id
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> saveBind(
            @RequestParam String itemId,
            @RequestParam String processDefinitionId,
            @RequestParam String[] templateId) {
        Map<String, Object> map = itemWordTemplateBindService.save(itemId, processDefinitionId, templateId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存绑定值
     * @param id
     * @param bindValue
     * @return
     */
    @PostMapping(value = "/saveTemplateValue")
    public Y9Result<String> saveTemplateValue(
            @RequestParam String id,
            @RequestParam String bindValue) {
        Map<String, Object> map = itemWordTemplateBindService.saveTemplateValue(id, bindValue);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 设置绑定模板状态
     * @param id
     * @param itemId
     * @return
     */
    @PostMapping(value = "/updateBindStatus")
    public Y9Result<String> updateBindStatus(@RequestParam String id,@RequestParam String itemId,@RequestParam String processDefinitionId) {
        try {
            itemWordTemplateBindService.updateBindStatus(id, itemId,processDefinitionId);
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
            Y9Result.failure("更新绑定状态失败");
        }
        return Y9Result.successMsg("更新绑定状态成功");
    }

    /**
     * 清除绑定模板状态
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    @PostMapping(value = "/clearBindStatus")
    public Y9Result<String> clearBindStatus(@RequestParam String itemId,@RequestParam String processDefinitionId) {
        try {
            itemWordTemplateBindService.clearBindStatus( itemId,processDefinitionId);
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
            Y9Result.failure("更新绑定状态失败");
        }
        return Y9Result.successMsg("更新绑定状态成功");
    }

    /**
     * 获取正文模板
     * @param fileName
     * @param page
     * @param rows
     * @return
     */
    @GetMapping(value = "/getWordTemplateList")
    public Y9Result<Map<String, Object>> getWordTemplateList(@RequestParam(required = false)String fileName,int page,int rows){
        Map<String, Object> ret_map = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            String personId = Y9LoginUserHolder.getPersonId(),tenantId = Y9LoginUserHolder.getTenantId();
            List<WordTemplate> list = null;
            String bureauId = orgUnitApi.getBureau(tenantId, personId).getData().getId();
            if(StringUtils.isNotBlank(fileName)) {
                list = wordTemplateService.findByBureauIdAndFileNameContainingOrderByUploadTimeDesc(bureauId,fileName);
            }else {
                list = wordTemplateService.findByBureauIdOrderByUploadTimeDesc(bureauId);
            }
            for (WordTemplate wordTemplate : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", wordTemplate.getId());
                map.put("fileName", wordTemplate.getFileName());
                map.put("fileSize", wordTemplate.getFileSize());
                map.put("fileUrl", wordTemplate.getFilePath());
                map.put("personName", wordTemplate.getPersonName());
                items.add(map);
            }
            ret_map.put("rows", items);
            ret_map.put("currpage", page);
            ret_map.put("totalpages", list.size()/rows);
            ret_map.put("total", list.size());
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
            Y9Result.failure("获取失败");
        }
        return Y9Result.success(ret_map, "获取成功");
    }

    /**
     * 获取绑定正文模板列表数据
     * @param itemId
     * @return
     */
    @GetMapping(value = "/getBindWordTemplateList")
    public Y9Result<Map<String, Object>> getBindWordTemplateList(@RequestParam(required = false) String itemId, int page,int rows) {
        Map<String, Object> ret_map = new HashMap<String, Object>();
        try {
            List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>();
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            List<ItemWordTemplateBind> list = itemWordTemplateBindService.findByItemIdOrderByBindValueAsc(itemId);
            for (ItemWordTemplateBind itemWordTemplateBind : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", itemWordTemplateBind.getId());
                map.put("itemId", itemWordTemplateBind.getItemId());
                map.put("itemName", item.getName());
                map.put("templateId", itemWordTemplateBind.getTemplateId());
                WordTemplate word = wordTemplateService.findById(itemWordTemplateBind.getTemplateId());
                map.put("templateName", "");
                map.put("bindStatus", itemWordTemplateBind.getBindStatus());
                map.put("bindValue", itemWordTemplateBind.getBindValue());
                map.put("processDefinitionId", itemWordTemplateBind.getProcessDefinitionId());
                if(null != word && StringUtils.isNotBlank(word.getId())) {
                    map.put("templateName", word.getFileName());
                    list_map.add(map);
                }
            }
            ret_map.put("rows", list_map);
            ret_map.put("currpage", page);
            ret_map.put("totalpages", list.size()/rows);
            ret_map.put("total", list.size());
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
            Y9Result.failure("获取失败");
        }
        return Y9Result.success(ret_map, "获取成功");
    }
}
