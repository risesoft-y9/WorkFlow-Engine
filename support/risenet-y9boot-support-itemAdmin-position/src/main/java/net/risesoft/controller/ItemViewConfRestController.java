package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemViewConf;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemViewConfService;
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
@RequestMapping("/vue/itemViewConf")
public class ItemViewConfRestController {

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private ItemViewConfService itemViewConfService;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private Y9FormFieldService y9FormFieldService;

    @Autowired
    private Y9TableService y9TableService;

    /**
     * 保存或者修改
     *
     * @param itemViewConf 视图信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copyView", method = RequestMethod.POST, produces = "application/json")
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
    @ResponseBody
    @RequestMapping(value = "/findByItemId", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConf>> findByItemId(@RequestParam(required = true) String itemId, @RequestParam(required = true) String viewType) {
        List<ItemViewConf> list = itemViewConfService.findByItemIdAndViewType(itemId, viewType);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 根据绑定的表名称获取绑定的字段
     *
     * @param tableName 表名
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getColumns", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormField>> getColumns(@RequestParam(required = true) String tableName, @RequestParam(required = true) String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Y9FormField> list = new ArrayList<Y9FormField>();
        List<String> fieldNameList = new ArrayList<String>();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey);
        List<Y9FormItemBind> formList = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        for (Y9FormItemBind bind : formList) {
            List<Y9FormField> formFieldList = y9FormFieldService.findByTableNameAndFormId(tableName, bind.getFormId());
            for (Y9FormField formField : formFieldList) {
                if (!fieldNameList.contains(formField.getFieldName()) && !formField.getFieldName().equalsIgnoreCase("guid") && !formField.getFieldName().equalsIgnoreCase("processInstanceId")) {
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
     * @param viewType 视图类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/newOrModify", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> newOrModify(@RequestParam(required = false) String id, @RequestParam(required = true) String itemId) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition = repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey);
        List<Y9FormItemBind> formList = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
        List<String> tableNameList = new ArrayList<>();
        List<Y9Table> tableList = new ArrayList<>();
        List<Map<String, Object>> tablefield = new ArrayList<Map<String, Object>>();
        for (Y9FormItemBind bind : formList) {
            String formId = bind.getFormId();
            List<Y9FormField> formFieldList = y9FormFieldService.findByFormId(formId);
            for (Y9FormField formField : formFieldList) {
                if (!tableNameList.contains(formField.getTableName())) {
                    Y9Table y9Table = y9TableService.findById(formField.getTableId());
                    tableNameList.add(formField.getTableName());
                    tableList.add(y9Table);
                    List<Y9FormField> fieldlist = new ArrayList<Y9FormField>();
                    for (Y9FormField formField1 : formFieldList) {
                        if (y9Table.getTableName().equals(formField1.getTableName())) {
                            fieldlist.add(formField1);
                        }
                    }
                    Map<String, Object> tableFieldMap = new HashMap<String, Object>();
                    tableFieldMap.put("tableName", y9Table.getTableName());
                    tableFieldMap.put("fieldlist", fieldlist);
                    tablefield.add(tableFieldMap);
                }
            }
        }
        if (StringUtils.isNotBlank(id)) {
            ItemViewConf itemViewConf = itemViewConfService.findById(id);
            resMap.put("itemViewConf", itemViewConf);
        }
        resMap.put("tableList", tableList);
        resMap.put("tablefield", tablefield);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取自定义列视图
     *
     * @param id 主键id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/newOrModify4Custom", method = RequestMethod.GET, produces = "application/json")
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
     * @param ids
     */
    @ResponseBody
    @RequestMapping(value = "/removeView", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeView(@RequestParam(required = true) String[] ids) {
        itemViewConfService.removeItemViewConfs(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存排序
     *
     * @param idAndTabIndexs
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(@RequestParam(required = true) String[] idAndTabIndexs) {
        itemViewConfService.update4Order(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存或者修改
     *
     * @param itemViewConf 视图信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(ItemViewConf itemViewConf) {
        itemViewConfService.saveOrUpdate(itemViewConf);
        return Y9Result.successMsg("保存成功");
    }
}
