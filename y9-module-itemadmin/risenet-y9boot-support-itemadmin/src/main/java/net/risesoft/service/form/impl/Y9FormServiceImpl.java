package net.risesoft.service.form.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.DialectEnum;
import net.risesoft.enums.ItemTableTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.jpa.ItemRepository;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormServiceImpl implements Y9FormService {

    private final JdbcTemplate jdbcTemplate4Tenant;
    private final Y9FormRepository y9FormRepository;
    private final Y9TableService y9TableService;
    private final Y9FormFieldRepository y9FormFieldRepository;
    private final Y9TableFieldRepository y9TableFieldRepository;
    private final ItemRepository itemRepository;

    public Y9FormServiceImpl(
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        Y9FormRepository y9FormRepository,
        Y9TableService y9TableService,
        Y9FormFieldRepository y9FormFieldRepository,
        Y9TableFieldRepository y9TableFieldRepository,
        ItemRepository itemRepository) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9FormRepository = y9FormRepository;
        this.y9TableService = y9TableService;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Y9Result<Object> delChildTableRow(String formId, String tableId, String guid) {
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            StringBuilder sqlStr = new StringBuilder();
            if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("delete FROM \"" + tableName + "\" where guid = '" + guid + "'");
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("delete FROM " + tableName + " where guid = '" + guid + "'");
            }
            jdbcTemplate4Tenant.execute(sqlStr.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败");
        }
        return Y9Result.successMsg("删除成功");
    }

    @Override
    @Transactional
    public Y9Result<Object> delPreFormData(String formId, String guid) {
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> list = y9FormRepository.findBindTableName(formId);
            for (String tableName : list) {
                StringBuilder sqlStr = new StringBuilder();
                if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder("delete FROM \"" + tableName + "\" where guid = '" + guid + "'");
                } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder("delete FROM " + tableName + " where guid = '" + guid + "'");
                }
                jdbcTemplate4Tenant.execute(sqlStr.toString());
            }
            return Y9Result.success(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> delete(String ids) {
        try {
            String[] id = ids.split(",");
            for (String idTemp : id) {
                y9FormRepository.deleteById(idTemp);
                y9FormFieldRepository.deleteByFormId(idTemp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败");
        }
        return Y9Result.successMsg("删除成功");
    }

    @Override
    @Transactional
    @SuppressWarnings("java:S2077")
    public boolean deleteByGuid(String y9TableId, String guid) {
        Y9Table y9Table = y9TableService.findById(y9TableId);
        String tableName = y9Table.getTableName();
        try {
            String sql = "DELETE FROM " + tableName + " WHERE GUID=?";
            jdbcTemplate4Tenant.update(sql, guid);
            return true;
        } catch (Exception e) {
            LOGGER.error("删除业务表[{}]数据失败,guid:{}", tableName, guid, e);
        }
        return false;
    }

    @Override
    public Y9Form findById(String id) {
        return y9FormRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> getData(String guid, String tableName) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(ItemConsts.EDITTYPE_KEY, "0");
        try {
            if (StringUtils.isBlank(guid)) {
                return map;
            }
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            String dataSql = buildSelectSql(dialect, tableName) + " where guid=?";
            List<Map<String, Object>> dataMap = jdbcTemplate4Tenant.queryForList(dataSql, guid);
            if (dataMap.isEmpty()) {
                map.put(ItemConsts.EDITTYPE_KEY, "0");
            } else {
                map.put(ItemConsts.EDITTYPE_KEY, "1");
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取表单数据失败,tableName:{},guid:{}", tableName, guid, e);
        }
        return map;
    }

    @Override
    public Map<String, Object> getFormData(String formId, String guid) {
        Map<String, Object> result = new HashMap<>(16);
        Map<String, Object> formData = new HashMap<>(16);
        try {
            if (StringUtils.isBlank(formId) || StringUtils.isBlank(guid)) {
                result.put("formData", formData);
                result.put(UtilConsts.SUCCESS, true);
                return result;
            }
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 只处理主表数据
                if (y9Table != null && y9Table.getTableType() == ItemTableTypeEnum.MAIN) {
                    fetchMainTableData(dialect, tableName, formId, guid, formData);
                }
            }
            result.put("formData", formData);
            result.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("获取表单数据失败, formId: {}, guid: {}", formId, guid, e);
            result.put("formData", formData);
            result.put(UtilConsts.SUCCESS, false);
        }

        return result;
    }

    /**
     * 获取主表数据
     */
    private void fetchMainTableData(String dialect, String tableName, String formId, String guid,
        Map<String, Object> formData) {
        try {
            String selectSql = buildSelectSql(dialect, tableName) + " WHERE guid = ?";
            List<Map<String, Object>> dataMap = jdbcTemplate4Tenant.queryForList(selectSql, guid);
            if (!dataMap.isEmpty()) {
                List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                populateFormData(dataMap.get(0), elementList, formData);
            }
        } catch (Exception e) {
            LOGGER.error("查询主表数据失败, tableName: {}, guid: {}", tableName, guid, e);
        }
    }

    /**
     * 填充表单数据
     */
    private void populateFormData(Map<String, Object> rowData, List<Y9FormField> elementList,
        Map<String, Object> formData) {
        for (Y9FormField element : elementList) {
            String fieldName = element.getFieldName();
            Object value = rowData.get(fieldName);
            formData.put(fieldName, value != null ? value.toString() : "");
        }
    }

    @Override
    public Map<String, Object> getFormData4Var(String formId, String guid) {
        Map<String, Object> result = new HashMap<>(16);
        try {
            if (StringUtils.isBlank(formId) || StringUtils.isBlank(guid)) {
                return result;
            }
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 只处理主表数据
                if (y9Table != null && y9Table.getTableType() == ItemTableTypeEnum.MAIN) {
                    fetchVariableData(dialect, tableName, guid, y9Table.getId(), result);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取表单变量数据失败, formId: {}, guid: {}", formId, guid, e);
        }
        return result;
    }

    /**
     * 获取变量数据
     */
    private void fetchVariableData(String dialect, String tableName, String guid, String tableId,
        Map<String, Object> result) {
        try {
            String selectSql = buildSelectSql(dialect, tableName) + " WHERE guid = ?";
            List<Map<String, Object>> dataMap = jdbcTemplate4Tenant.queryForList(selectSql, guid);
            if (!dataMap.isEmpty()) {
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
                for (Y9TableField tableField : tableFieldList) {
                    // 只处理标记为变量的字段
                    if (tableField.getIsVar() != null && tableField.getIsVar() == 1) {
                        String fieldName = tableField.getFieldName();
                        Object value = dataMap.get(0).get(fieldName);
                        result.put(fieldName, value != null ? value.toString() : "");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("查询变量数据失败, tableName: {}, guid: {}", tableName, guid, e);
        }
    }

    @Override
    public String getFormField(String id) {
        List<Y9FormField> list = y9FormFieldRepository.findByFormId(id);
        return Y9JsonUtil.writeValueAsString(list);
    }

    @Override
    public List<Y9Form> listAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.UPDATETIME_KEY);
        return y9FormRepository.findAll(sort);
    }

    @Override
    public List<Map<String, Object>> listChildFormData(String formId, String parentProcessSerialNumber) {
        List<Map<String, Object>> dataMap = new ArrayList<>();
        String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
        List<String> tableNameList = y9FormRepository.findBindTableName(formId);
        for (String tableName : tableNameList) {
            Y9Table y9Table = y9TableService.findByTableName(tableName);
            List<Y9FormField> tableFieldList = y9FormFieldRepository.findByTableName(tableName);
            String userIdSql = "";
            for (Y9FormField formField : tableFieldList) {// 表单如果绑定了y9_userId，则加上y9_userId为查询条件
                if (formField.getFieldName().equals("y9_userId") || formField.getFieldName().equals("Y9_USERID")) {
                    userIdSql = " and y9_userId = '" + Y9LoginUserHolder.getOrgUnitId() + "'";
                    break;
                }
            }
            if (y9Table.getTableType() == ItemTableTypeEnum.SUB) {
                StringBuilder sqlStr = new StringBuilder();
                if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder(
                        "SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?" + userIdSql);
                } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder(
                        "SELECT * FROM " + tableName + " where parentProcessSerialNumber =?" + userIdSql);
                }
                dataMap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), parentProcessSerialNumber);
                return dataMap;
            }
        }
        return dataMap;
    }

    @Override
    public List<Map<String, Object>> listChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception {
        List<Map<String, Object>> dataMap;
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            StringBuilder sqlStr = new StringBuilder();
            if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?");
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where parentProcessSerialNumber =?");
            }
            dataMap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), processSerialNumber);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Y9FormServiceImpl getChildTableData error");
        }
    }

    @Override
    public List<Map<String, Object>> listFormData(String formId) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            if (StringUtils.isBlank(formId)) {
                return result;
            }
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 只处理主表数据
                if (y9Table != null && y9Table.getTableType() == ItemTableTypeEnum.MAIN) {
                    fetchAllMainTableData(dialect, tableName, formId, result);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取表单数据列表失败, formId: {}", formId, e);
        }

        return result;
    }

    /**
     * 获取主表所有数据
     */
    private void fetchAllMainTableData(String dialect, String tableName, String formId,
        List<Map<String, Object>> result) {
        try {
            String selectSql = buildSelectSql(dialect, tableName);
            List<Map<String, Object>> dataMap = jdbcTemplate4Tenant.queryForList(selectSql);
            List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
            for (Map<String, Object> data : dataMap) {
                Map<String, Object> formData = new HashMap<>(16);
                populateFormData(data, elementList, formData);
                result.add(formData);
            }
        } catch (Exception e) {
            LOGGER.error("查询主表所有数据失败, tableName: {}", tableName, e);
        }
    }

    /**
     * 构建查询所有数据的SQL
     */
    private String buildSelectSql(String dialect, String tableName) {
        if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
            || DialectEnum.KINGBASE.getValue().equals(dialect)) {
            return "SELECT * FROM \"" + tableName + "\"";
        } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            return "SELECT * FROM " + tableName;
        }
        return "SELECT * FROM " + tableName;
    }

    /**
     * 将listMap转为map
     *
     * @param listMap
     * @return
     */
    private Map<String, Object> listMapToKeyValue(List<Map<String, Object>> listMap) {
        Map<String, Object> map = new CaseInsensitiveMap<>(16);
        for (Map<String, Object> m : listMap) {
            map.put((String)m.get("name"), m.get("value"));
        }
        return map;
    }

    @Override
    public Y9Page<Map<String, Object>> pageFormList(String systemName, int page, int rows) {
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.UPDATETIME_KEY);
        Pageable pageable = PageRequest.of(page - 1, rows, sort);
        Page<Y9Form> pageList;
        if (StringUtils.isBlank(systemName)) {
            pageList = y9FormRepository.findAll(pageable);
        } else {
            pageList = y9FormRepository.findBySystemName(systemName, pageable);
        }
        List<Y9Form> list = pageList.getContent();
        List<Map<String, Object>> listMap = new ArrayList<>();
        List<Map<String, Object>> slist = itemRepository.getItemSystem();
        String systemCnName = "";
        for (Map<String, Object> m : slist) {
            if (m.get("systemName").equals(systemName)) {
                systemCnName = m.get("sysLevel").toString();
            }
        }
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", y9Form.getId());
            map.put("formName", y9Form.getFormName());
            map.put("formType", y9Form.getFormType());
            map.put("templateType", y9Form.getTemplateType());
            map.put("fileName", y9Form.getFileName() == null ? "" : y9Form.getFileName());
            map.put("systemCnName", systemCnName.isEmpty() ? y9Form.getSystemCnName() : systemCnName);
            map.put("systemName", y9Form.getSystemName());
            map.put(ItemConsts.UPDATETIME_KEY, Y9DateTimeUtils.formatDateTime(y9Form.getUpdateTime()));
            listMap.add(map);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Y9Result<Object> saveChildTableData(String formId, String tableId, String processSerialNumber,
        String jsonData) {
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> dataList = Y9JsonUtil.readValue(jsonData, List.class);
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
            assert dataList != null;
            for (Map<String, Object> keyValue : dataList) {
                String guid = getGuidFromData(keyValue);
                String actionType = determineActionType(guid, tableName);
                if ("0".equals(actionType)) {
                    performInsertOperation(dialect, tableName, keyValue, formId, tableFieldList, false);
                } else {
                    performUpdateOperation(dialect, tableName, keyValue, formId, tableFieldList);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存子表数据失败, formId: {}, tableId: {}", formId, tableId, e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 从数据中获取GUID
     */
    private String getGuidFromData(Map<String, Object> keyValue) {
        String guid = keyValue.get("guid") != null ? (String)keyValue.get("guid") : "";
        if (StringUtils.isBlank(guid)) {
            guid = keyValue.get("GUID") != null ? (String)keyValue.get("GUID") : "";
        }
        return guid;
    }

    /**
     * 确定操作类型（插入或更新）
     */
    private String determineActionType(String guid, String tableName) {
        Map<String, Object> dataMap = this.getData(guid, tableName);
        return dataMap.get(ItemConsts.EDITTYPE_KEY).equals("0") ? "0" : "1";
    }

    /**
     * 执行更新操作
     */
    private void performUpdateOperation(String dialect, String tableName, Map<String, Object> keyValue, String formId,
        List<Y9TableField> tableFieldList) {
        try {
            List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
            StringBuilder sqlBuilder = new StringBuilder();
            List<Object> parameters = new ArrayList<>();
            // 构建UPDATE语句
            buildUpdateSql(dialect, tableName, sqlBuilder);
            // 构建SET子句
            StringBuilder setClause = new StringBuilder();
            boolean isFirst = true;
            for (Y9FormField element : elementList) {
                if (!element.getTableName().equals(tableName)) {
                    continue;
                }
                String fieldName = element.getFieldName();
                // GUID字段作为WHERE条件，不在SET子句中
                if (isGuidField(fieldName)) {
                    continue;
                }
                Y9TableField y9TableField = findTableField(tableFieldList, fieldName);
                if (y9TableField != null) {
                    if (!isFirst) {
                        setClause.append(",");
                    }
                    setClause.append(fieldName).append(" = ?");
                    Object value = formatFieldValue(keyValue, fieldName, y9TableField);
                    parameters.add(value);

                    isFirst = false;
                }
            }
            // 添加WHERE条件
            String guidValue = getGuidFromData(keyValue);
            parameters.add(guidValue);
            sqlBuilder.append(setClause).append(" WHERE guid = ?");
            jdbcTemplate4Tenant.update(sqlBuilder.toString(), parameters.toArray());
        } catch (Exception e) {
            LOGGER.error("执行更新操作失败, tableName: {}", tableName, e);
            throw new RuntimeException("更新操作失败", e);
        }
    }

    /**
     * 构建INSERT SQL语句头
     */
    private void buildInsertSql(String dialect, String tableName, StringBuilder sqlBuilder) {
        if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
            || DialectEnum.KINGBASE.getValue().equals(dialect)) {
            sqlBuilder.append("INSERT INTO \"").append(tableName).append("\" (");
        } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            sqlBuilder.append("INSERT INTO ").append(tableName).append(" (");
        }
    }

    /**
     * 构建UPDATE SQL语句头
     */
    private void buildUpdateSql(String dialect, String tableName, StringBuilder sqlBuilder) {
        if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
            || DialectEnum.KINGBASE.getValue().equals(dialect)) {
            sqlBuilder.append("UPDATE \"").append(tableName).append("\" SET ");
        } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");
        }
    }

    /**
     * 查找表字段
     */
    private Y9TableField findTableField(List<Y9TableField> tableFieldList, String fieldName) {
        for (Y9TableField tableField : tableFieldList) {
            if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                return tableField;
            }
        }
        return null;
    }

    /**
     * 判断是否为GUID字段
     */
    /**
     * 判断是否为GUID字段
     */
    private boolean isGuidField(String fieldName) {
        return "guid".equalsIgnoreCase(fieldName) || "Z_GUID".equalsIgnoreCase(fieldName);
    }

    /**
     * 格式化字段值
     */
    /**
     * 格式化字段值
     */
    private Object formatFieldValue(Map<String, Object> keyValue, String fieldName, Y9TableField y9TableField) {
        Object value = keyValue.get(fieldName);
        // 特殊字段处理
        if (isGuidField(fieldName)) {
            return handleSpecialField(value);
        }
        // 数组类型处理
        if (value instanceof ArrayList) {
            return handleArrayField(value);
        }
        // 空值处理
        if (value == null) {
            return null;
        }
        // 数据类型处理
        return handleTypedField(value, y9TableField);
    }

    /**
     * 处理特殊字段（GUID）
     */
    private Object handleSpecialField(Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return Y9IdGenerator.genId(IdType.SNOWFLAKE);
        }
        return value;
    }

    /**
     * 处理数组类型字段
     */
    private Object handleArrayField(Object value) {
        try {
            return Y9JsonUtil.writeValueAsString(value);
        } catch (Exception e) {
            LOGGER.warn("序列化数组字段失败", e);
            return "";
        }
    }

    /**
     * 根据字段类型处理值
     */
    private Object handleTypedField(Object value, Y9TableField y9TableField) {
        String fieldType = y9TableField.getFieldType().toLowerCase();
        String valueStr = value.toString();
        if (fieldType.contains("int")) {
            return handleIntegerField(value, valueStr);
        } else if (fieldType.contains("bit")) {
            return handleBitField(valueStr);
        } else if (y9TableField.getFieldType().toUpperCase().contains(ItemConsts.DOUBLE_UP_KEY)
            || y9TableField.getFieldType().toUpperCase().contains(ItemConsts.FLOAT_UP_KEY)) {
            return handleFloatField(value, valueStr);
        } else if (fieldType.contains("date")
            || y9TableField.getFieldType().toUpperCase().contains(ItemConsts.TIMESTAMP_UP_KEY)) {
            return handleDateField(value);
        } else {
            return handleStringField(valueStr);
        }
    }

    /**
     * 处理整数类型字段
     */
    private Object handleIntegerField(Object value, String valueStr) {
        return StringUtils.isNotBlank(valueStr) ? value : null;
    }

    /**
     * 处理位类型字段
     */
    private Object handleBitField(String valueStr) {
        if (StringUtils.isNotBlank(valueStr)) {
            return Boolean.parseBoolean(valueStr);
        }
        return null;
    }

    /**
     * 处理浮点数类型字段
     */
    private Object handleFloatField(Object value, String valueStr) {
        return StringUtils.isNotBlank(valueStr) ? value : null;
    }

    /**
     * 处理日期类型字段
     */
    private Object handleDateField(Object value) {
        return value;
    }

    /**
     * 处理字符串类型字段
     */
    private Object handleStringField(String valueStr) {
        return StringUtils.isNotBlank(valueStr) ? valueStr : "";
    }

    @Override
    @Transactional
    public Y9Result<Object> saveChildTableData(String formId, String jsonData) {
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> listMap = Y9JsonUtil.readValue(jsonData, new TypeReference<>() {});
            assert listMap != null;
            Map<String, Object> keyValue = this.listMapToKeyValue(listMap);
            String guid = getGuidFromData(keyValue);
            List<String> tableNames = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNames) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 只处理子表数据
                if (y9Table.getTableType() == ItemTableTypeEnum.MAIN) {
                    continue;
                }
                String actionType = determineActionType(guid, tableName);
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
                if ("0".equals(actionType)) {
                    performInsertOperation(dialect, tableName, keyValue, formId, tableFieldList, false);
                } else {
                    performUpdateOperation(dialect, tableName, keyValue, formId, tableFieldList);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存子表数据失败, formId: {}", formId, e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 执行插入操作
     */
    private void performInsertOperation(String dialect, String tableName, Map<String, Object> keyValue, String formId,
        List<Y9TableField> tableFieldList, boolean isMainTable) {
        try {
            StringBuilder sqlBuilder = new StringBuilder();
            List<Object> parameters = new ArrayList<>();
            // 构建INSERT语句
            buildInsertSql(dialect, tableName, sqlBuilder);
            // 获取表单字段列表
            List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);

            // 如果是主表，需要处理GUID字段
            if (isMainTable) {
                handleGuidFieldForInsert(elementList);
            }

            // 构建字段和值部分
            StringBuilder fieldsBuilder = new StringBuilder();
            StringBuilder valuesPlaceholder = new StringBuilder();
            buildInsertFieldsAndValues(elementList, tableFieldList, keyValue, fieldsBuilder, valuesPlaceholder,
                parameters);

            sqlBuilder.append(fieldsBuilder).append(") values (").append(valuesPlaceholder).append(")");
            jdbcTemplate4Tenant.update(sqlBuilder.toString(), parameters.toArray());
        } catch (Exception e) {
            LOGGER.error("执行插入操作失败, tableName: {}", tableName, e);
            throw new RuntimeException("插入操作失败", e);
        }
    }

    /**
     * 构建INSERT语句的字段和值部分
     */
    private void buildInsertFieldsAndValues(List<Y9FormField> elementList, List<Y9TableField> tableFieldList,
        Map<String, Object> keyValue, StringBuilder fieldsBuilder, StringBuilder valuesPlaceholder,
        List<Object> parameters) {
        List<String> fieldNames = new ArrayList<>();
        List<Object> fieldValues = new ArrayList<>();
        for (Y9FormField element : elementList) {
            String fieldName = element.getFieldName();
            Y9TableField y9TableField = findTableField(tableFieldList, fieldName);
            if (y9TableField != null) {
                fieldNames.add(fieldName);
                Object value = formatFieldValue(keyValue, fieldName, y9TableField);
                fieldValues.add(value);
            }
        }
        // 构建字段名部分
        for (int i = 0; i < fieldNames.size(); i++) {
            if (i > 0) {
                fieldsBuilder.append(",");
            }
            fieldsBuilder.append(fieldNames.get(i));
        }
        // 构建占位符部分
        for (int i = 0; i < fieldValues.size(); i++) {
            if (i > 0) {
                valuesPlaceholder.append(",");
            }
            valuesPlaceholder.append("?");
            parameters.add(fieldValues.get(i));
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @Transactional
    public Y9Result<Object> saveFormData(String formData) {
        try {
            String dialect = DbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> listMap = Y9JsonUtil.readValue(formData, List.class);
            assert listMap != null;
            Map<String, Object> keyValue = this.listMapToKeyValue(listMap);
            String formId = (String)keyValue.get("form_Id");
            if (!keyValue.containsKey("guid") && !keyValue.containsKey("GUID")) {
                LOGGER.error("保存失败:表单未绑定guid字段");
                return Y9Result.failure("保存失败:表单未绑定guid字段");
            }
            String guid = getGuidFromData(keyValue);
            List<String> tableNames = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNames) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 只处理主表数据
                if (y9Table.getTableType() == ItemTableTypeEnum.SUB) {
                    continue;
                }
                String actionType = determineActionType(guid, tableName);
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
                if ("0".equals(actionType)) {
                    performInsertOperation(dialect, tableName, keyValue, formId, tableFieldList, true);
                } else {
                    performUpdateOperation(dialect, tableName, keyValue, formId, tableFieldList);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存表单数据失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 处理插入操作中的GUID字段
     */
    private void handleGuidFieldForInsert(List<Y9FormField> elementList) {
        boolean isHaveGuid = elementList.stream().anyMatch(element -> "GUID".equalsIgnoreCase(element.getFieldName()));
        if (!isHaveGuid) {
            Y9FormField guidField = new Y9FormField();
            guidField.setFieldName("GUID");
            elementList.add(guidField);
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> saveFormField(String formId, String fieldJson) {
        try {
            List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fieldJson, String.class, Object.class);
            y9FormFieldRepository.deleteByFormId(formId);
            assert listMap != null;
            for (Map<String, Object> map : listMap) {
                Y9FormField formField = new Y9FormField();
                formField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                formField.setFieldCnName((String)map.get("fieldCnName"));
                formField.setFieldName((String)map.get("fieldName"));
                formField.setFieldType((String)map.get("fieldType"));
                formField.setFormId(formId);
                formField.setTableId((String)map.get("tableId"));
                formField.setTableName((String)map.get("tableName"));
                formField.setQuerySign((String)map.get("querySign"));
                formField.setQueryType((String)map.get("queryType"));
                formField.setOptionValue((String)map.get("optionValue"));
                formField.setContentUsedFor(map.get("contentUsedFor") != null ? (String)map.get("contentUsedFor") : "");
                y9FormFieldRepository.save(formField);
            }
            return Y9Result.successMsg("保存字段成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存字段失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> saveFormJson(String id, String formJson) {
        try {
            Optional<Y9Form> formOpt = y9FormRepository.findById(id);
            if (formOpt.isPresent()) {
                Y9Form form = formOpt.get();
                form.setFormJson(formJson);
                y9FormRepository.save(form);
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }

    }

    @Override
    @Transactional
    public Y9Result<Object> saveOrUpdate(Y9Form form) {
        try {
            Y9Form targetForm;
            boolean isNew = StringUtils.isBlank(form.getId());
            if (isNew) {
                targetForm = createNewForm(form);
            } else {
                targetForm = y9FormRepository.findById(form.getId()).orElse(null);
                if (targetForm == null) {
                    // 如果找不到对应记录，则直接保存传入的表单对象
                    y9FormRepository.save(form);
                    return Y9Result.successMsg("保存成功");
                }
            }
            // 更新表单属性
            updateFormProperties(targetForm, form);
            y9FormRepository.save(targetForm);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存表单失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 创建新的表单对象
     */
    private Y9Form createNewForm(Y9Form form) {
        Y9Form newForm = new Y9Form();
        newForm.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        updateFormProperties(newForm, form);
        return newForm;
    }

    /**
     * 更新表单属性
     */
    private void updateFormProperties(Y9Form targetForm, Y9Form sourceForm) {
        targetForm.setFileName(sourceForm.getFileName());
        targetForm.setFormName(sourceForm.getFormName());
        targetForm.setFormType(sourceForm.getFormType());
        targetForm.setSystemCnName(sourceForm.getSystemCnName());
        targetForm.setSystemName(sourceForm.getSystemName());
        targetForm.setTemplateType(sourceForm.getTemplateType());
        targetForm.setUpdateTime(new Date());
        targetForm.setPersonId(Y9LoginUserHolder.getPersonId());
        targetForm.setOriginalContent(sourceForm.getOriginalContent());
        targetForm.setCssUrl(sourceForm.getCssUrl());
        targetForm.setJsUrl(sourceForm.getJsUrl());
        targetForm.setInitDataUrl(sourceForm.getInitDataUrl());
    }
}
