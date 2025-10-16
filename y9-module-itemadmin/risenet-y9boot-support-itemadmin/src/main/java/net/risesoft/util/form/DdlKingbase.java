package net.risesoft.util.form;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public class DdlKingbase {

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    public DdlKingbase() {
        this.y9TableFieldRepository = Y9Context.getBean(Y9TableFieldRepository.class);
    }

    public void addTableColumn(DataSource dataSource, String tableName, List<DbColumn> dbColumnList) throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            return;
        }
        for (DbColumn dbc : dbColumnList) {
            if (shouldSkipColumn(dbc.getColumnName())) {
                continue;
            }
            processColumnAddition(dataSource, tableName, dbc);
        }
    }

    private boolean shouldSkipColumn(String columnName) {
        return "GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName);
    }

    private void processColumnAddition(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        ColumnInfo columnInfo = getColumnInfo(dataSource, tableName, dbc);
        String typeDdl = buildColumnTypeDdl(dataSource, tableName, dbc, columnInfo);
        DbMetaDataUtil.executeDdl(dataSource, typeDdl);
        handleNullableConstraint(dataSource, tableName, dbc, columnInfo);
        handleColumnComment(dataSource, tableName, dbc);
        y9TableFieldRepository.updateOldFieldName(dbc.getTableName(), dbc.getColumnName());
    }

    private ColumnInfo getColumnInfo(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        ColumnInfo info = new ColumnInfo();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableSchema = databaseMetaData.getUserName().toUpperCase();
            try (ResultSet rs =
                databaseMetaData.getColumns(null, tableSchema, tableName, dbc.getColumnName().toUpperCase())) {
                while (rs.next()) {
                    info.nullable = rs.getString("is_nullable");
                    info.dbColumnName = rs.getString("column_name".toLowerCase());
                }
            }
        }
        return info;
    }

    private String buildColumnTypeDdl(DataSource dataSource, String tableName, DbColumn dbc, ColumnInfo columnInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE \"").append(tableName).append("\"");
        determineColumnTypeOperation(dataSource, sb, dbc, columnInfo);
        appendColumnTypeDefinition(sb, dbc);
        return sb.toString();
    }

    private void determineColumnTypeOperation(DataSource dataSource, StringBuilder sb, DbColumn dbc,
        ColumnInfo columnInfo) {
        // 不存在旧字段则新增
        if ("".equals(columnInfo.dbColumnName)
            && org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
            sb.append(" ADD ").append(dbc.getColumnName()).append(" ");
            columnInfo.isAddOperation = true;
        } else {
            // 存在旧字段，字段名称没有改变则修改属性
            if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())
                || org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                sb.append(" ALTER COLUMN ").append(dbc.getColumnName()).append(" TYPE ");
            } else {
                // 存在旧字段，字段名称改变则先重命名再修改属性
                handleColumnRename(dataSource, dbc.getTableName(), dbc.getColumnNameOld(), dbc.getColumnName());
                sb.append(" ALTER COLUMN ").append(dbc.getColumnName()).append(" TYPE ");
            }
        }
    }

    private void handleColumnRename(DataSource dataSource, String tableName, String oldName, String newName) {
        try {
            StringBuilder renameSql = new StringBuilder();
            renameSql.append("ALTER TABLE \"").append(tableName).append("\"");
            DbMetaDataUtil.executeDdl(dataSource,
                renameSql.append(" RENAME COLUMN ").append(oldName).append(" TO ").append(newName).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendColumnTypeDefinition(StringBuilder sb, DbColumn dbc) {
        String sType = dbc.getTypeName().toUpperCase();
        if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType) || "NVARCHAR2".equals(sType)
            || "RAW".equals(sType)) {
            sb.append(sType).append("(").append(dbc.getDataLength()).append(" char)");
        } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
            || "NUMBER".equalsIgnoreCase(sType)) {
            if (dbc.getDataScale() == null) {
                sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
            } else {
                sb.append(sType)
                    .append("(")
                    .append(dbc.getDataLength())
                    .append(",")
                    .append(dbc.getDataScale())
                    .append(")");
            }
        } else {
            sb.append(sType);
        }
    }

    private void handleNullableConstraint(DataSource dataSource, String tableName, DbColumn dbc, ColumnInfo columnInfo)
        throws Exception {
        // 新增字段
        if (columnInfo.isAddOperation) {
            if (!dbc.getNullable()) {
                String sb =
                    "ALTER TABLE \"" + tableName + "\"" + " ALTER COLUMN " + dbc.getColumnName() + " SET NOT NULL";
                DbMetaDataUtil.executeDdl(dataSource, sb);
            }
        } else {
            // 修改字段
            handleExistingColumnNullable(dataSource, tableName, dbc, columnInfo);
        }
    }

    private void handleExistingColumnNullable(DataSource dataSource, String tableName, DbColumn dbc,
        ColumnInfo columnInfo) throws Exception {
        if (dbc.getNullable() && "NO".equals(columnInfo.nullable)) {
            String sb = "ALTER TABLE \"" + tableName + "\"" + " ALTER COLUMN " + dbc.getColumnName() + " DROP NOT NULL";
            DbMetaDataUtil.executeDdl(dataSource, sb);
        }
        if (!dbc.getNullable() && "YES".equals(columnInfo.nullable)) {
            String sb = "ALTER TABLE \"" + tableName + "\"" + " ALTER COLUMN " + dbc.getColumnName() + " SET NOT NULL";
            DbMetaDataUtil.executeDdl(dataSource, sb);
        }
    }

    private void handleColumnComment(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        if (StringUtils.hasText(dbc.getComment())) {
            DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
        }
    }

    public void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }
        DbColumn[] dbColumnArr = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbColumnArr) {
            if (StringUtils.hasText(dbc.getColumnNameOld())) {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE \"").append(tableName).append("\"");
                // 字段名称有改变
                if (!dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                    try {
                        DbMetaDataUtil.executeDdl(dataSource,
                            sb.append(" RENAME COLUMN ")
                                .append(dbc.getColumnNameOld())
                                .append(" TO ")
                                .append(dbc.getColumnName())
                                .toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.append(" MODIFY ").append(dbc.getColumnName()).append(" ");
                String sType = dbc.getTypeName().toUpperCase();
                if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType)
                    || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                    sb.append(sType).append("(").append(dbc.getDataLength()).append(" char)");
                } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
                    || "NUMBER".equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
                    } else {
                        sb.append(sType)
                            .append("(")
                            .append(dbc.getDataLength())
                            .append(",")
                            .append(dbc.getDataScale())
                            .append(")");
                    }
                } else {
                    sb.append(sType);
                }
                List<DbColumn> list = DbMetaDataUtil.listAllColumns(dataSource, tableName, dbc.getColumnNameOld());
                if (dbc.getNullable()) {
                    if (!list.get(0).getNullable()) {
                        sb.append(" NULL");
                    }
                } else {
                    if (list.get(0).getNullable()) {
                        sb.append(" NOT NULL");
                    }
                }
                DbMetaDataUtil.executeDdl(dataSource, sb.toString());
                if (StringUtils.hasText(dbc.getComment())) {
                    if (!list.get(0).getComment().equals(dbc.getComment())) {
                        DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                            + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                    }
                }
            }
        }
    }

    public void createTable(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        DbColumn[] dbColumnArr = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));

        String createTableSql = buildCreateTableSql(tableName, dbColumnArr);
        DbMetaDataUtil.executeDdl(dataSource, createTableSql);

        handleTableComments(dataSource, tableName, dbColumnArr);
    }

    private String buildCreateTableSql(String tableName, DbColumn[] dbColumnArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE \"")
            .append(tableName)
            .append("\" (\r\n")
            .append("GUID varchar2(38 char) NOT NULL, \r\n");

        for (DbColumn dbc : dbColumnArr) {
            if (shouldSkipColumn(dbc.getColumnName())) {
                continue;
            }

            appendColumnDefinition(sb, dbc);
            sb.append(",\r\n");
        }

        sb.append("PRIMARY KEY (GUID) \r\n").append(")");
        return sb.toString();
    }

    private void appendColumnDefinition(StringBuilder sb, DbColumn dbc) {
        sb.append(dbc.getColumnName()).append(" ");
        appendColumnTypeDefinition(sb, dbc);

        if (!dbc.getNullable()) {
            sb.append(" NOT NULL");
        }
    }

    private void handleTableComments(DataSource dataSource, String tableName, DbColumn[] dbColumnArr) throws Exception {
        for (DbColumn dbc : dbColumnArr) {
            if (StringUtils.hasText(dbc.getComment())) {
                String commentSql = "COMMENT ON COLUMN \"" + tableName + "\"."
                    + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'";
                DbMetaDataUtil.executeDdl(dataSource, commentSql);
            }
        }
    }

    public void dropTable(DataSource dataSource, String tableName) throws Exception {
        if (DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            DbMetaDataUtil.executeDdl(dataSource, "DROP TABLE \"" + tableName + "\"");
        }
    }

    public void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
        DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE \"" + tableName + "\" DROP COLUMN " + columnName);
    }

    public void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
        DbMetaDataUtil.executeDdl(dataSource, "RENAME \"" + tableNameOld + "\" TO \"" + tableNameNew + "\"");
    }

    // 内部类用于封装列信息
    private static class ColumnInfo {
        String nullable = "";
        String dbColumnName = "";
        boolean isAddOperation = false;
    }
}
