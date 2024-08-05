package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9PreFormItemBind;
import net.risesoft.entity.form.Y9FieldPerm;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FieldPermRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.service.FormDataService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.config.Y9PreFormItemBindService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class FormDataServiceImpl implements FormDataService {

    private final JdbcTemplate jdbcTemplate;

    private final SpmApproveItemService spmApproveItemService;

    private final Y9FormItemBindService y9FormItemBindService;

    private final Y9PreFormItemBindService y9PreFormItemBindService;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9FormService y9FormService;

    private final Y9FormRepository y9FormRepository;

    private final RepositoryApi repositoryManager;

    private final Y9FieldPermRepository y9FieldPermRepository;

    private final PositionRoleApi positionRoleApi;

    private final Y9TableService y9TableService;

    public FormDataServiceImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate,
        SpmApproveItemService spmApproveItemService, Y9FormItemBindService y9FormItemBindService,
        Y9PreFormItemBindService y9PreFormItemBindService, Y9FormFieldService y9FormFieldService,
        Y9FormService y9FormService, Y9FormRepository y9FormRepository, RepositoryApi repositoryManager,
        Y9FieldPermRepository y9FieldPermRepository, PositionRoleApi positionRoleApi, Y9TableService y9TableService) {
        this.jdbcTemplate = jdbcTemplate;
        this.spmApproveItemService = spmApproveItemService;
        this.y9FormItemBindService = y9FormItemBindService;
        this.y9PreFormItemBindService = y9PreFormItemBindService;
        this.y9FormFieldService = y9FormFieldService;
        this.y9FormService = y9FormService;
        this.y9FormRepository = y9FormRepository;
        this.repositoryManager = repositoryManager;
        this.y9FieldPermRepository = y9FieldPermRepository;
        this.positionRoleApi = positionRoleApi;
        this.y9TableService = y9TableService;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<Object> delChildTableRow(String formId, String tableId, String guid) {
        return y9FormService.delChildTableRow(formId, tableId, guid);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<Object> delPreFormData(String formId, String guid) {
        return y9FormService.delPreFormData(formId, guid);
    }

    @Override
    public Map<String, Object> getBindPreFormByItemId(String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("formId", "");
        map.put("formName", "");
        try {
            Y9PreFormItemBind item = y9PreFormItemBindService.findByItemId(itemId);
            if (item != null) {
                map.put("formId", item.getFormId());
                map.put("formName", item.getFormName());
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber) {
        Map<String, Object> retMap = new HashMap<>(16);
        try {
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            List<Map<String, Object>> list = null;
            for (Y9FormItemBind bind : formList) {
                String formId = bind.getFormId();
                // 获取表单绑定的表,可能多个
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                for (String tableName : tableNameList) {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
                    // 只获取主表
                    if (y9Table.getTableType() == 1) {
                        list = jdbcTemplate.queryForList("SELECT * FROM " + tableName.toUpperCase() + " WHERE GUID=?",
                            processSerialNumber);
                        if (!list.isEmpty()) {
                            retMap.putAll(list.get(0));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public FieldPermModel getFieldPerm(String formId, String fieldName, String taskDefKey, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 写权限
        FieldPermModel model = new FieldPermModel();
        model.setFieldName(fieldName);
        model.setWritePerm(false);
        Y9FieldPerm y9FieldPerm =
            y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm != null) {
            model = getFieldPerm(y9FieldPerm);
        } else {
            model = null;
            return model;
        }
        return model;
    }

    /**
     * 解析权限 Description:
     *
     * @param y9FieldPerm
     * @return
     */
    public FieldPermModel getFieldPerm(Y9FieldPerm y9FieldPerm) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getOrgUnitId();
        // 绑定了角色
        FieldPermModel model = new FieldPermModel();
        model.setFieldName(y9FieldPerm.getFieldName());
        if (StringUtils.isNotBlank(y9FieldPerm.getWriteRoleId())) {
            model.setWritePerm(false);
            String roleId = y9FieldPerm.getWriteRoleId();
            String[] roleIds = roleId.split(",");
            for (String id : roleIds) {
                boolean b = positionRoleApi.hasRole(tenantId, id, userId).getData();
                if (b) {
                    model.setWritePerm(true);
                    break;
                }
            }
        } else {// 未绑定角色，默认该节点所有人都有写权限
            // 写权限
            model.setWritePerm(true);
        }
        return model;
    }

    @Override
    public String getFormJson(String formId) {
        String formJson = "";
        try {
            Y9Form y9Form = y9FormRepository.findById(formId).orElse(null);
            formJson = y9Form.getFormJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formJson;
    }

    @Override
    public Map<String, Object> getFromData(String formId, String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map = y9FormService.getFormData(formId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<FieldPermModel> listAllFieldPerm(String formId, String taskDefKey, String processDefinitionId) {
        List<String> list = y9FieldPermRepository.findByFormId(formId);
        List<FieldPermModel> listMap = new ArrayList<>();
        for (String fieldName : list) {
            FieldPermModel model = this.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
            if (model != null) {
                listMap.add(model);
            }
        }
        return listMap;
    }

    @Override
    public List<Map<String, Object>> listChildFormData(String formId, String parentProcessSerialNumber) {
        return y9FormService.listChildFormData(formId, parentProcessSerialNumber);
    }

    @Override
    public List<Map<String, Object>> listChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception {
        return y9FormService.listChildTableData(formId, tableId, processSerialNumber);
    }

    @Override
    public List<Y9FormFieldModel> listFormFieldByItemId(String itemId) {
        List<Y9FormFieldModel> list = new ArrayList<>();
        try {
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition = repositoryManager
                .getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey).getData();
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            for (Y9FormItemBind form : formList) {
                List<Y9FormField> formElementList = y9FormFieldService.listByFormId(form.getFormId());
                for (Y9FormField formElement : formElementList) {
                    if (StringUtils.isNotBlank(formElement.getQuerySign()) && formElement.getQuerySign().equals("1")) {
                        Y9FormFieldModel model = new Y9FormFieldModel();
                        Y9BeanUtil.copyProperties(formElement, model);
                        if (!list.contains(model)) {
                            list.add(model);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<FormFieldDefineModel> listFormFieldDefineByFormId(String formId) {
        List<FormFieldDefineModel> list = new ArrayList<>();
        try {
            List<Y9FormField> formElementList = y9FormFieldService.listByFormId(formId);
            for (Y9FormField formElement : formElementList) {
                FormFieldDefineModel model = new FormFieldDefineModel();
                model.setFormCtrltype(formElement.getFieldType());
                model.setFormCtrlName(formElement.getFieldName());
                model.setColumnName(formElement.getFieldName());
                model.setDisChinaName(formElement.getFieldCnName());
                if (!list.contains(model)) {
                    list.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> listPreFormDataByFormId(String formId) {
        return y9FormService.listFormData(formId);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public String saveAFormData(String itemId, String formdata, String formId) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formdata, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", "form_Id");
            map.put("value", formId);
            listMap.add(map);
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put("value", value);
                listMap.add(map);
            }
            formdata = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveFormData(formdata);// 保存前置表单数据
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService savePreFormData前置表单 error0");
            }

            // 获取事项绑定主表信息
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition = repositoryManager
                .getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey).getData();
            List<Y9FormItemBind> list =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            String bindFormId = "";
            for (Y9FormItemBind form : list) {
                bindFormId = form.getFormId();
            }
            String processSerialNumber = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            if (!bindFormId.equals("")) {
                List<Map<String, Object>> list1 = Y9JsonUtil.readValue(formdata, List.class);
                for (Map<String, Object> map1 : list1) {
                    if (map1.get("name").equals("form_Id")) {// 重设表单id
                        map1.put("value", bindFormId);
                    } else if (map1.get("name").equals("guid")) {// 重设主键id
                        map1.put("value", processSerialNumber);
                    } else if (map1.get("name").equals("GUID")) {// 重设主键id
                        map1.put("value", processSerialNumber);
                    }
                }
                formdata = Y9JsonUtil.writeValueAsString(list1);
                Y9Result<Object> y9Result2 = y9FormService.saveFormData(formdata);// 保存主表信息
                if (!y9Result2.isSuccess()) {
                    throw new Exception("FormDataService savePreFormData主表 error0");
                }
                return processSerialNumber;// 返回主表主键id
            }
        } catch (Exception e) {
            LOGGER.error("****************************formdata:" + formdata);
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            LOGGER.error(msg);
            throw new Exception("FormDataService savePreFormData error1");
        }
        return "";
    }

    @Override
    @Transactional
    public void saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData)
        throws Exception {
        try {
            Y9Result<Object> map = y9FormService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);
            if (!map.isSuccess()) {
                throw new Exception("FormDataService saveChildTableData error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("FormDataService saveChildTableData error");
        }
    }

    @Override
    @Transactional
    public void saveChildTableData(String formId, String formdata) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formdata, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", "form_Id");
            map.put("value", formId);
            listMap.add(map);
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put("value", value);
                listMap.add(map);
            }
            formdata = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveChildTableData(formId, formdata);
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService saveChildTableData error0");
            }
        } catch (Exception e) {
            LOGGER.error("****************************formdata:" + formdata);
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            LOGGER.error(msg);
            throw new Exception("FormDataService saveChildTableData error1");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void saveFormData(String formdata, String formId) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formdata, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", "form_Id");
            map.put("value", formId);
            listMap.add(map);
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put("value", value);
                listMap.add(map);
            }
            formdata = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveFormData(formdata);
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService saveFormData error0");
            }
        } catch (Exception e) {
            LOGGER.error("****************************formdata:" + formdata);
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            LOGGER.error(msg);
            throw new Exception("FormDataService saveFormData error1");
        }
    }
}
