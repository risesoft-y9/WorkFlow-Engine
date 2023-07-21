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
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.sqlddl.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "y9TableService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableServiceImpl implements Y9TableService {

    @Autowired
    private Y9TableRepository y9TableRepository;

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    @Autowired
    private TableManagerService tableManagerService;

    @Autowired
    private SpmApproveItemRepository approveItemRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> addDataBaseTable(String tableName, String systemName, String systemCnName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "操作成功");
        map.put(UtilConsts.SUCCESS, true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            Y9Table y9Table = new Y9Table();
            y9Table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9Table.setCreateTime(sdf.format(new Date()));
            y9Table.setOldTableName("");
            y9Table.setTableCnName(tableName);
            y9Table.setSystemName(systemName);
            y9Table.setSystemCnName(systemCnName);
            y9Table.setTableName(tableName);
            y9TableRepository.save(y9Table);
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            List<DbColumn> list = dbMetaDataUtil.listAllColumns(connection, tableName, "%");
            int order = 1;
            for (DbColumn dbColumn : list) {
                Y9TableField y9TableField = new Y9TableField();
                y9TableField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                y9TableField.setFieldCnName(StringUtils.isNotBlank(dbColumn.getComment()) ? dbColumn.getComment() : dbColumn.getColumnName());
                y9TableField.setFieldLength(dbColumn.getDataLength());
                y9TableField.setFieldType(dbColumn.getTypeName() + "(" + dbColumn.getDataLength() + ")");
                y9TableField.setIsMayNull(dbColumn.getNullable() ? 1 : 0);
                y9TableField.setState(1);
                y9TableField.setTableId(y9Table.getId());
                y9TableField.setTableName(tableName);
                y9TableField.setIsSystemField(dbColumn.getPrimaryKey() ? 1 : 0);
                y9TableField.setDisplayOrder(order);
                y9TableField.setFieldName(dbColumn.getColumnName());
                order += 1;
                y9TableFieldRepository.save(y9TableField);
            }
        } catch (Exception e) {
            map.put("msg", "操作失败");
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
    @Transactional(readOnly = false)
    public Map<String, Object> buildTable(Y9Table table, List<Map<String, Object>> listMap) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "创建失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            boolean tableExist = true;
            if (StringUtils.isEmpty(table.getId())) {
                tableExist = false;
            } else {
                Y9Table oldTable = this.findById(table.getId());
                if (null == oldTable) {
                    tableExist = false;
                }
            }
            Y9Table tableTemp = this.saveOrUpdate(table);
            if (tableTemp != null && tableTemp.getId() != null) {
                String tableId = tableTemp.getId();
                if (listMap.size() > 0) {
                    List<String> ids = new ArrayList<String>();
                    List<DbColumn> dbcs = new ArrayList<DbColumn>();
                    if (tableExist) {
                        List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
                        dbcs = saveField(tableId, tableTemp.getTableName(), listMap, ids);
                        for (Y9TableField y9TableField : list) {
                            if (!ids.contains(y9TableField.getId())) {
                                y9TableFieldRepository.delete(y9TableField);
                            }
                        }
                    } else {
                        dbcs = saveField(tableId, tableTemp.getTableName(), listMap, ids);
                    }
                    map = tableManagerService.buildTable(tableTemp, dbcs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "创建失败");
            map.put(UtilConsts.SUCCESS, false);
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
                y9TableRepository.deleteById(idTemp);
                y9TableFieldRepository.deleteByTableId(idTemp);
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
    public Y9Table findById(String id) {
        Y9Table c = y9TableRepository.findById(id).orElse(null);
        return c;
    }

    @Override
    public Y9Table findByTableName(String tableName) {
        return y9TableRepository.findByTableName(tableName);
    }

    @Override
    public List<Y9Table> findByTableType(Integer tableType) {
        return y9TableRepository.findByTableType(tableType);
    }

    @Override
    public List<Y9Table> getAllTable() {
        return y9TableRepository.findAll();
    }

    @Override
    public String getAlltableName() {
        StringBuilder tableNames = new StringBuilder();
        try {
            List<Y9Table> list = y9TableRepository.findAll();
            for (Y9Table y9Table : list) {
                if (tableNames.length() != 0) {
                    tableNames.append("," + y9Table.getTableName());
                } else {
                    tableNames.append(y9Table.getTableName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableNames.toString();
    }

    @Override
    public Map<String, Object> getAllTables(String name) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            if (StringUtils.isNotBlank(name)) {
                List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(connection, "%" + name + "%");
                for (Map<String, String> m : list1) {
                    if (m.get("name").startsWith("y9_form_") || m.get("name").startsWith("Y9_FORM_")) {
                        list.add(m);
                    }
                }
            } else {
                list = dbMetaDataUtil.listAllTables(connection, "y9_form_%");
                String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
                if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(connection, "Y9_FORM_%");
                    list.addAll(list1);
                } else if (DialectEnum.DM.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(connection, "Y9_FORM_%");
                    list.addAll(list1);
                }
            }
            map.put("rows", list);
        } catch (Exception e) {
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
    public List<Map<String, Object>> getAppList() {
        List<Map<String, Object>> tree = new ArrayList<Map<String, Object>>();
        List<SpmApproveItem> list = approveItemRepository.findAll();
        Map<String, Object> pNode = new HashMap<String, Object>(16);
        String parentId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        pNode.put("id", parentId);
        pNode.put("systemName", "");
        pNode.put("name", "系统列表");
        tree.add(pNode);
        for (SpmApproveItem approveItem : list) {
            pNode = new HashMap<String, Object>(16);
            String systemName = approveItem.getSystemName();
            String sysLevel = approveItem.getSysLevel();
            pNode.put("id", systemName);
            pNode.put("parentID", parentId);
            pNode.put("systemName", systemName);
            pNode.put("name", sysLevel);
            if (!tree.contains(pNode)) {
                tree.add(pNode);
            }
        }
        return tree;
    }

    /**
     * 解析字段类型，
     *
     * @param type
     * @return
     */
    private String getFieldType(String type) {
        type = type.substring(0, type.lastIndexOf("("));
        return type;
    }

    @Override
    public Map<String, Object> getTables(String systemName, int page, int rows) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page - 1, rows, sort);
        Page<Y9Table> pageList = null;
        if (StringUtils.isBlank(systemName)) {
            pageList = y9TableRepository.findAll(pageable);
        } else {
            pageList = y9TableRepository.findBySystemName(systemName, pageable);
        }
        List<Y9Table> list = pageList.getContent();
        Map<String, Object> map = tableManagerService.getDataSourceTableNames();
        for (Y9Table y9Table : list) {
            y9Table.setTableMemo("0");
            if (map.get(y9Table.getTableName().toLowerCase()) != null) {
                // 数据库是否存在物理表,1为是，0为否
                y9Table.setTableMemo("1");
            }
        }
        resMap.put("rows", list);
        resMap.put("currpage", page);
        resMap.put("totalpages", pageList.getTotalPages());
        resMap.put("total", pageList.getTotalElements());
        resMap.put(UtilConsts.SUCCESS, true);
        return resMap;
    }

    /**
     * 保存字段信息
     *
     * @param tableId
     * @param tableName
     * @param listMap
     * @param ids
     * @return
     */
    @Transactional(readOnly = false)
    public List<DbColumn> saveField(String tableId, String tableName, List<Map<String, Object>> listMap, List<String> ids) {
        List<DbColumn> dbcs = new ArrayList<DbColumn>();
        int order = 1;
        Y9TableField fieldTemp = null;
        for (Map<String, Object> m : listMap) {
            String id = (String)m.get("id");
            Integer isSystemField = (Integer)m.get("isSystemField");
            String fieldCnName = (String)m.get("fieldCnName");
            String fieldName = (String)m.get("fieldName");
            Integer fieldLength = (Integer)m.get("fieldLength");
            String fieldType = (String)m.get("fieldType");
            Integer isMayNull = (Integer)m.get("isMayNull");
            String oldFieldName = (String)m.get("oldFieldName");
            if (StringUtils.isEmpty(id)) {
                fieldTemp = new Y9TableField();
                fieldTemp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            } else {
                ids.add(id);
                fieldTemp = y9TableFieldRepository.findById(id).orElse(null);
                if (null == fieldTemp) {
                    fieldTemp = new Y9TableField();
                    fieldTemp.setId(id);
                }
            }
            fieldTemp.setFieldCnName(fieldCnName);
            fieldTemp.setFieldLength(fieldLength);
            fieldTemp.setFieldName(fieldName);
            fieldTemp.setFieldType(fieldType);
            fieldTemp.setIsMayNull(isMayNull);
            fieldTemp.setIsSystemField(isSystemField);
            fieldTemp.setOldFieldName(StringUtils.isBlank(oldFieldName) ? "" : oldFieldName);
            fieldTemp.setDisplayOrder(order);
            fieldTemp.setTableId(tableId);
            fieldTemp.setTableName(tableName);
            order += 1;

            DbColumn dbColumn = new DbColumn();
            dbColumn.setColumnName(fieldTemp.getFieldName());
            dbColumn.setIsPrimaryKey(fieldTemp.getIsSystemField());
            dbColumn.setPrimaryKey(fieldTemp.getIsSystemField() == 1 ? true : false);
            dbColumn.setNullable(fieldTemp.getIsMayNull() == 1 ? true : false);
            dbColumn.setTypeName(getFieldType(fieldTemp.getFieldType()));
            dbColumn.setDataLength(fieldTemp.getFieldLength());
            dbColumn.setComment(fieldTemp.getFieldCnName());
            dbColumn.setColumnNameOld(fieldTemp.getOldFieldName());
            dbColumn.setDataPrecision(0);
            dbColumn.setDataScale(0);
            dbColumn.setDataType(0);
            dbColumn.setIsNull(fieldTemp.getIsMayNull());
            dbColumn.setTableName(tableName);
            dbcs.add(dbColumn);
            y9TableFieldRepository.save(fieldTemp);
        }
        return dbcs;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Table saveOrUpdate(Y9Table table) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection connection = null;
        try {
            if (StringUtils.isBlank(table.getId())) {
                table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                table.setTableName(table.getTableName().toLowerCase());
            }
            table.setCreateTime(sdf.format(new Date()));
            return y9TableRepository.save(table);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Y9TableServiceImpl saveOrUpdate error");
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
    @Transactional(readOnly = false)
    public Map<String, Object> updateTable(Y9Table table, List<Map<String, Object>> listMap, String type) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "操作成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9Table savetable = this.saveOrUpdate(table);
            if (savetable != null && savetable.getId() != null) {
                String tableId = savetable.getId();
                String tableName = savetable.getTableName();
                if (listMap.size() > 0) {
                    List<String> ids = new ArrayList<String>();
                    List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
                    List<DbColumn> dbcs = new ArrayList<DbColumn>();
                    dbcs = saveField(tableId, tableName, listMap, ids);
                    /**
                     * 删除页面上已删除的字段
                     */
                    for (Y9TableField y9TableField : list) {
                        if (!ids.contains(y9TableField.getId())) {
                            y9TableFieldRepository.delete(y9TableField);
                        }
                    }
                    /**
                     * 修改表结构
                     */
                    boolean isSave = "save".equals(type);
                    if (!isSave) {
                        map = tableManagerService.addFieldToTable(savetable, dbcs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "操作失败");
            map.put(UtilConsts.SUCCESS, false);
        }

        return map;
    }
}
