package net.risesoft.service.form.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Item;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.service.ItemWorkDayService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableServiceImpl implements Y9TableService {

    private static final String AND = " AND ";

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final Y9TableRepository y9TableRepository;

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final Y9FormFieldRepository y9FormFieldRepository;

    private final TableManagerService tableManagerService;

    private final ItemService itemService;

    private final ItemWorkDayService itemWorkDayService;

    private final Y9TableService self;

    public Y9TableServiceImpl(
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        Y9TableRepository y9TableRepository,
        Y9TableFieldRepository y9TableFieldRepository,
        Y9FormFieldRepository y9FormFieldRepository,
        TableManagerService tableManagerService,
        ItemService itemService,
        ItemWorkDayService itemWorkDayService,
        @Lazy Y9TableService self) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9TableRepository = y9TableRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
        this.y9FormFieldRepository = y9FormFieldRepository;
        this.tableManagerService = tableManagerService;
        this.itemService = itemService;
        this.itemWorkDayService = itemWorkDayService;
        this.self = self;
    }

    @Override
    @Transactional
    public Y9Result<Object> addDataBaseTable(String tableName, String systemName, String systemCnName) {
        try {
            Y9Table y9Table = new Y9Table();
            y9Table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            y9Table.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            y9Table.setOldTableName("");
            y9Table.setTableCnName(tableName);
            y9Table.setSystemName(systemName);
            y9Table.setSystemCnName(systemCnName);
            y9Table.setTableName(tableName);
            y9TableRepository.save(y9Table);
            List<DbColumn> list = DbMetaDataUtil
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
    @Transactional
    public Y9Result<Object> buildTable(Y9Table table, List<Map<String, Object>> listMap) {
        try {
            // 检查表是否已存在
            boolean tableExist = checkTableExists(table);
            // 保存或更新表信息
            Y9Table tableTemp = self.saveOrUpdate(table);
            if (tableTemp == null || StringUtils.isBlank(tableTemp.getId())) {
                return Y9Result.failure("创建数据表失败：无法保存表信息");
            }
            // 处理字段信息
            if (!listMap.isEmpty()) {
                List<String> ids = new ArrayList<>();
                List<DbColumn> dbColumns = self.saveField(tableTemp.getId(), tableTemp.getTableName(), listMap, ids);
                // 如果表已存在，删除已移除的字段
                if (tableExist) {
                    cleanupRemovedFields(tableTemp.getId(), ids);
                }
                // 构建物理表
                return tableManagerService.buildTable(tableTemp, dbColumns);
            }
            return Y9Result.successMsg("创建数据表成功");
        } catch (Exception e) {
            LOGGER.error("创建数据表失败", e);
            return Y9Result.failure("创建数据表失败：" + e.getMessage());
        }
    }

    /**
     * 检查表是否已存在
     */
    private boolean checkTableExists(Y9Table table) {
        if (StringUtils.isEmpty(table.getId())) {
            return false;
        }
        Y9Table oldTable = this.findById(table.getId());
        return oldTable != null;
    }

    /**
     * 清理已移除的字段
     */
    private void cleanupRemovedFields(String tableId, List<String> ids) {
        List<Y9TableField> existingFields = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
        for (Y9TableField y9TableField : existingFields) {
            if (!ids.contains(y9TableField.getId())) {
                y9TableFieldRepository.delete(y9TableField);
            }
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
    public Y9Table findByTableAlias(String tableAlias) {
        return y9TableRepository.findByTableAlias(tableAlias);
    }

    @Override
    public List<String> getSql(Map<String, Object> searchMap) {
        SqlBuilderContext context = new SqlBuilderContext();
        for (String key : searchMap.keySet()) {
            if (key.contains(".")) {
                handleTableFieldCondition(key, searchMap, context);
            } else {
                handleProcessCondition(key, searchMap, context);
            }
        }
        return List.of(context.innerSql.toString(), context.whereSql.toString(),
            context.assigneeNameInnerSql.toString(), context.assigneeNameWhereSql.toString());
    }

    /**
     * 处理表字段查询条件
     */
    private void handleTableFieldCondition(String key, Map<String, Object> searchMap, SqlBuilderContext context) {
        String[] aliasColumnNameType = key.split("\\.");
        String alias = aliasColumnNameType[0];
        // 添加表连接
        addTableJoin(alias, context);
        // 根据参数格式处理不同查询条件
        switch (aliasColumnNameType.length) {
            case 2:
                handleTwoPartCondition(key, aliasColumnNameType, searchMap, context);
                break;
            case 3:
                handleThreePartCondition(key, aliasColumnNameType, searchMap, context);
                break;
            default:
                LOGGER.warn("参数不符合a.b或者a.b.c的格式。");
                break;
        }
    }

    /**
     * 添加表连接语句
     */
    private void addTableJoin(String alias, SqlBuilderContext context) {
        if (!context.tableAliasList.contains(alias)) {
            context.tableAliasList.add(alias);
            Y9Table y9Table = this.findByTableAlias(alias);
            if (y9Table == null) {
                throw new IllegalArgumentException("找不到表别名对应的表: " + alias);
            }
            context.innerSql.append("INNER JOIN ")
                .append(y9Table.getTableName().toUpperCase())
                .append(" ")
                .append(alias.toUpperCase())
                .append(" ON T.PROCESSSERIALNUMBER = ")
                .append(alias.toUpperCase())
                .append(".GUID ");
        }
    }

    /**
     * 处理两部分参数格式的条件 (a.b)
     */
    private void handleTwoPartCondition(String key, String[] aliasColumnNameType, Map<String, Object> searchMap,
        SqlBuilderContext context) {
        if ("dbsx".equalsIgnoreCase(aliasColumnNameType[1])) {
            handleDeadlineCondition(key, searchMap, context);
        } else {
            handleLikeCondition(key, searchMap, context);
        }
    }

    /**
     * 处理截止日期条件
     */
    private void handleDeadlineCondition(String key, Map<String, Object> searchMap, SqlBuilderContext context) {
        int days = Integer.parseInt(searchMap.get(key).toString());
        List<String> startEnd = itemWorkDayService.getDb(days);
        context.whereSql.append(AND)
            .append(key.toUpperCase())
            .append(" >='")
            .append(startEnd.get(0))
            .append("'")
            .append(AND)
            .append(key.toUpperCase())
            .append(" <='")
            .append(startEnd.get(1))
            .append("'");
    }

    /**
     * 处理模糊查询条件
     */
    private void handleLikeCondition(String key, Map<String, Object> searchMap, SqlBuilderContext context) {
        Object value = searchMap.get(key);
        if (value != null && StringUtils.isNotBlank(value.toString())) {
            context.whereSql.append(AND).append(key.toUpperCase()).append(" LIKE '%").append(value).append("%'");
        } else {
            context.whereSql.append(AND)
                .append("(")
                .append(key.toUpperCase())
                .append("= '' OR ")
                .append(key.toUpperCase())
                .append(" IS NULL)");
        }
    }

    /**
     * 处理三部分参数格式的条件 (a.b.c)
     */
    private void handleThreePartCondition(String key, String[] aliasColumnNameType, Map<String, Object> searchMap,
        SqlBuilderContext context) {
        String aliasColumnName = aliasColumnNameType[0] + "." + aliasColumnNameType[1];
        String type = aliasColumnNameType[2];
        if ("equal".equals(type)) {
            context.whereSql.append(AND)
                .append(aliasColumnName.toUpperCase())
                .append("='")
                .append(searchMap.get(key).toString())
                .append("' ");
        } else if ("date".equals(type)) {
            handleDateRangeCondition(aliasColumnName, searchMap, context);
        }
    }

    /**
     * 处理日期范围条件
     */
    private void handleDateRangeCondition(String aliasColumnName, Map<String, Object> searchMap,
        SqlBuilderContext context) {
        @SuppressWarnings("unchecked")
        ArrayList<String> list = (ArrayList<String>)searchMap.get(aliasColumnName);
        context.whereSql.append(AND)
            .append(aliasColumnName.toUpperCase())
            .append(" >='")
            .append(list.get(0))
            .append("'")
            .append(AND)
            .append(aliasColumnName.toUpperCase())
            .append(" <='")
            .append(list.get(1))
            .append("'");
    }

    /**
     * 处理流程相关查询条件
     */
    private void handleProcessCondition(String key, Map<String, Object> searchMap, SqlBuilderContext context) {
        if ("ended".equals(key)) {
            context.whereSql.append(AND)
                .append("T.")
                .append(key.toUpperCase())
                .append("=")
                .append((boolean)searchMap.get(key));
        } else if ("assigneeName".equals(key)) {
            context.assigneeNameInnerSql
                .append(" JOIN FF_ACT_RU_DETAIL TT ON T.PROCESSSERIALNUMBER = TT.PROCESSSERIALNUMBER");
            context.assigneeNameWhereSql.append(AND)
                .append("INSTR(TT.")
                .append(key.toUpperCase())
                .append(",'")
                .append(searchMap.get(key).toString())
                .append("') > 0 ")
                .append(AND)
                .append("TT.STATUS = 0 ");
        }
    }

    @Override
    public List<Y9Table> getAllTable() {
        return y9TableRepository.findAll();
    }

    @Override
    public List<Map<String, String>> getAllTables(String name) {
        try {
            DataSource dataSource = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource());
            List<Map<String, String>> list = new ArrayList<>();
            if (StringUtils.isNotBlank(name)) {
                List<Map<String, String>> list1 = DbMetaDataUtil.listAllTables(dataSource, "%" + name + "%");
                for (Map<String, String> m : list1) {
                    if (m.get("name").startsWith("y9_form_") || m.get("name").startsWith("Y9_FORM_")) {
                        list.add(m);
                    }
                }
            } else {
                list = DbMetaDataUtil.listAllTables(dataSource, "y9_form_%");
                String dialect = DbMetaDataUtil.getDatabaseDialectName(dataSource);
                if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = DbMetaDataUtil.listAllTables(dataSource, "Y9_FORM_%");
                    list.addAll(list1);
                } else if (DialectEnum.DM.getValue().equals(dialect)) {
                    List<Map<String, String>> list1 = DbMetaDataUtil.listAllTables(dataSource, "Y9_FORM_%");
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
     * @param type 字段类型
     * @return 字段类型
     */
    private String getFieldType(String type) {
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
        List<Item> list = itemService.list();
        Map<String, Object> pNode = new HashMap<>(16);
        String parentId = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        pNode.put("id", parentId);
        pNode.put("systemName", "");
        pNode.put("name", "系统列表");
        tree.add(pNode);
        for (Item item : list) {
            pNode = new HashMap<>(16);
            String systemName = item.getSystemName();
            String sysLevel = item.getSysLevel();
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
        Page<Y9Table> pageList;
        if (StringUtils.isBlank(systemName)) {
            pageList = y9TableRepository.findAll(pageable);
        } else {
            pageList = y9TableRepository.findBySystemName(systemName, pageable);
        }
        List<Y9Table> list = pageList.getContent();
        Map<String, Object> map = tableManagerService.getDataSourceTableNames();
        List<ItemSystemListModel> systemList = itemService.getItemSystem();
        systemList = systemList.stream()
            .filter(system -> system.getSystemName().equals(systemName))
            .collect(Collectors.toList());
        Optional<ItemSystemListModel> optional =
            systemList.stream().filter(system -> system.getSystemName().equals(systemName)).findFirst();
        String systemCnName = optional.isPresent() ? optional.get().getSysLevel() : "";
        for (Y9Table y9Table : list) {
            y9Table.setSystemCnName(systemCnName.isEmpty() ? y9Table.getSystemCnName() : systemCnName);
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
     * @param tableId 表ID
     * @param tableName 表名
     * @param listMap 字段信息
     * @param ids 字段ID
     * @return {@code List<DbColumn>}
     */
    @Override
    @Transactional
    public List<DbColumn> saveField(String tableId, String tableName, List<Map<String, Object>> listMap,
        List<String> ids) {
        List<DbColumn> dbColumns = new ArrayList<>();
        int order = 1;
        Y9TableField fieldTemp;
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
            dbColumns.add(dbColumn);
            y9TableFieldRepository.save(fieldTemp);
        }
        return dbColumns;
    }

    @Override
    @Transactional
    public Y9Table saveOrUpdate(Y9Table table) throws Exception {
        try {
            if (StringUtils.isBlank(table.getId())) {
                table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            DataSource dataSource = Objects.requireNonNull(jdbcTemplate4Tenant.getDataSource());
            String dialect = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                table.setTableName(table.getTableName().toLowerCase());
            }
            table.setCreateTime(Y9DateTimeUtils.formatCurrentDateTime());
            return y9TableRepository.save(table);
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            throw new Exception("Y9TableServiceImpl saveOrUpdate error");
        }
    }

    @Override
    @Transactional
    public Y9Result<Object> updateTable(Y9Table table, List<Map<String, Object>> listMap, String type) {
        try {
            Y9Table saveTable = self.saveOrUpdate(table);
            if (saveTable != null && saveTable.getId() != null) {
                String tableId = saveTable.getId();
                String tableName = saveTable.getTableName();
                if (!listMap.isEmpty()) {
                    List<String> ids = new ArrayList<>();
                    List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
                    List<DbColumn> dbColumnList = self.saveField(tableId, tableName, listMap, ids);
                    // 删除页面上已删除的字段
                    for (Y9TableField y9TableField : list) {
                        if (!ids.contains(y9TableField.getId())) {
                            y9TableFieldRepository.delete(y9TableField);
                        }
                    }
                    // 修改表结构
                    boolean isSave = "save".equals(type);
                    if (!isSave) {
                        return tableManagerService.addFieldToTable(saveTable, dbColumnList);
                    }
                }
            }
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            LOGGER.error("操作失败", e);
            return Y9Result.failure("操作失败");
        }
    }

    /**
     * SQL构建上下文
     */
    private static class SqlBuilderContext {
        final StringBuilder innerSql = new StringBuilder();
        final StringBuilder whereSql = new StringBuilder();
        final StringBuilder assigneeNameInnerSql = new StringBuilder();
        final StringBuilder assigneeNameWhereSql = new StringBuilder();
        final List<String> tableAliasList = new ArrayList<>();
    }
}
