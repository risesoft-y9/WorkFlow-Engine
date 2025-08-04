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
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.view.ItemViewConf;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemViewConfService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.core.ItemService;
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
@RequestMapping(value = "/vue/itemViewConf", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemViewConfRestController {

    private final Y9FormItemBindService y9FormItemBindService;

    private final ItemService itemService;

    private final ItemViewConfService itemViewConfService;

    private final RepositoryApi repositoryApi;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9TableService y9TableService;

    /**
     * 保存或者修改
     *
     * @param ids 视图id
     * @param viewType 视图类型
     * @return
     */
    @PostMapping(value = "/copyView")
    public Y9Result<String> copyView(String[] ids, String viewType) {
        itemViewConfService.copyView(ids, viewType);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 根据事项唯一标示查找视图配置
     *
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return
     */
    @GetMapping(value = "/findByItemId")
    public Y9Result<List<ItemViewConf>> findByItemId(@RequestParam String itemId, @RequestParam String viewType) {
        List<ItemViewConf> list = itemViewConfService.listByItemIdAndViewType(itemId, viewType);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 根据绑定的表名称获取绑定的字段
     *
     * @param tableName 表名
     * @return
     */
    @GetMapping(value = "/getColumns")
    public Y9Result<List<Y9FormField>> getColumns(@RequestParam String tableName, @RequestParam String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Y9FormField> list = new ArrayList<>();
        List<String> fieldNameList = new ArrayList<>();
        Item item = itemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
        List<Y9FormItemBind> formList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        for (Y9FormItemBind bind : formList) {
            List<Y9FormField> formFieldList = y9FormFieldService.listByTableNameAndFormId(tableName, bind.getFormId());
            for (Y9FormField formField : formFieldList) {
                if (!fieldNameList.contains(formField.getFieldName())
                    && !formField.getFieldName().equalsIgnoreCase("guid")
                    && !formField.getFieldName().equalsIgnoreCase("processInstanceId")) {
                    list.add(formField);
                    fieldNameList.add(formField.getFieldName());
                }
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 新建或者编辑视图
     *
     * @param id 视图id
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/newOrModify")
    public Y9Result<Map<String, Object>> newOrModify(@RequestParam(required = false) String id,
        @RequestParam String itemId) {
        Map<String, Object> resMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        Item item = itemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
        List<Y9FormItemBind> formList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        List<Y9Table> tableList = new ArrayList<>();
        List<Map<String, Object>> tableField = new ArrayList<>();
        for (Y9FormItemBind bind : formList) {
            String formId = bind.getFormId();
            List<Y9FormField> formFieldList = y9FormFieldService.listByFormId(formId);
            for (Y9FormField formField : formFieldList) {
                Y9Table y9Table = y9TableService.findById(formField.getTableId());
                if (!tableList.contains(y9Table)) {
                    tableList.add(y9Table);
                }
                List<Y9FormField> fieldlist = new ArrayList<>();
                Map<String, Object> tableFieldMap = tableField.stream().filter(t -> {
                    return t.get("tableName").equals(y9Table.getTableName());
                }).findFirst().orElse(null);
                if (tableFieldMap == null) {// 判断是否已经存在
                    tableFieldMap = new HashMap<>();
                    tableFieldMap.put("tableName", y9Table.getTableName());
                    if (y9Table.getTableName().equals(formField.getTableName())) {// 判断是否是同一张表
                        fieldlist.add(formField);
                    }
                    tableFieldMap.put("fieldlist", fieldlist);
                    tableField.add(tableFieldMap);
                } else {
                    fieldlist = (List<Y9FormField>)tableFieldMap.get("fieldlist");
                    if (y9Table.getTableName().equals(formField.getTableName())) {// 判断是否是同一张表
                        Y9FormField oldformField = fieldlist.stream().filter(t -> {
                            return t.getFieldName().equals(formField.getFieldName());
                        }).findFirst().orElse(null);
                        if (oldformField == null) {// 判断是否已存在
                            fieldlist.add(formField);
                        }
                    }
                    tableFieldMap.put("fieldlist", fieldlist);
                    for (Map<String, Object> map : tableField) {
                        if (map.get("tableName").equals(y9Table.getTableName())) {
                            map.put("fieldlist", fieldlist);
                        }
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf itemViewConf = itemViewConfService.findById(id);
            resMap.put("itemViewConf", itemViewConf);
        }
        resMap.put("tableList", tableList);
        resMap.put("tablefield", tableField);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取自定义列视图
     *
     * @param id 主键id
     * @return
     */
    @GetMapping(value = "/newOrModify4Custom")
    public Y9Result<ItemViewConf> newOrModify4Custom(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf itemViewConf = itemViewConfService.findById(id);
            return Y9Result.success(itemViewConf, "获取成功");
        }
        return Y9Result.success(null, "获取成功");
    }

    /**
     * 删除
     *
     * @param ids 视图id
     */
    @PostMapping(value = "/removeView")
    public Y9Result<String> removeView(@RequestParam String[] ids) {
        itemViewConfService.removeItemViewConfs(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或者修改
     *
     * @param itemViewConf 视图信息
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(ItemViewConf itemViewConf) {
        itemViewConfService.saveOrUpdate(itemViewConf);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存排序
     *
     * @param idAndTabIndexs 视图id和排序索引
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        itemViewConfService.update4Order(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}
