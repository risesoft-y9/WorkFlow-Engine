package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.WordTemplate;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemWordTemplateBindService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.WordTemplateService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/itemWordBind")
public class ItemWordTemplateBindController {

    @Autowired
    private WordTemplateService wordTemplateService;

    @Autowired
    private ItemWordTemplateBindService itemWordTemplateBindService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApiClient repositoryManager;

    @Autowired
    private PersonApi personManager;

    /**
     * 删除正文模板绑定
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/deleteBind", method = RequestMethod.POST, produces = "application/json")
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
    @RequestMapping(value = "/getTemplateBind", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTemplateBind(@RequestParam String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid(), tenantId = Y9LoginUserHolder.getTenantId(),
            personId = userInfo.getPersonId();
        ProcessDefinitionModel processDefinition =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey);
        String processDefinitionId = processDefinition.getId();
        map.put("processDefinitionId", processDefinitionId);
        List<WordTemplate> templateList = wordTemplateService
            .findByBureauIdOrderByUploadTimeDesc(personManager.getBureau(tenantId, personId).getId());
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
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String templateId) {
        Map<String, Object> map = itemWordTemplateBindService.save(itemId, processDefinitionId, templateId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }
}
