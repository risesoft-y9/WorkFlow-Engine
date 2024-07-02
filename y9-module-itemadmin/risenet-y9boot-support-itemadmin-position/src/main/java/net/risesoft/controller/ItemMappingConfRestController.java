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

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.service.ItemMappingConfService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/vue/itemMappingConf")
public class ItemMappingConfRestController {

    private final Y9FormItemBindService y9FormItemBindService;

    private final SpmApproveItemService spmApproveItemService;

    private final ItemMappingConfService itemMappingConfService;

    private final ItemMappingConfRepository itemMappingConfRepository;

    private final RepositoryApi repositoryManager;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9TableService y9TableService;

    /**
     * 根据绑定的表名称获取绑定的字段
     *
     * @param tableName 表名
     * @return
     */
    @RequestMapping(value = "/getColumns", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormField>> getColumns(@RequestParam String tableName) {
        List<Y9FormField> list = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        List<Y9FormField> formFieldList = y9FormFieldService.findByTableName(tableName);
        for (Y9FormField formField : formFieldList) {
            if (!fieldNameList.contains(formField.getFieldName())) {
                list.add(formField);
                fieldNameList.add(formField.getFieldName());
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 新建或者编辑映射
     *
     * @param id 映射id
     * @param itemId 事项id
     * @param mappingItemId 对接事项id
     * @return
     */
    @RequestMapping(value = "/getConfInfo", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getConfInfo(@RequestParam(required = false) String id,
        @RequestParam String itemId, @RequestParam(required = false) String mappingItemId) {
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
        List<Y9FormItemBind> formList =
            y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        List<String> tableNameList = new ArrayList<>();
        List<Y9Table> tableList = new ArrayList<>();
        for (Y9FormItemBind bind : formList) {
            String formId = bind.getFormId();
            List<Y9FormField> formFieldList = y9FormFieldService.findByFormId(formId);
            for (Y9FormField formField : formFieldList) {
                if (!tableNameList.contains(formField.getTableName())) {
                    Y9Table y9Table = y9TableService.findById(formField.getTableId());
                    tableNameList.add(formField.getTableName());
                    tableList.add(y9Table);
                }
            }
        }
        if (StringUtils.isNotBlank(mappingItemId)) {
            SpmApproveItem item1 = spmApproveItemService.findById(mappingItemId);
            String processDefineKey1 = item1.getWorkflowGuid();
            ProcessDefinitionModel processDefinition1 =
                repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey1).getData();
            List<Y9FormItemBind> formList1 = y9FormItemBindService
                .findByItemIdAndProcDefIdAndTaskDefKeyIsNull(mappingItemId, processDefinition1.getId());
            List<String> tableNameList1 = new ArrayList<>();
            List<Y9Table> tableList1 = new ArrayList<>();
            for (Y9FormItemBind bind : formList1) {
                String formId = bind.getFormId();
                List<Y9FormField> formFieldList = y9FormFieldService.findByFormId(formId);
                for (Y9FormField formField : formFieldList) {
                    if (!tableNameList1.contains(formField.getTableName())) {
                        Y9Table y9Table = y9TableService.findById(formField.getTableId());
                        tableNameList1.add(formField.getTableName());
                        tableList1.add(y9Table);
                    }
                }
            }
            resMap.put("mappingTableList", tableList1);
        }
        if (StringUtils.isNotBlank(id)) {
            ItemMappingConf itemMappingConf = itemMappingConfRepository.findById(id).orElse(null);
            resMap.put("itemMappingConf", itemMappingConf);
        }
        resMap.put("tableList", tableList);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取映射列表
     *
     * @param itemId 事项id
     * @param mappingId 映射标识
     * @return
     */
    @RequestMapping(value = "/getList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemMappingConf>> getList(@RequestParam String itemId, @RequestParam String mappingId) {
        List<ItemMappingConf> list = itemMappingConfService.getList(itemId, mappingId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除
     *
     * @param ids id数组
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        itemMappingConfService.delItemMappingConf(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或者修改
     *
     * @param itemMappingConf 映射信息
     * @return
     */
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(ItemMappingConf itemMappingConf) {
        itemMappingConfService.saveItemMappingConf(itemMappingConf);
        return Y9Result.successMsg("保存成功");
    }

}
