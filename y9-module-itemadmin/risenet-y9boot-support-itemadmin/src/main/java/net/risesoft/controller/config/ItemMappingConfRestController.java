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

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemMappingConf;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemMappingConfRepository;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.ItemMappingConfService;
import net.risesoft.service.config.Y9FormItemBindService;
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
@RequestMapping(value = "/vue/itemMappingConf", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemMappingConfRestController {

    private final Y9FormItemBindService y9FormItemBindService;

    private final SpmApproveItemService spmApproveItemService;

    private final ItemMappingConfService itemMappingConfService;

    private final ItemMappingConfRepository itemMappingConfRepository;

    private final RepositoryApi repositoryApi;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9TableService y9TableService;

    /**
     * 根据绑定的表名称获取绑定的字段
     *
     * @param tableName 表名
     * @return
     */
    @GetMapping(value = "/getColumns")
    public Y9Result<List<Y9FormField>> getColumns(@RequestParam String tableName) {
        List<Y9FormField> list = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        List<Y9FormField> formFieldList = y9FormFieldService.listByTableName(tableName);
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
    @GetMapping(value = "/getConfInfo")
    public Y9Result<Map<String, Object>> getConfInfo(@RequestParam(required = false) String id,
        @RequestParam String itemId, @RequestParam(required = false) String mappingItemId) {
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item item = spmApproveItemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
        List<Y9FormItemBind> formList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        List<String> tableNameList = new ArrayList<>();
        List<Y9Table> tableList = new ArrayList<>();
        for (Y9FormItemBind bind : formList) {
            String formId = bind.getFormId();
            List<Y9FormField> formFieldList = y9FormFieldService.listByFormId(formId);
            for (Y9FormField formField : formFieldList) {
                if (!tableNameList.contains(formField.getTableName())) {
                    Y9Table y9Table = y9TableService.findById(formField.getTableId());
                    tableNameList.add(formField.getTableName());
                    tableList.add(y9Table);
                }
            }
        }
        if (StringUtils.isNotBlank(mappingItemId)) {
            Item item1 = spmApproveItemService.findById(mappingItemId);
            String processDefineKey1 = item1.getWorkflowGuid();
            ProcessDefinitionModel processDefinition1 =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey1).getData();
            List<Y9FormItemBind> formList1 = y9FormItemBindService
                .listByItemIdAndProcDefIdAndTaskDefKeyIsNull(mappingItemId, processDefinition1.getId());
            List<String> tableNameList1 = new ArrayList<>();
            List<Y9Table> tableList1 = new ArrayList<>();
            for (Y9FormItemBind bind : formList1) {
                String formId = bind.getFormId();
                List<Y9FormField> formFieldList = y9FormFieldService.listByFormId(formId);
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
    @GetMapping(value = "/getList")
    public Y9Result<List<ItemMappingConf>> getList(@RequestParam String itemId, @RequestParam String mappingId) {
        List<ItemMappingConf> list = itemMappingConfService.listByItemIdAndMappingId(itemId, mappingId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除
     *
     * @param ids id数组
     */
    @PostMapping(value = "/remove")
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
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(ItemMappingConf itemMappingConf) {
        itemMappingConfService.saveItemMappingConf(itemMappingConf);
        return Y9Result.successMsg("保存成功");
    }

}
