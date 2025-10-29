package net.risesoft.service.form.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
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

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.ItemConsts;
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
import net.risesoft.service.config.Y9FormItemBindService;
import net.risesoft.service.config.Y9PreFormItemBindService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.form.FormDataService;
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

    private static final String FORM_ID_KEY = "form_Id";
    private static final String SELECT_KEY = "SELECT * FROM ";
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

    public FormDataServiceImpl(
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate,
        ItemService itemService,
        Y9FormItemBindService y9FormItemBindService,
        Y9PreFormItemBindService y9PreFormItemBindService,
        Y9FormFieldService y9FormFieldService,
        Y9FormService y9FormService,
        Y9FormRepository y9FormRepository,
        RepositoryApi repositoryApi,
        Y9FieldPermRepository y9FieldPermRepository,
        PositionRoleApi positionRoleApi,
        Y9TableService y9TableService,
        ProcessParamService processParamService) {
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
            // 获取流程绑定的所有表名
            List<String> allTableNameList = getBindTableNames(itemId, processDefinition);
            // 复制数据
            allTableNameList.forEach(
                tableName -> copyMainTableData(tableName, sourceProcessSerialNumber, targetProcessSerialNumber));
            return Y9Result.successMsg("复制成功");
        } catch (Exception e) {
            LOGGER.error("复制流程数据失败", e);
            return Y9Result.failure("复制失败");
        }
    }

    /**
     * 获取流程绑定的所有表名
     *
     * @param itemId 事项ID
     * @param processDefinition 流程定义
     * @return 表名列表
     */
    private List<String> getBindTableNames(String itemId, ProcessDefinitionModel processDefinition) {
        List<String> allTableNameList = new ArrayList<>();
        List<Y9FormItemBind> formList =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());

        formList.forEach(bind -> {
            // 表单中涉及的表，多个表单有可能会绑定同一张表
            y9FormRepository.findBindTableName(bind.getFormId()).forEach(tableName -> {
                if (!allTableNameList.contains(tableName)) {
                    allTableNameList.add(tableName);
                }
            });
        });

        return allTableNameList;
    }

    /**
     * 复制主表数据
     *
     * @param tableName 表名
     * @param sourceProcessSerialNumber 源流程序列号
     * @param targetProcessSerialNumber 目标流程序列号
     */
    @SuppressWarnings("java:S2077")
    private void copyMainTableData(String tableName, String sourceProcessSerialNumber,
        String targetProcessSerialNumber) {
        Y9Table y9Table = y9TableService.findByTableName(tableName);
        if (y9Table.getTableType().equals(ItemTableTypeEnum.MAIN)) {
            try {
                Map<String, Object> map = jdbcTemplate
                    .queryForMap(SELECT_KEY + tableName.toUpperCase() + " WHERE GUID=?", sourceProcessSerialNumber);
                StringBuilder columnSql = new StringBuilder();
                map.entrySet()
                    .stream()
                    .filter(entry -> !"guid".equalsIgnoreCase(entry.getKey()))
                    .forEach(entry -> columnSql.append(",").append(entry.getKey()));
                String sql = "INSERT INTO " + tableName + " (guid" + columnSql + ") " + "SELECT ?" + columnSql
                    + " FROM " + tableName + " WHERE guid = ?";
                jdbcTemplate.update(sql, targetProcessSerialNumber, sourceProcessSerialNumber);
            } catch (EmptyResultDataAccessException ex) {
                LOGGER.error("表{}没有guid:{}的数据", tableName, sourceProcessSerialNumber);
            }
        }
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
    @SuppressWarnings("java:S2077")
    public Map<String, Object> getData(String itemId, String processSerialNumber) {
        Map<String, Object> retMap = new HashMap<>(16);
        try {
            Item item = itemService.findById(itemId);
            if (item == null) {
                LOGGER.warn("未找到ID为{}的事项", itemId);
                return retMap;
            }
            String processDefineKey = item.getWorkflowGuid();
            Y9Result<ProcessDefinitionModel> processDefinitionResult =
                repositoryApi.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey);
            if (!processDefinitionResult.isSuccess() || processDefinitionResult.getData() == null) {
                LOGGER.warn("未找到事项{}对应的工作流定义", itemId);
                return retMap;
            }
            ProcessDefinitionModel processDefinition = processDefinitionResult.getData();
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            for (Y9FormItemBind bind : formList) {
                String formId = bind.getFormId();
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                for (String tableName : tableNameList) {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
                    if (y9Table != null && y9Table.getTableType().equals(ItemTableTypeEnum.MAIN)) {
                        String sql = SELECT_KEY + tableName.toUpperCase() + " WHERE GUID=?";
                        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, processSerialNumber);
                        if (!list.isEmpty()) {
                            retMap.putAll(list.get(0));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取表单数据失败，itemId: {}, processSerialNumber: {}", itemId, processSerialNumber, e);
        }
        return retMap;
    }

    @SuppressWarnings("java:S2077")
    @Override
    public Map<String, Map<String, Object>> getDataByProcessSerialNumbers(String itemId,
        List<String> processSerialNumbers) {
        Map<String, Map<String, Object>> retMap = new HashMap<>();
        try {
            Item item = itemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey)
                    .getData();
            List<Y9FormItemBind> formList =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            List<Map<String, Object>> mapList = new ArrayList<>();
            formList.forEach(bind -> {
                String formId = bind.getFormId();
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                tableNameList.forEach(tableName -> {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
                    if (y9Table.getTableType().equals(ItemTableTypeEnum.MAIN)) {
                        String placeholders = String.join(",", Collections.nCopies(processSerialNumbers.size(), "?"));
                        String sql = SELECT_KEY + tableName.toUpperCase() + " WHERE GUID IN (" + placeholders + ")";
                        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, processSerialNumbers.toArray());
                        mapList.addAll(list);
                    }
                });
            });
            retMap = mergeMapsByKeyValueNonNullPriority(mapList, "guid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    /**
     * 合并具有相同key的Map，对于相同字段，优先保留非null值
     *
     * @param listMap 要合并的Map列表
     * @param key 用于判断相等的键名
     * @return 合并后的Map
     */
    public Map<String, Map<String, Object>> mergeMapsByKeyValueNonNullPriority(List<Map<String, Object>> listMap,
        String key) {
        Map<String, Map<String, Object>> mergedMap = new HashMap<>();
        for (Map<String, Object> map : listMap) {
            String keyValue = findValueIgnoreCase(map, key);
            if (keyValue != null) {
                mergedMap.merge(keyValue, new HashMap<>(map),
                    (existingMap, newMap) -> mergeTwoMaps(existingMap, newMap));
            }
        }
        return mergedMap;
    }

    private Map<String, Object> mergeTwoMaps(Map<String, Object> existingMap, Map<String, Object> newMap) {
        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            String entryKey = entry.getKey();
            Object entryValue = entry.getValue();
            // 如果新值不为null，或者原值为null，则更新
            if (entryValue != null || existingMap.get(entryKey) == null) {
                existingMap.put(entryKey, entryValue);
            }
        }
        return existingMap;
    }

    private String findValueIgnoreCase(Map<String, Object> map, String key) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue() != null ? entry.getValue().toString() : null;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("java:S2077")
    public Y9Result<Map<String, Object>> getData4TableAlias(String guid, String tableAlias) {
        Y9Table y9Table = y9TableService.findByTableAlias(tableAlias);
        if (null == y9Table) {
            LOGGER.error("表简称[{}]对应的字段不存在", tableAlias);
            return Y9Result.failure("表简称[" + tableAlias + "]对应的字段不存在");
        }
        String selectSql = SELECT_KEY + y9Table.getTableName() + " WHERE GUID = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql, guid);
        return Y9Result.success(!list.isEmpty() ? list.get(0) : null);
    }

    @Override
    public FieldPermModel getFieldPerm(String formId, String fieldName, String taskDefKey, String processDefinitionId) {
        // 写权限
        FieldPermModel model = new FieldPermModel();
        model.setFieldName(fieldName);
        model.setWritePerm(false);
        Y9FieldPerm y9FieldPerm =
            y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm != null) {
            model = getFieldPerm(y9FieldPerm);
        } else {
            return null;
        }
        return model;
    }

    /**
     * 解析权限
     *
     * @param y9FieldPerm
     * @return
     */
    public FieldPermModel getFieldPerm(Y9FieldPerm y9FieldPerm) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9FlowableHolder.getOrgUnitId();
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
            assert y9Form != null;
            formJson = y9Form.getFormJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formJson;
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S2077")
    public Y9Result<String> insertFormData(String tableName, String guid, String formData) {
        try {
            // 验证输入参数
            if (StringUtils.isBlank(tableName) || StringUtils.isBlank(guid) || StringUtils.isBlank(formData)) {
                return Y9Result.failure("参数不能为空");
            }
            Map<String, Object> formDataMap = Y9JsonUtil.readHashMap(formData);
            if (formDataMap == null) {
                return Y9Result.failure("表单数据格式错误");
            }
            Y9Table y9Table = y9TableService.findByTableName(tableName);
            if (null == y9Table) {
                return Y9Result.failure("表不存在：{}", tableName);
            }
            // 检查主键是否已存在
            List<Map<String, Object>> list =
                jdbcTemplate.queryForList(SELECT_KEY + tableName + " WHERE GUID = ?", guid);
            if (!list.isEmpty()) {
                return Y9Result.failure("主键已存在：{}", guid);
            }
            // 构建参数化SQL语句
            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<String> placeholders = new ArrayList<>();
            for (Map.Entry<String, Object> entry : formDataMap.entrySet()) {
                String column = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    columns.add(column);
                    values.add(value);
                    placeholders.add("?");
                }
            }
            if (columns.isEmpty()) {
                return Y9Result.failure("没有有效的数据字段");
            }
            // 添加GUID字段
            columns.add(0, "guid");
            values.add(0, guid);
            placeholders.add(0, "?");

            String columnStr = String.join(",", columns);
            String placeholderStr = String.join(",", placeholders);
            String insertSql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnStr, placeholderStr);

            jdbcTemplate.update(insertSql, values.toArray());
            return Y9Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("插入表单数据异常，表名：{}，GUID：{}，表单数据：{}", tableName, guid, formData, e);
            return Y9Result.failure("数据插入失败：" + e.getMessage());
        }
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
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey)
                    .getData();
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
                model.setContentUsedFor(formElement.getContentUsedFor());
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
    public String saveAFormData(String itemId, String formData, String formId) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formData, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", FORM_ID_KEY);
            map.put(ItemConsts.VALUE_KEY, formId);
            listMap.add(map);
            assert mapFormJsonData != null;
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put(ItemConsts.VALUE_KEY, value);
                listMap.add(map);
            }
            formData = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveFormData(formData);// 保存前置表单数据
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService savePreFormData前置表单 error0");
            }

            // 获取事项绑定主表信息
            Item item = itemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey)
                    .getData();
            List<Y9FormItemBind> list =
                y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            String bindFormId = "";
            for (Y9FormItemBind form : list) {
                bindFormId = form.getFormId();
            }
            String processSerialNumber = Y9IdGenerator.genId(IdType.SNOWFLAKE);
            if (!bindFormId.isEmpty()) {
                List<Map<String, Object>> list1 = Y9JsonUtil.readValue(formData, List.class);
                for (Map<String, Object> map1 : list1) {
                    if (map1.get("name").equals(FORM_ID_KEY)) {// 重设表单id
                        map1.put(ItemConsts.VALUE_KEY, bindFormId);
                    } else if (map1.get("name").equals("guid")) {// 重设主键id
                        map1.put(ItemConsts.VALUE_KEY, processSerialNumber);
                    } else if (map1.get("name").equals("GUID")) {// 重设主键id
                        map1.put(ItemConsts.VALUE_KEY, processSerialNumber);
                    }
                }
                formData = Y9JsonUtil.writeValueAsString(list1);
                Y9Result<Object> y9Result2 = y9FormService.saveFormData(formData);// 保存主表信息
                if (!y9Result2.isSuccess()) {
                    throw new Exception("FormDataService savePreFormData主表 error0");
                }
                return processSerialNumber;// 返回主表主键id
            }
        } catch (Exception e) {
            LOGGER.error("****************************formData:" + formData);
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
    public void saveChildTableData(String formId, String formData) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formData, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", FORM_ID_KEY);
            map.put(ItemConsts.VALUE_KEY, formId);
            listMap.add(map);
            assert mapFormJsonData != null;
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put(ItemConsts.VALUE_KEY, value);
                listMap.add(map);
            }
            formData = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveChildTableData(formId, formData);
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService saveChildTableData error0");
            }
        } catch (Exception e) {
            LOGGER.error("****************************formData:{}", formData);
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
    public void saveFormData(String formData, String formId) throws Exception {
        try {
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formData, Map.class);
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", FORM_ID_KEY);
            map.put(ItemConsts.VALUE_KEY, formId);
            listMap.add(map);
            for (String columnName : mapFormJsonData.keySet()) {
                map = new HashMap<>(16);
                map.put("name", columnName);
                map.put(ItemConsts.VALUE_KEY, mapFormJsonData.get(columnName));
                listMap.add(map);
            }
            formData = Y9JsonUtil.writeValueAsString(listMap);
            Y9Result<Object> y9Result = y9FormService.saveFormData(formData);
            if (!y9Result.isSuccess()) {
                throw new Exception("FormDataService saveFormData error0");
            }
        } catch (Exception e) {
            LOGGER.error("****************************formData:{}", formData);
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
            if (dataMap == null) {
                return Y9Result.failure("表单数据格式错误");
            }
            // 验证并获取表信息
            List<Y9Table> tableList = validateAndExtractTables(dataMap);
            if (tableList == null) {
                return Y9Result.failure("字段验证失败");
            }
            // 更新各表数据
            for (Y9Table table : tableList) {
                Y9Result<String> updateResult = updateTableData(table, guid, dataMap);
                if (!updateResult.isSuccess()) {
                    return updateResult;
                }
            }
            return Y9Result.success("操作成功");
        } catch (Exception e) {
            LOGGER.error("[updateFormData]更新表单数据异常，表单数据：{}", formData, e);
            return Y9Result.failure("更新数据异常：" + e.getMessage());
        }
    }

    /**
     * 验证字段并提取涉及的表
     */
    private List<Y9Table> validateAndExtractTables(Map<String, Object> dataMap) {
        List<Y9Table> tableList = new ArrayList<>();
        for (String key : dataMap.keySet()) {
            if (!key.contains(".")) {
                LOGGER.warn("字段未包含表简称：{}", key);
                return null;
            }
            String[] aliasColumnNameType = key.split("\\.");
            String alias = aliasColumnNameType[0];
            Y9Table y9Table = y9TableService.findByTableAlias(alias);
            if (null == y9Table) {
                LOGGER.warn("表简称对应的表不存在：{}", alias);
                return null;
            }
            if (!tableList.contains(y9Table)) {
                tableList.add(y9Table);
            }
        }
        return tableList;
    }

    /**
     * 更新单个表的数据
     */
    private Y9Result<String> updateTableData(Y9Table table, String guid, Map<String, Object> dataMap) {
        try {
            StringBuilder updateSql = new StringBuilder();
            List<Object> parameters = new ArrayList<>();
            updateSql.append("UPDATE ").append(table.getTableName()).append(" SET ");
            StringBuilder setClause = new StringBuilder();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(table.getTableAlias() + ".")) {
                    if (setClause.length() > 0) {
                        setClause.append(", ");
                    }
                    String columnName = key.substring(key.indexOf(".") + 1);
                    setClause.append(columnName).append(" = ?");
                    parameters.add(entry.getValue());
                }
            }
            updateSql.append(setClause).append(" WHERE guid = ?");
            parameters.add(guid);
            jdbcTemplate.update(updateSql.toString(), parameters.toArray());
            return Y9Result.success("更新成功");
        } catch (Exception e) {
            LOGGER.error("更新表{}数据失败", table.getTableName(), e);
            return Y9Result.failure("更新表" + table.getTableName() + "数据失败：" + e.getMessage());
        }
    }
}
