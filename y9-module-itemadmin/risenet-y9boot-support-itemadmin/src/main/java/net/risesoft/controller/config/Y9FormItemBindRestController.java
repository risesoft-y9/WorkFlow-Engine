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
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.controller.vo.Y9FormItemBindVO;
import net.risesoft.controller.vo.Y9FormVO;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormItemMobileBind;
import net.risesoft.entity.template.ItemPrintTemplateBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.jpa.Y9FormItemMobileBindRepository;
import net.risesoft.service.PrintTemplateService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/y9form/item", produces = MediaType.APPLICATION_JSON_VALUE)
public class Y9FormItemBindRestController {

    private final Y9FormItemBindService y9FormItemBindService;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

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
    @GetMapping(value = "/bindList")
    public Y9Result<List<Y9FormItemBind>> bindList(@RequestParam String itemId, @RequestParam String procDefId,
        @RequestParam(required = false) String taskDefKey) {
        List<Y9FormItemBind> eformItemList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, procDefId, taskDefKey);
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
    @PostMapping(value = "/copyForm")
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
    @PostMapping(value = "/deleteBind")
    public Y9Result<String> delete(@RequestParam String id) {
        return y9FormItemBindService.delete(id);
    }

    /**
     * 删除手机端绑定表单
     *
     * @param id 绑定id
     * @return
     */
    @PostMapping(value = "/deleteMobileBind")
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
    @GetMapping(value = "/formList")
    public Y9Result<List<Y9FormVO>> formList(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String systemName) {
        List<Y9FormVO> listMap = new ArrayList<>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%%");
        List<Y9FormItemBind> bindList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, processDefinitionId, taskDefKey);
        for (Y9Form y9Form : list) {
            boolean isbind = false;
            for (Y9FormItemBind bind : bindList) {
                if (bind.getFormId().equals(y9Form.getId())) {
                    isbind = true;
                    break;
                }
            }
            if (!isbind) {
                Y9FormVO form = new Y9FormVO();
                form.setFormId(y9Form.getId());
                form.setFormName(y9Form.getFormName());
                listMap.add(form);
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
    @GetMapping(value = "/getBindForm")
    public Y9Result<Y9FormItemBind> getBindForm(@RequestParam(required = false) String id,
        @RequestParam String procDefId) {
        Y9FormItemBind eformItemBind;
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isNotBlank(id)) {
            eformItemBind = y9FormItemBindService.getById(id);
            Y9Form form = y9FormRepository.findById(eformItemBind.getFormId()).orElse(null);
            eformItemBind.setFormName(form != null ? form.getFormName() : "表单不存在");
        } else {
            id = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            eformItemBind = new Y9FormItemBind();
            eformItemBind.setId(id);
            eformItemBind.setProcessDefinitionId(procDefId);
        }
        ProcessDefinitionModel processDefinition =
            repositoryApi.getProcessDefinitionById(tenantId, procDefId).getData();
        eformItemBind.setProcDefName(processDefinition.getName());
        return Y9Result.success(eformItemBind, "获取成功");
    }

    /**
     * 获取流程定义信息
     *
     * @param processDefinitionId 流程定义id
     * @param itemId 事项id
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<Y9FormItemBindVO>> getBpmList(@RequestParam String processDefinitionId,
        @RequestParam String itemId) {
        List<Y9FormItemBindVO> list = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TargetModel> targetModelList = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        Y9FormItemBindVO map;
        List<Y9FormItemBind> pcBindList;
        List<Y9FormItemMobileBind> mobileBindList;
        for (TargetModel targetModel : targetModelList) {
            map = new Y9FormItemBindVO();
            String eformNames = "";
            String mobileFormName = "";
            pcBindList = y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey4Own(itemId, processDefinitionId,
                targetModel.getTaskDefKey());
            mobileBindList = y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(itemId,
                processDefinitionId, targetModel.getTaskDefKey());
            for (Y9FormItemBind eib : pcBindList) {
                String formId = eib.getFormId();
                Y9Form form = y9FormRepository.findById(formId).orElse(null);
                String formName = form != null ? form.getFormName() : "表单不存在";
                eformNames = Y9Util.genCustomStr(eformNames, formName);
            }
            if (!mobileBindList.isEmpty()) {
                for (Y9FormItemMobileBind mobileBind : mobileBindList) {
                    String formId = mobileBind.getFormId();
                    Y9Form form = y9FormRepository.findById(formId).orElse(null);
                    String formName = form != null ? form.getFormName() : "表单不存在";
                    mobileFormName = Y9Util.genCustomStr(mobileFormName, formName);
                }
            }
            map.setTaskDefName(targetModel.getTaskDefName());
            map.setEformNames(eformNames);
            map.setMobileFormName(mobileFormName);
            map.setTaskDefKey(targetModel.getTaskDefKey());
            list.add(map);
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取y9表单列表
     *
     * @param systemName 系统名称
     * @return
     */
    @GetMapping(value = "/getformList")
    public Y9Result<List<Y9FormVO>> getformList(@RequestParam String systemName) {
        List<Y9FormVO> listMap = new ArrayList<>();
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(systemName, "%%");
        for (Y9Form y9Form : list) {
            Y9FormVO form = new Y9FormVO();
            form.setFormName(y9Form.getFormName());
            form.setFormId(y9Form.getId());
            listMap.add(form);
        }
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 获取打印表单
     *
     * @param itemId 事项id
     * @param formName 表单名称
     * @return
     */
    @GetMapping(value = "/getPrintFormList")
    public Y9Result<List<Y9FormVO>> listFormByItemId(@RequestParam String itemId,
        @RequestParam(required = false) String formName) {
        List<Y9FormVO> listmap = new ArrayList<>();
        SpmApproveItem spmApproveItem = spmApproveItemService.findById(itemId);
        List<Y9Form> list = y9FormRepository.findBySystemNameAndFormNameLike(spmApproveItem.getSystemName(),
            StringUtils.isNotBlank(formName) ? "%" + formName + "%" : "%%");
        List<ItemPrintTemplateBind> bindList = printTemplateService.listTemplateBindByItemId(itemId);
        ItemPrintTemplateBind itemPrintTemplateBind = !bindList.isEmpty() ? bindList.get(0) : null;
        for (Y9Form y9Form : list) {
            Y9FormVO form = new Y9FormVO();
            boolean isBind =
                itemPrintTemplateBind != null && itemPrintTemplateBind.getTemplateId().equals(y9Form.getId());
            if (!isBind) {
                form.setFormName(y9Form.getFormName());
                form.setFormId(y9Form.getId());
                listmap.add(form);
            }
        }
        return Y9Result.success(listmap, "获取成功");
    }

    /**
     * 获取绑定的手机端表单列表
     *
     * @param itemId 事项id
     * @param procDefId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @GetMapping(value = "/mobileBindList")
    public Y9Result<List<Y9FormItemMobileBind>> mobileBindList(@RequestParam String itemId,
        @RequestParam String procDefId, @RequestParam(required = false) String taskDefKey) {
        List<Y9FormItemMobileBind> eformItemList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey4OwnMobile(itemId, procDefId, taskDefKey);
        for (Y9FormItemMobileBind bind : eformItemList) {
            Y9Form form = y9FormRepository.findById(bind.getFormId()).orElse(null);
            bind.setFormName(form != null ? form.getFormName() : "表单不存在");
        }

        return Y9Result.success(eformItemList, "获取成功");
    }

    /**
     * 保存绑定表单
     *
     * @param eformItem 绑定信息
     * @return
     */
    @PostMapping(value = "/saveBind")
    public Y9Result<String> save(Y9FormItemBind eformItem) {
        return y9FormItemBindService.save(eformItem);
    }

    /**
     * 保存手机端绑定表单
     *
     * @param eformItem 绑定信息
     * @return
     */
    @PostMapping(value = "/saveMobileBind")
    public Y9Result<String> saveMobileBind(Y9FormItemMobileBind eformItem) {
        return y9FormItemBindService.save(eformItem);
    }
}
