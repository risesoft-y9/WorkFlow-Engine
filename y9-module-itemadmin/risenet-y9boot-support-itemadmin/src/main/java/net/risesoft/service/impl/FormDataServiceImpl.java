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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.form.Y9FieldPerm;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9FormItemBind;
import net.risesoft.entity.form.Y9PreFormItemBind;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.enums.ItemTableTypeEnum;
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
import net.risesoft.service.ItemService;
import net.risesoft.service.ProcessParamService;
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

    private final ItemService itemService;

    private final Y9FormItemBindService y9FormItemBindService;

    private final Y9PreFormItemBindService y9PreFormItemBindService;

    private final Y9FormFieldService y9FormFieldService;

    private final Y9FormService y9FormService;

    private final Y9FormRepository y9FormRepository;

    private final RepositoryApi repositoryApi;

    private final Y9FieldPermRepository y9FieldPermRepository;

    private final PositionRoleApi positionRoleApi;

    private final Y9TableService y9TableService;

    private final ProcessParamService processParamService;

    public FormDataServiceImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate, ItemService itemService,
        Y9FormItemBindService y9FormItemBindService, Y9PreFormItemBindService y9PreFormItemBindService,
        Y9FormFieldService y9FormFieldService, Y9FormService y9FormService, Y9FormRepository y9FormRepository,
        RepositoryApi repositoryApi, Y9FieldPermRepository y9FieldPermRepository, PositionRoleApi positionRoleApi,
        Y9TableService y9TableService, ProcessParamService processParamService) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemService = itemService;
        this.y9FormItemBindService = y9FormItemBindService;
        this.y9PreFormItemBindService = y9PreFormItemBindService;
        this.y9FormFieldService = y9FormFieldService;
        this.y9FormService = y9FormService;
        this.y9FormRepository = y9FormRepository;
        this.repositoryApi = repositoryApi;
        this.y9FieldPermRepository = y9FieldPermRepository;
        this.positionRoleApi = positionRoleApi;
        this.y9TableService = y9TableService;
        this.processParamService = processParamService;
    }

    @Override
    @Transactional
    public Y9Result<Object> copy(String sourceProcessSerialNumber, String targetProcessSerialNumber) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(sourceProcessSerialNumber);
            String itemId = processParam.getItemId();
            Item item = itemService.findById(itemId);
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, item.getWorkflowGuid()).getData();
            // 流程上绑定的表单
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            List<String> allTableNameList = new ArrayList<>();
            formList.forEach(bind -> {
                // 表单中涉及的表，多个表单有可能会绑定同一张表
                y9FormRepository.findBindTableName(bind.getFormId()).forEach(tableName -> {
                    if (!allTableNameList.contains(tableName)) {
                        allTableNameList.add(tableName);
                    }
                });
            });
            // 复制数据
            allTableNameList.forEach(tableName -> {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType().equals(ItemTableTypeEnum.MAIN.getValue())) {
                    try {
                        Map<String, Object> map = jdbcTemplate.queryForMap(
                            "SELECT * FROM " + tableName.toUpperCase() + " WHERE GUID=?", sourceProcessSerialNumber);
                        StringBuilder columnSql = new StringBuilder();
                        map.entrySet().stream().filter(entry -> !"guid".equalsIgnoreCase(entry.getKey()))
                            .forEach(entry -> columnSql.append(",").append(entry.getKey()));
                        String sql = "INSERT INTO " + tableName + " (guid" + columnSql + ") " + "SELECT ?" + columnSql
                            + " FROM " + tableName + " WHERE guid = ?";
                        jdbcTemplate.update(sql, targetProcessSerialNumber, sourceProcessSerialNumber);
                    } catch (EmptyResultDataAccessException ex) {
                        LOGGER.error("表{}没有guid:{}的数据", tableName, sourceProcessSerialNumber);
                    }
                }
            });
            return Y9Result.successMsg("复制成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("复制失败");
    }

    @Override
    @Transactional
    public Y9Result<Object> delChildTableRow(String formId, String tableId, String guid) {
        return y9FormService.delChildTableRow(formId, tableId, guid);
    }

    @Override
    @Transactional
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
            Item item = itemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefineKey).getData();
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            formList.forEach(bind -> {
                String formId = bind.getFormId();
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                tableNameList.forEach(tableName -> {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
                    if (y9Table.getTableType().equals(ItemTableTypeEnum.MAIN.getValue())) {
                        List<Map<String, Object>> list = jdbcTemplate.queryForList(
                            "SELECT * FROM " + tableName.toUpperCase() + " WHERE GUID=?", processSerialNumber);
                        if (!list.isEmpty()) {
                            retMap.putAll(list.get(0));
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public Y9Result<Map<String, Object>> getData4TableAlias(String guid, String tableAlias) {
        Y9Table y9Table = y9TableService.findByTableAlias(tableAlias);
        if (null == y9Table) {
            LOGGER.error("表简称[" + tableAlias + "]对应的字段不存在");
            return Y9Result.failure("表简称[" + tableAlias + "]对应的字段不存在");
        }
        String selectSql = "SELECT * FROM " + y9Table.getTableName() + " WHERE GUID ='" + guid + "'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql);
        return Y9Result.success(!list.isEmpty() ? list.get(0) : null);
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
    public Map<String, Object> getFormData(String formId, String processSerialNumber) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map = y9FormService.getFormData(formId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
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
            Item item = itemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition = repositoryApi
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
                model.setQuerySign(formElement.getQuerySign());
                model.setTableName(formElement.getTableName());
                model.setId(formElement.getId());
                model.setFormId(formElement.getFormId());
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
            Item item = itemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition = repositoryApi
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
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put("value", mapFormJsonData.get(columnName));
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

    @Override
    @Transactional
    public Y9Result<String> updateFormData(String guid, String formData) {
        try {
            Map<String, Object> dataMap = Y9JsonUtil.readHashMap(formData);
            assert dataMap != null;
            List<Y9Table> tableList = new ArrayList<>();
            for (String key : dataMap.keySet()) {
                if (key.contains(".")) {
                    String[] aliasColumnNameType = key.split("\\.");
                    String alias = aliasColumnNameType[0];
                    Y9Table y9Table = y9TableService.findByTableAlias(alias);
                    if (null == y9Table) {
                        return Y9Result.failure("表简称对应的字段不存在：{}", alias);
                    }
                    if (!tableList.contains(y9Table)) {
                        tableList.add(y9Table);
                    }
                } else {
                    return Y9Result.failure("字段未包含表简称：{}", key);
                }
            }
            tableList.forEach(table -> {
                StringBuilder updateSql = new StringBuilder();
                StringBuilder sql = new StringBuilder();
                updateSql.append("UPDATE ").append(table.getTableName()).append(" ").append(table.getTableAlias())
                    .append("  SET ");
                for (String key : dataMap.keySet()) {
                    if (key.contains(table.getTableAlias() + ".")) {
                        if (sql.length() != 0) {
                            sql.append(",");
                        }
                        sql.append(key).append("='").append(dataMap.get(key)).append("'");
                    }
                }
                updateSql.append(sql).append(" WHERE guid='").append(guid).append("'");
                jdbcTemplate.execute(updateSql.toString());
            });
            return Y9Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("****************************[updateFormData]更新表单数据异常，表单数据：{}", formData);
        }
        return Y9Result.failure("发生异常");
    }

    @Override
    @Transactional
    public Y9Result<String> insertFormData(String tableName, String guid, String formData) {
        try {
            Map<String, Object> formDataMap = Y9JsonUtil.readHashMap(formData);
            assert formDataMap != null;
            Y9Table y9Table = y9TableService.findByTableName(tableName);
            if (null == y9Table) {
                return Y9Result.failure("表不存在：{}", tableName);
            }
            List<Map<String, Object>> list =
                jdbcTemplate.queryForList("SELECT * FROM " + tableName + " WHERE GUID ='" + guid + "'");
            if (!list.isEmpty()) {
                return Y9Result.failure("主键已存在：{}", guid);
            }
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, Object> entry : formDataMap.entrySet()) {
                String column = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    columns.append(column).append(",");
                    values.append("'").append(value).append("',");
                }
            }
            if (columns.length() > 0) {
                columns.deleteCharAt(columns.length() - 1);
            }
            if (values.length() > 0) {
                values.deleteCharAt(values.length() - 1);
            }
            String insertSql = String.format("INSERT INTO " + tableName + " (%s) VALUES (%s)", columns, values);
            jdbcTemplate.execute(insertSql);
            return Y9Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("****************************[insertFormData]插入表单数据异常：{}，表单数据：{}", e, formData);
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }
}
