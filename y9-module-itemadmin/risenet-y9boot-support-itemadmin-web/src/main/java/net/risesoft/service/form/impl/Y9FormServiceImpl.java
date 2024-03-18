package net.risesoft.service.form.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "y9FormService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormServiceImpl implements Y9FormService {

    @Autowired
    private Y9FormRepository y9FormRepository;

    @Autowired
    private Y9TableService y9TableService;

    @Autowired
    private Y9FormFieldRepository y9FormFieldRepository;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    @Override
    public Map<String, Object> delChildTableRow(String formId, String tableId, String guid) {
        Connection connection = null;
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            StringBuffer sqlStr = new StringBuffer();
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("delete FROM \"" + tableName + "\" where guid = '" + guid + "'");
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("delete FROM \"" + tableName + "\" where guid = '" + guid + "'");
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("delete FROM \"" + tableName + "\" where guid = '" + guid + "'");
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("delete FROM " + tableName + " where guid = '" + guid + "'");
            }
            jdbcTemplate4Tenant.execute(sqlStr.toString());
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String[] id = ids.split(",");
            for (String idTemp : id) {
                y9FormRepository.deleteById(idTemp);
                y9FormFieldRepository.deleteByFormId(idTemp);
            }
            map.put("msg", "删除成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
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
    public List<Y9Form> findAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        return y9FormRepository.findAll(sort);
    }

    @Override
    public Y9Form findById(String id) {
        Y9Form c = y9FormRepository.findById(id).orElse(null);
        return c;
    }

    @Override
    public List<Map<String, Object>> getChildTableData(String formId, String tableId, String processSerialNumber) throws Exception {
        Connection connection = null;
        List<Map<String, Object>> datamap = new ArrayList<Map<String, Object>>();
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            Y9Table y9Table = y9TableService.findById(tableId);
            String tableName = y9Table.getTableName();
            StringBuffer sqlStr = new StringBuffer();
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?");
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?");
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where parentProcessSerialNumber =?");
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM " + tableName + " where parentProcessSerialNumber =?");
            }
            datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), processSerialNumber);
            return datamap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Y9FormServiceImpl getChildTableData error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Map<String, Object> getData(String guid, String tableName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("edittype", "0");
        Connection connection = null;
        try {
            if (StringUtils.isBlank(guid)) {
                return map;
            }
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            String dataSql = "";
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                dataSql = "select * from \"" + tableName + "\" t where t.guid=?";
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                dataSql = "select * from \"" + tableName + "\" t where t.guid=?";
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                dataSql = "select * from \"" + tableName + "\" t where t.guid=?";
            } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                dataSql = "select * from " + tableName + " t where t.guid=?";
            }
            List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(dataSql, guid);
            if (datamap.size() == 0) {
                map.put("edittype", "0");
            } else {
                map.put("edittype", "1");
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getFormData(String formId, String guid) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            List<String> tableNameList = y9FormRepository.findBindTableName(formId);
            for (String tableName : tableNameList) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                if (y9Table.getTableType() == 1) {
                    StringBuffer sqlStr = new StringBuffer();
                    if ("oracle".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("dm".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("kingbase".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
                    } else if ("mysql".equals(dialect)) {
                        sqlStr = new StringBuffer("SELECT * FROM " + tableName + " where guid =?");
                    }
                    List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(sqlStr.toString(), guid);
                    if (datamap.size() > 0) {
                        List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                        for (Y9FormField element : elementList) {
                            String fieldName = element.getFieldName();
                            resMap.put(fieldName, datamap.get(0).get(fieldName) != null ? datamap.get(0).get(fieldName).toString() : "");
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
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @Override
    public String getFormField(String id) {
        List<Y9FormField> list = y9FormFieldRepository.findByFormId(id);
        return Y9JsonUtil.writeValueAsString(list);
    }

    @Override
    public Map<String, Object> getFormList(String systemName, int page, int rows) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
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
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Y9Form y9Form : list) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", y9Form.getId());
            map.put("formName", y9Form.getFormName());
            map.put("formType", y9Form.getFormType());
            map.put("templateType", y9Form.getTemplateType());
            map.put("fileName", y9Form.getFileName() == null ? "" : y9Form.getFileName());
            map.put("systemCnName", y9Form.getSystemCnName());
            map.put("systemName", y9Form.getSystemName());
            map.put("updateTime", sdf.format(y9Form.getUpdateTime()));
            listMap.add(map);
        }
        resMap.put("rows", listMap);
        resMap.put("currpage", page);
        resMap.put("totalpages", pageList.getTotalPages());
        resMap.put("total", pageList.getTotalElements());
        resMap.put(UtilConsts.SUCCESS, true);
        return resMap;
    }

    /**
     * 将listMap转为map
     *
     * @param listMap
     * @return
     */
    private Map<String, Object> listMapToKeyValue(List<Map<String, Object>> listMap) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        for (Map<String, Object> m : listMap) {
            map.put((String)m.get("name"), (String)m.get("value"));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
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
                if ("0".equals(m.get("edittype"))) {
                    actionType = "0";
                } else {
                    actionType = "1";
                }
                // 新增
                if ("0".equals(actionType)) {
                    StringBuffer sqlStr = new StringBuffer("");
                    if ("oracle".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    }
                    if ("dm".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");

                    } else if ("mysql".equals(dialect)) {
                        sqlStr.append("insert into " + tableName + " (");

                    } else if ("kingbase".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    }
                    StringBuffer sqlStr1 = new StringBuffer(") values (");
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
                                sqlStr1.append(keyValue.get(fieldName));
                            } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                if ("oracle".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else if ("dm".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else if ("kingbase".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                if ("oracle".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else if ("kingbase".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else if ("dm".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else {
                                if ("guid".equals(fieldName) || "GUID".equals(fieldName) || "Z_GUID".equals(fieldName) || "z_guid".equals(fieldName)) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if ("processInstanceId".equals(fieldName) || "PROCESSINSTANCEID".equals(fieldName)) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
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
                    StringBuffer sqlStr = new StringBuffer("");
                    if ("oracle".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");

                    } else if ("dm".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");

                    } else if ("mysql".equals(dialect)) {
                        sqlStr.append("update " + tableName + " set ");

                    } else if ("kingbase".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");
                    }
                    StringBuffer sqlStr1 = new StringBuffer("");
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
                                // 主键不更新
                                if ("guid".equals(fieldName) || "GUID".equals(fieldName)) {
                                    sqlStr1.append(" where guid ='" + keyValue.get(fieldName) + "'");
                                    continue;
                                }
                                if (isHaveField) {
                                    sqlStr.append(",");
                                }
                                sqlStr.append(fieldName + "=");
                                if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                    sqlStr.append(keyValue.get(fieldName));
                                } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                    if ("oracle".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else if ("dm".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else if ("kingbase".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                    if ("oracle".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else if ("dm".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else if ("kingbase".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
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
            map.put("msg", "保存成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveFormData(String formdata) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            List<Map<String, Object>> listMap = Y9JsonUtil.readValue(formdata, List.class);
            Map<String, Object> keyValue = this.listMapToKeyValue(listMap);
            String formId = (String)keyValue.get("form_Id");
            String guid = keyValue.get("guid") != null ? (String)keyValue.get("guid") : "";
            if (StringUtils.isBlank(guid)) {
                guid = keyValue.get("GUID") != null ? (String)keyValue.get("GUID") : "";
            }
            List<String> list = y9FormRepository.findBindTableName(formId);
            for (String tableName : list) {
                Y9Table y9Table = y9TableService.findByTableName(tableName);
                // 子表数据不保存
                if (y9Table.getTableType() == 2) {
                    continue;
                }
                String actionType = "0";
                Map<String, Object> m = this.getData(guid, tableName);
                if ("0".equals(m.get("edittype"))) {
                    actionType = "0";
                } else {
                    actionType = "1";
                }
                List<Y9TableField> tableFieldList = y9TableFieldRepository.findByTableName(tableName);
                if ("0".equals(actionType)) {
                    List<Y9FormField> elementList = y9FormFieldRepository.findByFormIdAndTableName(formId, tableName);
                    StringBuffer sqlStr = new StringBuffer("");
                    if ("oracle".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    }
                    if ("dm".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");

                    } else if ("mysql".equals(dialect)) {
                        sqlStr.append("insert into " + tableName + " (");

                    } else if ("kingbase".equals(dialect)) {
                        sqlStr.append("insert into \"" + tableName + "\" (");
                    }
                    StringBuffer sqlStr1 = new StringBuffer(") values (");
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
                                sqlStr1.append(keyValue.get(fieldName));
                            } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                if ("oracle".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else if ("dm".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else if ("kingbase".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                if ("oracle".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else if ("kingbase".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else if ("dm".equals(dialect)) {
                                    sqlStr1.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");

                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                }
                            } else {
                                if ("guid".equals(fieldName) || "GUID".equals(fieldName) || "Z_GUID".equals(fieldName) || "z_guid".equals(fieldName)) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if ("processInstanceId".equals(fieldName) || "PROCESSINSTANCEID".equals(fieldName)) {
                                    if (StringUtils.isBlank((String)keyValue.get(fieldName))) {
                                        sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                                    } else {
                                        sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    sqlStr1.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
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
                    StringBuffer sqlStr = new StringBuffer("");
                    if ("oracle".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");

                    } else if ("dm".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");

                    } else if ("mysql".equals(dialect)) {
                        sqlStr.append("update " + tableName + " set ");

                    } else if ("kingbase".equals(dialect)) {
                        sqlStr.append("update \"" + tableName + "\" set ");
                    }
                    StringBuffer sqlStr1 = new StringBuffer("");
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
                                if ("guid".equals(fieldName) || "GUID".equals(fieldName)) {
                                    sqlStr1.append(" where guid ='" + keyValue.get(fieldName) + "'");
                                    continue;
                                }
                                if (isHaveField) {
                                    sqlStr.append(",");
                                }
                                sqlStr.append(fieldName + "=");
                                if (y9TableField.getFieldType().toLowerCase().contains("int")) {
                                    sqlStr.append(keyValue.get(fieldName));
                                } else if (y9TableField.getFieldType().toLowerCase().contains("date")) {
                                    if ("oracle".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else if ("dm".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else if ("kingbase".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else if (y9TableField.getFieldType().toUpperCase().contains("TIMESTAMP")) {
                                    if ("oracle".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else if ("dm".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else if ("kingbase".equals(dialect)) {
                                        sqlStr.append("TO_DATE('" + keyValue.get(fieldName) + "','yyyy-MM-dd HH24:mi:ss')");
                                    } else {
                                        sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
                                    }
                                } else {
                                    sqlStr.append(StringUtils.isNotBlank((String)keyValue.get(fieldName)) ? "'" + keyValue.get(fieldName) + "'" : "''");
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
            map.put("msg", "保存成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveFormField(String formId, String fieldJson) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
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
                y9FormFieldRepository.save(formField);
            }
            resMap.put(UtilConsts.SUCCESS, true);
            resMap.put("msg", "保存字段成功");
        } catch (Exception e) {
            resMap.put(UtilConsts.SUCCESS, false);
            resMap.put("msg", "保存字段失败");
            e.printStackTrace();
        }
        return resMap;

    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveFormJson(String id, String formJson) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9Form form = y9FormRepository.findById(id).orElse(null);
            form.setFormJson(formJson);
            y9FormRepository.save(form);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        return map;

    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrUpdate(Y9Form form) {
        Map<String, Object> map = new HashMap<String, Object>(16);
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
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        return map;
    }

}
