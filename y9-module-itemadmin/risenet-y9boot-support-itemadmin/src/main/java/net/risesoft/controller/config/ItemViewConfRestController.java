package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.ItemConsts;
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
        // 提取所有字段
        List<Y9FormField> allFields = formList.stream()
            .flatMap(bind -> y9FormFieldService.listByFormId(bind.getFormId()).stream())
            .collect(Collectors.toList());
        // 提取唯一表信息
        Map<String,
            Y9Table> tableMap = allFields.stream()
                .map(field -> y9TableService.findById(field.getTableId()))
                .collect(Collectors.toMap(Y9Table::getTableName, table -> table, (existing, replacement) -> existing,
                    LinkedHashMap::new));
        // 按表名分组字段并去重
        Map<String,
            List<Y9FormField>> tableFieldMap = allFields.stream()
                .collect(Collectors.groupingBy(field -> y9TableService.findById(field.getTableId()).getTableName(),
                    Collectors.collectingAndThen(Collectors.toList(), this::removeDuplicateFields)));
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf itemViewConf = itemViewConfService.findById(id);
            resMap.put("itemViewConf", itemViewConf);
        }
        resMap.put("tableList", new ArrayList<>(tableMap.values()));
        resMap.put("tablefield", buildTableFieldResult(tableFieldMap));
        return Y9Result.success(resMap, "获取成功");
    }

    private List<Y9FormField> removeDuplicateFields(List<Y9FormField> fields) {
        return new ArrayList<>(fields.stream()
            .collect(Collectors.toMap(Y9FormField::getFieldName, field -> field, (existing, replacement) -> existing))
            .values());
    }

    private List<Map<String, Object>> buildTableFieldResult(Map<String, List<Y9FormField>> tableFieldMap) {
        return tableFieldMap.entrySet().stream().map(entry -> {
            Map<String, Object> map = new HashMap<>();
            map.put(ItemConsts.TABLENAME_KEY, entry.getKey());
            map.put(ItemConsts.FIELDLIST_KEY, entry.getValue());
            return map;
        }).collect(Collectors.toList());
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
