package net.risesoft.service.form.impl;

import java.text.SimpleDateFormat;
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
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.form.Y9FormDbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormServiceImpl implements Y9FormService {

    private final JdbcTemplate jdbcTemplate4Tenant;
    private final Y9FormRepository y9FormRepository;

    private final Y9TableService y9TableService;

    private final Y9FormFieldRepository y9FormFieldRepository;

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final SpmApproveItemRepository approveItemRepository;

    public Y9FormServiceImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        Y9FormRepository y9FormRepository, Y9TableService y9TableService, Y9FormFieldRepository y9FormFieldRepository,
        Y9TableFieldRepository y9TableFieldRepository, SpmApproveItemRepository approveItemRepository) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9FormRepository = y9FormRepository;
        this.y9TableService = y9TableService;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
        this.approveItemRepository = approveItemRepository;
    }

    @Override
    @Transactional
    public Y9Result<Object> delChildTableRow(String formId, String tableId, String guid) {
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
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
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
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
    public boolean deleteByGuid(String y9TableId, String guid) {
        try {
            Y9Table y9Table = y9TableService.findById(y9TableId);
            String tableName = y9Table.getTableName();
            String sql = "DELETE FROM " + tableName + " WHERE GUID='" + guid + "'";
            jdbcTemplate4Tenant.execute(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
        map.put("edittype", "0");
        try {
            if (StringUtils.isBlank(guid)) {
                return map;
            }
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            String dataSql = "";
            if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                dataSql = "select * from \"" + tableName + "\" t where t.guid=?";
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                dataSql = "select * from " + tableName + " t where t.guid=?";
            }
            List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(dataSql, guid);
            if (datamap.isEmpty()) {
                map.put("edittype", "0");
            } else {
                map.put("edittype", "1");
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> getFormData(String formId, String guid) {
        Map<String, Object> map = new HashMap<>(16);
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType() == 1) {
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where guid =?");
                    }
                    List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), guid);
                    if (!datamap.isEmpty()) {
                        List<Y9FormField> elementList =
                            y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                        for (Y9FormField element : elementList) {
                            String fieldName = element.getFieldName();
                            resMap.put(fieldName,
                                datamap.get(0).get(fieldName) != null ? datamap.get(0).get(fieldName).toString() : "");
                        }
                    }
                }
            }
            map.put("formData", resMap);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("formData", resMap);
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Object> getFormData4Var(String formId, String guid) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType() == 1) {
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where guid =?");
                    }
                    List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), guid);
                    if (!datamap.isEmpty()) {
                        List<Y9TableField> tableFieldList =
                            y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(y9Table.getId());
                        for (Y9TableField tableField : tableFieldList) {
                            if (null != tableField.getIsVar() && 1 == tableField.getIsVar()) {
                                String fieldName = tableField.getFieldName();
                                map.put(fieldName, datamap.get(0).get(fieldName) != null
                                    ? datamap.get(0).get(fieldName).toString() : "");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public String getFormField(String id) {
        List<Y9FormField> list = y9FormFieldRepository.findByFormId(id);
        return Y9JsonUtil.writeValueAsString(list);
    }

    @Override
    public List<Y9Form> listAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        return y9FormRepository.findAll(sort);
    }

    @Override
    public List<Map<String, Object>> listChildFormData(String formId, String parentProcessSerialNumber) {
        List<Map<String, Object>> datamap = new ArrayList<>();
        String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
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
            if (y9Table.getTableType() == 2) {// 子表查询
                StringBuilder sqlStr = new StringBuilder();
                if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder(
                        "SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?" + userIdSql);
                } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                    sqlStr = new StringBuilder(
                        "SELECT * FROM " + tableName + " where parentProcessSerialNumber =?" + userIdSql);
                }
                datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), parentProcessSerialNumber);
                return datamap;
            }
        }
        return datamap;
    }

    @Override
    public List<Map<String, Object>> listChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception {
        List<Map<String, Object>> datamap = new ArrayList<>();
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            StringBuilder sqlStr = new StringBuilder();
            if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?");
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where parentProcessSerialNumber =?");
            }
            datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), processSerialNumber);
            return datamap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Y9FormServiceImpl getChildTableData error");
        }
    }

    @Override
    public List<Map<String, Object>> listFormData(String formId) {
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType() == 1) {
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\"");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr = new StringBuilder("SELECT * FROM " + tableName);
                    }
                    List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString());
                    for (Map<String, Object> data : datamap) {
                        List<Y9FormField> elementList =
                            y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                        Map<String, Object> map = new HashMap<>(16);
                        for (Y9FormField element : elementList) {
                            String fieldName = element.getFieldName();
                            map.put(fieldName, data.get(fieldName) != null ? data.get(fieldName).toString() : "");
                        }
                        resList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 将listMap转为map
     *
     * @param listMap
     * @return
     */
    private final Map<String, Object> listMapToKeyValue(List<Map<String, Object>> listMap) {
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
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(page - 1, rows, sort);
        Page<Y9Form> pageList = null;
        if (StringUtils.isBlank(systemName)) {
            pageList = y9FormRepository.findAll(pageable);
        } else {
            pageList = y9FormRepository.findBySystemName(systemName, pageable);
        }
        List<Y9Form> list = pageList.getContent();
        List<Map<String, Object>> listMap = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> slist = approveItemRepository.getItemSystem();
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
            map.put("systemCnName", systemCnName.equals("") ? y9Form.getSystemCnName() : systemCnName);
            map.put("systemName", y9Form.getSystemName());
            map.put("updateTime", sdf.format(y9Form.getUpdateTime()));
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
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> list = Y9JsonUtil.readValue(jsonData, List.class);
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
            List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
            for (Map<String, Object> keyValue : list) {
                String guid = keyValue.get("guid") != null ? (String)keyValue.get("guid") : "";
                if (StringUtils.isBlank(guid)) {
                    guid = keyValue.get("GUID") != null ? (String)keyValue.get("GUID") : "";
                }
                String actionType = "0";
                Map<String, Object> m = this.getData(guid, tableName);
                if (m.get("edittype").equals("0")) {
                    actionType = "0";
                } else {
                    actionType = "1";
                }
                if (actionType.equals("0")) {
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("insert into " + tableName + " (");
                    }
                    StringBuilder sqlStr1 = new StringBuilder(") values (");
                    boolean isHaveField = false;
                    for (Y9FormField element : elementList) {
                        String fieldName = element.getFieldName();
                        Y9TableField y9TableField = null;
                        for (Y9TableField tableField : tableFieldList) {
                            if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                y9TableField = tableField;
                                break;
                            }
                        }
                        if (y9TableField != null) {
                            if (isHaveField) {
                                sqlStr.append(",");
                            }
                            sqlStr.append(fieldName);
                            if (isHaveField) {
                                sqlStr1.append(",");
                            }
                            if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                // sqlStr1.append(keyValue.get(fieldName));
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1
                                        .append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else {
                                if (fieldName.equals("guid") || fieldName.equals("GUID") || fieldName.equals("Z_GUID")
                                    || fieldName.equals("z_guid")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (fieldName.equals("processInstanceId")
                                    || fieldName.equals("PROCESSINSTANCEID")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr1.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr1.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                            }
                            isHaveField = true;
                        }
                    }
                    sqlStr1.append(")");
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                } else {// 编辑
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("update " + tableName + " set ");
                    }
                    StringBuilder sqlStr1 = new StringBuilder();
                    boolean isHaveField = false;
                    for (Y9FormField element : elementList) {
                        if (element.getTableName().equals(tableName)) {
                            String fieldName = element.getFieldName();
                            Y9TableField y9TableField = null;
                            for (Y9TableField tableField : tableFieldList) {
                                if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                    y9TableField = tableField;
                                    break;
                                }
                            }
                            if (y9TableField != null) {
                                if (fieldName.equals("guid") || fieldName.equals("GUID")) {
                                    sqlStr1.append(" where guid ='" + keyValue.get(fieldName) + "'");
                                    continue;
                                }
                                if (isHaveField) {
                                    sqlStr.append(",");
                                }
                                sqlStr.append(fieldName + "=");
                                if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                    // sqlStr.append(keyValue.get(fieldName));
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                    || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append(
                                            "TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                                isHaveField = true;
                            }
                        }
                    }
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> saveChildTableData(String formId, String jsonData) {
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> listMap = Y9JsonUtil.readValue(jsonData, List.class);
            Map<String, Object> keyValue = this.listMapToKeyValue(listMap);
            String guid = keyValue.get("guid") != null ? (String)keyValue.get("guid") : "";
            if (StringUtils.isBlank(guid)) {
                guid = keyValue.get("GUID") != null ? (String)keyValue.get("GUID") : "";
            }
            List<String> list = y9FormRepository.findBindTableName(formId);
            for (String tableName : list) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType() == 1) {// 排除主表
                    continue;
                }
                String actionType = "0";
                Map<String, Object> m = this.getData(guid, tableName);
                if (m.get("edittype").equals("0")) {
                    actionType = "0";
                } else {
                    actionType = "1";
                }
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
                if (actionType.equals("0")) {
                    List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("insert into " + tableName + " (");
                    }
                    StringBuilder sqlStr1 = new StringBuilder(") values (");
                    boolean isHaveField = false;
                    for (Y9FormField element : elementList) {
                        String fieldName = element.getFieldName();
                        Y9TableField y9TableField = null;
                        for (Y9TableField tableField : tableFieldList) {
                            if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                y9TableField = tableField;
                                break;
                            }
                        }
                        if (y9TableField != null) {
                            if (isHaveField) {
                                sqlStr.append(",");
                            }
                            sqlStr.append(fieldName);
                            if (isHaveField) {
                                sqlStr1.append(",");
                            }
                            if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                // sqlStr1.append(keyValue.get(fieldName));
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1
                                        .append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else {
                                if (fieldName.equals("guid") || fieldName.equals("GUID") || fieldName.equals("Z_GUID")
                                    || fieldName.equals("z_guid")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (fieldName.equals("processInstanceId")
                                    || fieldName.equals("PROCESSINSTANCEID")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr1.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr1.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                            }
                            isHaveField = true;
                        }
                    }
                    sqlStr1.append(")");
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                } else {// 编辑
                    List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("update " + tableName + " set ");
                    }
                    StringBuilder sqlStr1 = new StringBuilder();
                    boolean isHaveField = false;
                    for (Y9FormField element : elementList) {
                        if (element.getTableName().equals(tableName)) {
                            String fieldName = element.getFieldName();
                            Y9TableField y9TableField = null;
                            for (Y9TableField tableField : tableFieldList) {
                                if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                    y9TableField = tableField;
                                    break;
                                }
                            }
                            if (y9TableField != null) {
                                if (fieldName.equals("guid") || fieldName.equals("GUID")) {
                                    sqlStr1.append(" where guid ='" + keyValue.get(fieldName) + "'");
                                    continue;
                                }
                                if (isHaveField) {
                                    sqlStr.append(",");
                                }
                                sqlStr.append(fieldName + "=");
                                if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                    // sqlStr.append(keyValue.get(fieldName));
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                    || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append(
                                            "TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                                isHaveField = true;
                            }
                        }
                    }
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @Transactional
    public Y9Result<Object> saveFormData(String formdata) {
        try {
            String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, Object>> listMap = Y9JsonUtil.readValue(formdata, List.class);
            Map<String, Object> keyValue = this.listMapToKeyValue(listMap);
            String formId = (String)keyValue.get("form_Id");
            String guid = keyValue.get("guid") != null ? (String)keyValue.get("guid") : "";
            if (StringUtils.isBlank(guid)) {
                guid = keyValue.get("GUID") != null ? (String)keyValue.get("GUID") : "";
            }
            if (StringUtils.isBlank(guid)) {
                return Y9Result.failure("保存失败:表单未绑定guid字段");
            }
            List<String> list = y9FormRepository.findBindTableName(formId);
            for (String tableName : list) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType().equals(ItemTableTypeEnum.SUB.getValue())) {
                    continue;
                }
                String actionType;
                Map<String, Object> m = this.getData(guid, tableName);
                if ("0".equals(m.get("edittype"))) {
                    actionType = "0";
                } else {
                    actionType = "1";
                }
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
                if ("0".equals(actionType)) {
                    List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("insert into \"").append(tableName).append("\" (");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("insert into ").append(tableName).append(" (");
                    }
                    StringBuilder sqlStr1 = new StringBuilder(") values (");
                    boolean isHaveField = false;
                    /*
                    一个表单绑定多个表的时候只会绑定一个guid，其他没有绑定guid的表，需要加上guid字段
                     */
                    boolean isHaveGuid =
                        elementList.stream().anyMatch(element -> "GUID".equalsIgnoreCase(element.getFieldName()));
                    if (!isHaveGuid) {
                        Y9FormField guidField = new Y9FormField();
                        guidField.setFieldName("GUID");
                        elementList.add(guidField);
                    }
                    for (Y9FormField element : elementList) {
                        String fieldName = element.getFieldName();
                        Y9TableField y9TableField = null;
                        for (Y9TableField tableField : tableFieldList) {
                            if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                y9TableField = tableField;
                                break;
                            }
                        }
                        if (y9TableField != null) {
                            if (isHaveField) {
                                sqlStr.append(",");
                            }
                            sqlStr.append(fieldName);
                            if (isHaveField) {
                                sqlStr1.append(",");
                            }
                            if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                // sqlStr1.append(keyValue.get(fieldName));
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toLowerCase().contains("bit")) {
                                boolean isTrue = false;
                                if ((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))) {
                                    isTrue = Boolean.parseBoolean(String.valueOf(keyValue.get(fieldName)));
                                }
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName)))) ? isTrue
                                        : null);
                            } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                sqlStr1.append((keyValue.get(fieldName) != null
                                    && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                        ? keyValue.get(fieldName) : null);
                            } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1.append("TO_DATE('").append(keyValue.get(fieldName))
                                        .append("','yyyy-MM-dd')");
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                if (DialectEnum.ORACLE.getValue().equals(dialect)
                                    || DialectEnum.DM.getValue().equals(dialect)
                                    || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                    sqlStr1.append("TO_DATE('").append(keyValue.get(fieldName))
                                        .append("','yyyy-MM-dd HH24:mi:ss')");
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                        ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else {
                                if (fieldName.equalsIgnoreCase("guid") || fieldName.equalsIgnoreCase("Z_GUID")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'").append(Y9IdGenerator.genId(IdType.SNOWFLAKE)).append("'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (fieldName.equalsIgnoreCase("processInstanceId")) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'").append(Y9IdGenerator.genId(IdType.SNOWFLAKE)).append("'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr1.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr1.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                            }
                            isHaveField = true;
                        }
                    }
                    sqlStr1.append(")");
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                } else {// 编辑
                    List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                    StringBuilder sqlStr = new StringBuilder();
                    if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                        sqlStr.append("update \"").append(tableName).append("\" set ");
                    } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                        sqlStr.append("update ").append(tableName).append(" set ");
                    }
                    StringBuilder sqlStr1 = new StringBuilder();
                    boolean isHaveField = false;
                    for (Y9FormField element : elementList) {
                        if (element.getTableName().equals(tableName)) {
                            String fieldName = element.getFieldName();
                            Y9TableField y9TableField = null;
                            for (Y9TableField tableField : tableFieldList) {
                                if (tableField.getFieldName().equalsIgnoreCase(fieldName)) {
                                    y9TableField = tableField;
                                    break;
                                }
                            }
                            if (y9TableField != null) {
                                if (fieldName.equals("guid") || fieldName.equals("GUID")) {
                                    sqlStr1.append(" where guid ='" + keyValue.get(fieldName) + "'");
                                    continue;
                                }
                                if (isHaveField) {
                                    sqlStr.append(",");
                                }
                                sqlStr.append(fieldName + "=");
                                if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                    // sqlStr.append(keyValue.get(fieldName));
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toLowerCase().contains("bit")) {
                                    boolean isTrue = false;
                                    if ((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))) {
                                        isTrue = Boolean.parseBoolean(String.valueOf(keyValue.get(fieldName)));
                                    }
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName)))) ? isTrue
                                            : null);
                                } else if (y9TableField.getFieldType().toUpperCase().contains("DOUBLE")
                                    || y9TableField.getFieldType().toUpperCase().contains("FLOAT")) {
                                    sqlStr.append((keyValue.get(fieldName) != null
                                        && StringUtils.isNotBlank(String.valueOf(keyValue.get(fieldName))))
                                            ? keyValue.get(fieldName) : null);
                                } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                    if (DialectEnum.ORACLE.getValue().equals(dialect)
                                        || DialectEnum.DM.getValue().equals(dialect)
                                        || DialectEnum.KINGBASE.getValue().equals(dialect)) {
                                        sqlStr.append(
                                            "TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName))
                                            ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    if (keyValue.get(fieldName) instanceof ArrayList) {
                                        sqlStr.append(StringUtils.isNotBlank(keyValue.get(fieldName).toString())
                                            ? "'" + Y9JsonUtil.writeValueAsString(keyValue.get(fieldName)) + "'"
                                            : "''");
                                    } else {
                                        sqlStr.append((keyValue.get(fieldName) != null
                                            && StringUtils.isNotBlank(keyValue.get(fieldName).toString()))
                                                ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                }
                                isHaveField = true;
                            }
                        }
                    }
                    sqlStr.append(sqlStr1);
                    String sql = sqlStr.toString();
                    jdbcTemplate4Tenant.execute(sql);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> saveFormField(String formId, String fieldJson) {
        try {
            List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fieldJson, String.class, Object.class);
            y9FormFieldRepository.deleteByFormId(formId);
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
            if (StringUtils.isBlank(form.getId())) {
                Y9Form newForm = new Y9Form();
                newForm.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newForm.setFileName(form.getFileName());
                newForm.setFormName(form.getFormName());
                newForm.setFormType(form.getFormType());
                newForm.setSystemCnName(form.getSystemCnName());
                newForm.setSystemName(form.getSystemName());
                newForm.setTemplateType(form.getTemplateType());
                newForm.setUpdateTime(new Date());
                newForm.setPersonId(Y9LoginUserHolder.getPersonId());
                newForm.setOriginalContent(form.getOriginalContent());
                newForm.setCssUrl(form.getCssUrl());
                newForm.setJsUrl(form.getJsUrl());
                newForm.setInitDataUrl(form.getInitDataUrl());
                y9FormRepository.save(newForm);
            } else {
                Y9Form oldForm = y9FormRepository.findById(form.getId()).orElse(null);
                if (null == oldForm) {
                    y9FormRepository.save(form);
                } else {
                    oldForm.setFileName(form.getFileName());
                    oldForm.setFormName(form.getFormName());
                    oldForm.setFormType(form.getFormType());
                    oldForm.setSystemCnName(form.getSystemCnName());
                    oldForm.setSystemName(form.getSystemName());
                    oldForm.setTemplateType(form.getTemplateType());
                    oldForm.setUpdateTime(new Date());
                    oldForm.setPersonId(Y9LoginUserHolder.getPersonId());
                    oldForm.setOriginalContent(form.getOriginalContent());
                    oldForm.setCssUrl(form.getCssUrl());
                    oldForm.setJsUrl(form.getJsUrl());
                    oldForm.setInitDataUrl(form.getInitDataUrl());
                    y9FormRepository.save(oldForm);
                }
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }
}
