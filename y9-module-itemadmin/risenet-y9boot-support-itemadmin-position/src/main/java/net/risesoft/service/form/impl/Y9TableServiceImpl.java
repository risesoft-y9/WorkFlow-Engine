package net.risesoft.service.form.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
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
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableServiceImpl implements Y9TableService {

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final Y9TableRepository y9TableRepository;

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final Y9FormFieldRepository y9FormFieldRepository;

    private final TableManagerService tableManagerService;

    private final SpmApproveItemRepository approveItemRepository;

    public Y9TableServiceImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        Y9TableRepository y9TableRepository, Y9TableFieldRepository y9TableFieldRepository,
        Y9FormFieldRepository y9FormFieldRepository, TableManagerService tableManagerService,
        SpmApproveItemRepository approveItemRepository) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9TableRepository = y9TableRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.tableManagerService = tableManagerService;
        this.approveItemRepository = approveItemRepository;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<Object> addDataBaseTable(String tableName, String systemName, String systemCnName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
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
            List<DbColumn> list = dbMetaDataUtil
                .listAllColumns(Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource()), tableName, "%");
            int order = 1;
            for (DbColumn dbColumn : list) {
                Y9TableField y9TableField = new Y9TableField();
                y9TableField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                y9TableField.setFieldCnName(
                    StringUtils.isNotBlank(dbColumn.getComment()) ? dbColumn.getComment() : dbColumn.getColumnName());
                y9TableField.setFieldLength(dbColumn.getDataLength());
                y9TableField.setFieldType(dbColumn.getTypeName() + "(" + dbColumn.getDataLength() + ")");
                y9TableField.setIsMayNull(Boolean.TRUE.equals(dbColumn.getNullable()) ? 1 : 0);
                y9TableField.setState(1);
                y9TableField.setTableId(y9Table.getId());
                y9TableField.setTableName(tableName);
                y9TableField.setIsSystemField(Boolean.TRUE.equals(dbColumn.getPrimaryKey()) ? 1 : 0);
                y9TableField.setDisplayOrder(order);
                y9TableField.setFieldName(dbColumn.getColumnName());
                order += 1;
                y9TableFieldRepository.save(y9TableField);
            }
            return Y9Result.successMsg("添加数据表成功");
        } catch (Exception e) {
            LOGGER.error("添加数据表失败", e);
            return Y9Result.failure("添加数据表失败");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<Object> buildTable(Y9Table table, List<Map<String, Object>> listMap) {
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
                if (!listMap.isEmpty()) {
                    List<String> ids = new ArrayList<>();
                    List<DbColumn> dbcs;
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
                    return tableManagerService.buildTable(tableTemp, dbcs);
                }
            }
            return Y9Result.successMsg("创建数据表成功");
        } catch (Exception e) {
            LOGGER.error("创建数据表失败", e);
            return Y9Result.failure("创建数据表失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> delete(String ids) {
        try {
            String[] id = ids.split(",");
            for (String idTemp : id) {
                y9TableRepository.deleteById(idTemp);
                y9TableFieldRepository.deleteByTableId(idTemp);
                y9FormFieldRepository.deleteByTableId(idTemp);
            }
            return Y9Result.successMsg("删除数据表成功");
        } catch (Exception e) {
            LOGGER.error("删除数据表失败", e);
            return Y9Result.failure("删除数据表失败");
        }
    }

    @Override
    public Y9Table findById(String id) {
        return y9TableRepository.findById(id).orElse(null);
    }

    @Override
    public Y9Table findByTableName(String tableName) {
        return y9TableRepository.findByTableName(tableName);
    }

    @Override
    public String getAlltableName() {
        StringBuilder tableNames = new StringBuilder();
        try {
            List<Y9Table> list = y9TableRepository.findAll();
            for (Y9Table y9Table : list) {
                if (tableNames.length() != 0) {
                    tableNames.append(",").append(y9Table.getTableName());
                } else {
                    tableNames.append(y9Table.getTableName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取所有表名失败", e);
        }
        return tableNames.toString();
    }

    @Override
    public List<Map<String, String>> getAllTables(String name) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        try {
            DataSource dataSource = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, String>> list = new ArrayList<>();
            if (StringUtils.isNotBlank(name)) {
                List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(dataSource, "%" + name + "%");
                for (Map<String, String> m : list1) {
                    if (m.get("name").startsWith("y9_form_") || m.get("name").startsWith("Y9_FORM_")) {
                        list.add(m);
                    }
                }
            } else {
                list = dbMetaDataUtil.listAllTables(dataSource, "y9_form_%");
                String dialect = dbMetaDataUtil.getDatabaseDialectName(dataSource);
                if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(dataSource, "Y9_FORM_%");
                    list.addAll(list1);
                } else if (DialectEnum.DM.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = dbMetaDataUtil.listAllTables(dataSource, "Y9_FORM_%");
                    list.addAll(list1);
                }
            }
            return list;
        } catch (Exception e) {
            LOGGER.error("获取所有表失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 解析字段类型，
     *
     * @param type
     * @return
     */
    private final String getFieldType(String type) {
        type = type.substring(0, type.lastIndexOf("("));
        return type;
    }

    @Override
    public List<Y9Table> listAllTable() {
        return y9TableRepository.findAll();
    }

    @Override
    public List<Map<String, Object>> listApps() {
        List<Map<String, Object>> tree = new ArrayList<>();
        List<SpmApproveItem> list = approveItemRepository.findAll();
        Map<String, Object> pNode = new HashMap<>(16);
        String parentId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        pNode.put("id", parentId);
        pNode.put("systemName", "");
        pNode.put("name", "系统列表");
        tree.add(pNode);
        for (SpmApproveItem approveItem : list) {
            pNode = new HashMap<>(16);
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

    @Override
    public List<Y9Table> listByTableType(Integer tableType) {
        return y9TableRepository.findByTableType(tableType);
    }

    @Override
    public Y9Page<Y9Table> pageTables(String systemName, int page, int rows) {
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
        List<Map<String, Object>> slist = approveItemRepository.getItemSystem();
        String systemCnName = "";
        for (Map<String, Object> m : slist) {
            if (m.get("systemName").equals(systemName)) {
                systemCnName = m.get("sysLevel").toString();
            }
        }
        for (Y9Table y9Table : list) {
            y9Table.setSystemCnName(systemCnName.equals("") ? y9Table.getSystemCnName() : systemCnName);
            y9Table.setTableMemo("0");
            if (map.get(y9Table.getTableName().toLowerCase()) != null) {
                // 数据库是否存在物理表,1为是，0为否
                y9Table.setTableMemo("1");
            }
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), list, "获取列表成功");
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
    @Transactional
    public List<DbColumn> saveField(String tableId, String tableName, List<Map<String, Object>> listMap,
        List<String> ids) {
        List<DbColumn> dbcs = new ArrayList<>();
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
            dbColumn.setPrimaryKey(fieldTemp.getIsSystemField() == 1);
            dbColumn.setNullable(fieldTemp.getIsMayNull() == 1);
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
    @Transactional
    public Y9Table saveOrUpdate(Y9Table table) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isBlank(table.getId())) {
                table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            DataSource dataSource = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource());
            String dialect = dbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                table.setTableName(table.getTableName().toLowerCase());
            }
            table.setCreateTime(sdf.format(new Date()));
            return y9TableRepository.save(table);
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            throw new Exception("Y9TableServiceImpl saveOrUpdate error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<Object> updateTable(Y9Table table, List<Map<String, Object>> listMap, String type) {
        try {
            Y9Table savetable = this.saveOrUpdate(table);
            if (savetable != null && savetable.getId() != null) {
                String tableId = savetable.getId();
                String tableName = savetable.getTableName();
                if (!listMap.isEmpty()) {
                    List<String> ids = new ArrayList<>();
                    List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
                    List<DbColumn> dbcs = new ArrayList<>();
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
                        return tableManagerService.addFieldToTable(savetable, dbcs);
                    }
                }
            }
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            LOGGER.error("操作失败", e);
            return Y9Result.failure("操作失败");
        }
    }
}
