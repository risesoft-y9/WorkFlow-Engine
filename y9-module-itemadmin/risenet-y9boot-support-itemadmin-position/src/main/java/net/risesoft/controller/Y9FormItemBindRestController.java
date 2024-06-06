package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.Y9FormItemMobileBindRepository;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/vue/y9form/item")
public class Y9FormItemBindRestController {

    private final Y9FormItemBindService y9FormItemBindService;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryManager;

    private final ProcessDefinitionApi processDefinitionManager;

    private final Y9FormRepository y9FormRepository;

    private final PrintTemplateService printTemplateService;

    /**
     * 获取绑定的表单列表
     *
     * @param itemId 事项id
     * @param procDefId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @RequestMapping(value = "/bindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormItemBind>> bindList(@RequestParam String itemId, @RequestParam String procDefId, @RequestParam(required = false) String taskDefKey) {
        List<Y9FormItemBind> eformItemList = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, procDefId, taskDefKey);
        for (Y9FormItemBind bind : eformItemList) {
            Y9Form form = y9FormRepository.findById(bind.getFormId()).orElse(null);
            bind.setFormName(form != null ? form.getFormName() : "表单不存在");
        }

        return Y9Result.success(eformItemList, "获取成功");
    }

    /**
     * 复制表单
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/copyForm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyForm(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        y9FormItemBindService.copyEform(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 删除绑定表单
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/deleteBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam String id) {
        Map<String, Object> map = y9FormItemBindService.delete(id);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 删除手机端绑定表单
     *
     * @param id 绑定id
     * @return
     */
    @RequestMapping(value = "/deleteMobileBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteMobileBind(@RequestParam String id) {
        y9FormItemMobileBindRepository.deleteById(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取y9表单列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @param systemName 系统名称
     * @return
     */
    @RequestMapping(value = "/formList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> formList(@RequestParam String itemId, @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey, @RequestParam String systemName) {
        List<Map<String, Object>> listMap = new ArrayList<>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%%");
        List<Y9FormItemBind> bindList = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, processDefinitionId, taskDefKey);
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<>(16);
            boolean isbind = false;
            for (Y9FormItemBind bind : bindList) {
                if (bind.getFormId().equals(y9Form.getId())) {
                    isbind = true;
                    break;
                }
            }
            if (!isbind) {
                map.put("formName", y9Form.getFormName());
                map.put("formId", y9Form.getId());
                listMap.add(map);
            }
        }
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 获取绑定表单信息
     *
     * @param id 绑定id
     * @param procDefId 流程定义id
     * @return
     */
    @RequestMapping(value = "/getBindForm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Y9FormItemBind> getBindForm(@RequestParam(required = false) String id, @RequestParam String procDefId) {
        Y9FormItemBind eformItemBind;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(id)) {
            eformItemBind = y9FormItemBindService.findOne(id);
            Y9Form form = y9FormRepository.findById(eformItemBind.getFormId()).orElse(null);
            eformItemBind.setFormName(form != null ? form.getFormName() : "表单不存在");
        } else {
            id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            eformItemBind = new Y9FormItemBind();
            eformItemBind.setId(id);
            eformItemBind.setProcessDefinitionId(procDefId);
        }
        ProcessDefinitionModel processDefinition = repositoryManager.getProcessDefinitionById(tenantId, procDefId);
        eformItemBind.setProcDefName(processDefinition.getName());
        return Y9Result.success(eformItemBind, "获取成功");
    }

    /**
     * 获取流程定义信息
     *
     * @param processDefinitionId 流程定义id
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam String processDefinitionId, @RequestParam String itemId) {
        List<Map<String, Object>> list;
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        List<Y9FormItemBind> eibList;
        List<Y9FormItemMobileBind> eibList1;
        for (Map<String, Object> map : list) {
            String eformNames = "";
            String mobileFormName = "";
            String mobileFormId = "";
            String mobileBindId = "";
            eibList = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, processDefinitionId, (String)map.get("taskDefKey"));
            eibList1 = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(itemId, processDefinitionId, (String)map.get("taskDefKey"));
            for (Y9FormItemBind eib : eibList) {
                String formId = eib.getFormId();
                Y9Form form = y9FormRepository.findById(formId).orElse(null);
                eformNames = Y9Util.genCustomStr(eformNames, form != null ? form.getFormName() : "表单不存在");
            }
            if (!eibList1.isEmpty()) {
                Y9Form form = y9FormRepository.findById(eibList1.get(0).getFormId()).orElse(null);
                mobileFormName = form != null ? form.getFormName() : "表单不存在";
                mobileFormId = eibList1.get(0).getFormId();
                mobileBindId = eibList1.get(0).getId();
            }
            map.put("eformNames", eformNames);
            map.put("mobileFormName", mobileFormName);
            map.put("mobileFormId", mobileFormId);
            map.put("mobileBindId", mobileBindId);
        }
        resMap.put("rows", list);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取y9表单列表
     *
     * @param systemName 系统名称
     * @return
     */
    @RequestMapping(value = "/getformList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getformList(@RequestParam String systemName) {
        List<Map<String, Object>> listmap = new ArrayList<>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%%");
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("formName", y9Form.getFormName());
            map.put("formId", y9Form.getId());
            listmap.add(map);
        }
        return Y9Result.success(listmap, "获取成功");
    }

    /**
     * 获取打印表单
     *
     * @param itemId 事项id
     * @param formName 表单名称
     * @return
     */
    @RequestMapping(value = "/getPrintFormList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getFormList(@RequestParam String itemId, @RequestParam(required = false) String formName) {
        List<Map<String, Object>> listmap = new ArrayList<>();
        SpmApproveItem spmApproveItem = spmApproveItemService.findById(itemId);
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(spmApproveItem.getSystemName(), StringUtils.isNotBlank(formName) ? "%" + formName + "%" : "%%");
        List<ItemPrintTemplateBind> bindList = printTemplateService.getTemplateBindList(itemId);
        ItemPrintTemplateBind itemPrintTemplateBind = !bindList.isEmpty() ? bindList.get(0) : null;
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<>(16);
            boolean isBind = itemPrintTemplateBind != null && itemPrintTemplateBind.getTemplateId().equals(y9Form.getId());
            if (!isBind) {
                map.put("formName", y9Form.getFormName());
                map.put("formId", y9Form.getId());
                listmap.add(map);
            }
        }
        return Y9Result.success(listmap, "获取成功");
    }

    /**
     * 保存绑定表单
     *
     * @param eformItem 绑定信息
     * @return
     */
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(Y9FormItemBind eformItem) {
        Map<String, Object> map = y9FormItemBindService.save(eformItem);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 保存手机端绑定表单
     *
     * @param eformItem 绑定信息
     * @return
     */
    @RequestMapping(value = "/saveMobileBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveMobileBind(Y9FormItemMobileBind eformItem) {
        Map<String, Object> map = y9FormItemBindService.save(eformItem);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }
}
