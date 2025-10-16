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
public class DdlOracle {

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    public DdlOracle() {
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
        String ddl = buildAlterTableDdl(dataSource, tableName, dbc, columnInfo);
        DbMetaDataUtil.executeDdl(dataSource, ddl);
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

    private String buildAlterTableDdl(DataSource dataSource, String tableName, DbColumn dbc, ColumnInfo columnInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE \"").append(tableName).append("\"");
        boolean isAddOperation = determineOperationType(dataSource, sb, dbc, columnInfo);
        appendColumnType(sb, dbc);
        appendNullableConstraint(sb, dbc, columnInfo, isAddOperation);
        return sb.toString();
    }

    private boolean determineOperationType(DataSource dataSource, StringBuilder sb, DbColumn dbc,
        ColumnInfo columnInfo) {
        boolean isAdd = false;
        // 不存在旧字段则新增
        if ("".equals(columnInfo.dbColumnName)
            && org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
            sb.append(" ADD ").append(dbc.getColumnName()).append(" ");
            isAdd = true;
        } else {
            // 存在旧字段，字段名称没有改变则修改属性
            if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())
                || org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                sb.append(" MODIFY ").append(dbc.getColumnName()).append(" ");
            } else {
                // 存在旧字段，字段名称改变则修改字段名称及属性
                handleColumnRename(dataSource, dbc.getTableName(), dbc.getColumnNameOld(), dbc.getColumnName());
                // 重命名之后再MODIFY
                sb.append(" MODIFY ").append(dbc.getColumnName()).append(" ");
            }
        }
        return isAdd;
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

    private void appendNullableConstraint(StringBuilder sb, DbColumn dbc, ColumnInfo columnInfo,
        boolean isAddOperation) {
        if (isAddOperation) {
            // 新增字段
            if (dbc.getNullable()) {
                sb.append(" NULL");
            } else {
                sb.append(" NOT NULL");
            }
        } else {
            // 修改字段
            if (dbc.getNullable() && "NO".equals(columnInfo.nullable)) {
                sb.append(" NULL");
            }
            if (!dbc.getNullable() && "YES".equals(columnInfo.nullable)) {
                sb.append(" NOT NULL");
            }
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
                processColumnAlteration(dataSource, tableName, dbc);
            }
        }
    }

    private void processColumnAlteration(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        // 处理字段重命名
        handleColumnRename(dataSource, tableName, dbc);
        // 构建MODIFY语句
        StringBuilder modifySql = buildModifyStatement(dataSource, tableName, dbc);
        // 执行MODIFY语句
        DbMetaDataUtil.executeDdl(dataSource, modifySql.toString());
        // 处理注释更新
        handleCommentUpdate(dataSource, tableName, dbc);
    }

    private void handleColumnRename(DataSource dataSource, String tableName, DbColumn dbc) {
        if (!dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
            try {
                String renameSql = "ALTER TABLE \"" + tableName + "\"" + " RENAME COLUMN " + dbc.getColumnNameOld()
                    + " TO " + dbc.getColumnName();
                DbMetaDataUtil.executeDdl(dataSource, renameSql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private StringBuilder buildModifyStatement(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE \"").append(tableName).append("\"");
        sb.append(" MODIFY ").append(dbc.getColumnName()).append(" ");
        appendColumnType(sb, dbc);
        appendNullableConstraint(dataSource, tableName, sb, dbc);
        return sb;
    }

    private void appendColumnType(StringBuilder sb, DbColumn dbc) {
        String sType = dbc.getTypeName().toUpperCase();
        if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType) || "NVARCHAR2".equals(sType)
            || "RAW".equals(sType)) {
            sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
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

    private void appendNullableConstraint(DataSource dataSource, String tableName, StringBuilder sb, DbColumn dbc)
        throws Exception {
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
    }

    private void handleCommentUpdate(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        if (StringUtils.hasText(dbc.getComment())) {
            List<DbColumn> list = DbMetaDataUtil.listAllColumns(dataSource, tableName, dbc.getColumnNameOld());
            if (!list.get(0).getComment().equals(dbc.getComment())) {
                String commentSql = "COMMENT ON COLUMN \"" + tableName + "\"."
                    + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'";
                DbMetaDataUtil.executeDdl(dataSource, commentSql);
            }
        }
    }

    public void createTable(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        StringBuilder sb = new StringBuilder();
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        //@formatter:off
        sb.append("CREATE TABLE \"").append(tableName).append("\" (\r\n").append("GUID varchar2(38) NOT NULL, \r\n");
        //@formatter:off
        for (DbColumn dbc : dbcs) {
            String columnName = dbc.getColumnName();
            if ("GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName)) {
                continue;
            }
            sb.append(columnName).append(" ");
            String sType = dbc.getTypeName().toUpperCase();
            if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType) || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
            } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType) || "NUMBER".equalsIgnoreCase(sType)) {
                if (dbc.getDataScale() == null) {
                    sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
                } else {
                    sb.append(sType).append("(").append(dbc.getDataLength()).append(",").append(dbc.getDataScale()).append(")");
                }
            } else {
                sb.append(sType);
            }

            if (!dbc.getNullable()) {
                sb.append(" NOT NULL");
            }
            sb.append(",\r\n");
        }
        sb.append("PRIMARY KEY (GUID) \r\n").append(")");
        DbMetaDataUtil.executeDdl(dataSource, sb.toString());

        for (DbColumn dbc : dbcs) {
            if (StringUtils.hasText(dbc.getComment())) {
                DbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"." + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
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
    }
}
