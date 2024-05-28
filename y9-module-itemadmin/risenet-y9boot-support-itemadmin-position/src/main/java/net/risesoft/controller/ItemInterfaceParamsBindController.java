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
import net.risesoft.entity.ItemInterfaceParamsBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemInterfaceParamsBindRepository;
import net.risesoft.service.ItemInterfaceParamsBindService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@RestController
@RequestMapping(value = "/vue/interfaceParamsBind")
public class ItemInterfaceParamsBindController {

    @Autowired
    private ItemInterfaceParamsBindService itemInterfaceParamsBindService;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private RepositoryApi repositoryApi;

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private Y9FormFieldService y9FormFieldService;

    @Autowired
    private Y9TableService y9TableService;

    @Autowired
    private ItemInterfaceParamsBindRepository itemInterfaceParamsBindRepository;

    /**
     * 获取绑定信息
     *
     * @param id 绑定id
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBindInfo", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBindInfo(@RequestParam(required = false) String id, @RequestParam(required = true) String itemId) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        SpmApproveItem item = spmApproveItemService.findById(itemId);
        String processDefineKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition = repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey);
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
            ItemInterfaceParamsBind info = itemInterfaceParamsBindRepository.findById(id).orElse(null);
            resMap.put("info", info);
        }
        resMap.put("tableList", tableList);
        resMap.put("tablefield", tablefield);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取绑定列表
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param type 参数类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemInterfaceParamsBind>> getBindList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String interfaceId, @RequestParam(required = true) String type) {
        List<ItemInterfaceParamsBind> list = itemInterfaceParamsBindService.getBindList(itemId, interfaceId, type);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param ids 绑定ids
     * @return
     */
    @RequestMapping(value = "/removeBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeBind(@RequestParam(required = true) String id) {
        itemInterfaceParamsBindService.removeBind(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存绑定
     *
     * @param interfaceId 接口id
     * @param itemId 事项id
     * @param parameterName 参数名称
     * @param parameterType 参数类型
     * @param bindType 绑定类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBind(ItemInterfaceParamsBind info) {
        itemInterfaceParamsBindService.saveBind(info);
        return Y9Result.successMsg("保存成功");
    }

}
