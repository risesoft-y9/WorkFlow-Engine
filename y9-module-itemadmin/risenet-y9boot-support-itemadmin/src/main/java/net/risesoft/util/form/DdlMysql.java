package net.risesoft.util.form;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
public class DdlMysql {

    private static final String NOT_NULL_KEY = " NOT NULL";
    private static final String COMMENT_KEY = " COMMENT '";

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    public DdlMysql() {
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
            String ddl = buildAlterTableDdl(dataSource, tableName, dbc);
            DbMetaDataUtil.executeDdl(dataSource, ddl);
            y9TableFieldRepository.updateOldFieldName(dbc.getTableName(), dbc.getColumnName());
        }
    }

    private boolean shouldSkipColumn(String columnName) {
        return "guid".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName);
    }

    private String buildAlterTableDdl(DataSource dataSource, String tableName, DbColumn dbc) throws Exception {
        String dbColumnName = getExistingColumnName(dataSource, tableName, dbc.getColumnName());
        StringBuilder ddlBuilder = new StringBuilder("ALTER TABLE ").append(tableName);
        // 确定操作类型
        String operation = determineOperation(dbColumnName, dbc);
        ddlBuilder.append(operation).append(dbc.getColumnName()).append(" ");
        // 添加字段类型定义
        appendColumnType(ddlBuilder, dbc);
        // 添加约束和注释
        appendColumnConstraints(ddlBuilder, dbc);
        return ddlBuilder.toString();
    }

    private String getExistingColumnName(DataSource dataSource, String tableName, String columnName) throws Exception {
        String dbColumnName = "";
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String tableSchema = databaseMetaData.getUserName().toUpperCase();
            try (ResultSet rs = databaseMetaData.getColumns(null, tableSchema, tableName, columnName)) {
                while (rs.next()) {
                    dbColumnName = rs.getString("column_name".toLowerCase());
                }
            }
        }
        return dbColumnName;
    }

    private String determineOperation(String dbColumnName, DbColumn dbc) {
        if ("".equals(dbColumnName) && StringUtils.isBlank(dbc.getColumnNameOld())) {
            return " ADD COLUMN ";
        } else if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())
            || StringUtils.isBlank(dbc.getColumnNameOld())) {
            return " MODIFY COLUMN ";
        } else {
            return " CHANGE COLUMN " + dbc.getColumnNameOld() + " ";
        }
    }

    private void appendColumnType(StringBuilder ddlBuilder, DbColumn dbc) {
        String sType = dbc.getTypeName().toUpperCase();
        if ("CHAR".equals(sType) || "VARCHAR".equals(sType)) {
            ddlBuilder.append(sType).append("(").append(dbc.getDataLength()).append(")");
        } else if ("DECIMAL".equals(sType) || "NUMERIC".equals(sType)) {
            if (dbc.getDataScale() == null) {
                ddlBuilder.append(sType).append("(").append(dbc.getDataLength()).append(")");
            } else {
                ddlBuilder.append(sType)
                    .append("(")
                    .append(dbc.getDataLength())
                    .append(",")
                    .append(dbc.getDataScale())
                    .append(")");
            }
        } else {
            ddlBuilder.append(sType);
        }
    }

    private void appendColumnConstraints(StringBuilder ddlBuilder, DbColumn dbc) {
        if (dbc.getNullable()) {
            ddlBuilder.append(" DEFAULT NULL");
        } else {
            ddlBuilder.append(NOT_NULL_KEY);
        }
        if (!dbc.getComment().isEmpty()) {
            ddlBuilder.append(COMMENT_KEY).append(dbc.getComment()).append("'");
        }
    }

    public void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        if (!DbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }
        DbColumn[] dbColumnArr = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbColumnArr) {
            String ddl = "ALTER TABLE " + tableName;
            // 字段名称没有改变
            if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                ddl += " MODIFY COLUMN " + dbc.getColumnName() + " ";
            } else {
                ddl += " CHANGE COLUMN " + dbc.getColumnNameOld() + " " + dbc.getColumnName() + " ";
            }
            String sType = dbc.getTypeName().toUpperCase();
            if ("CHAR".equals(sType) || "VARCHAR".equals(sType)) {
                ddl += sType + "(" + dbc.getDataLength() + ")";
            } else if ("DECIMAL".equals(sType) || "NUMERIC".equals(sType)) {
                if (dbc.getDataScale() == null) {
                    ddl += sType + "(" + dbc.getDataLength() + ")";
                } else {
                    ddl += sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")";
                }
            } else {
                ddl += sType;
            }

            if (dbc.getNullable()) {
                ddl += " DEFAULT NULL";
            } else {
                ddl += NOT_NULL_KEY;
            }
            if (!dbc.getComment().isEmpty()) {
                ddl += COMMENT_KEY + dbc.getComment() + "'";
            }
            DbMetaDataUtil.executeDdl(dataSource, ddl);
        }
    }

    public void createTable(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        List<DbColumn> dbColumnList = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            Y9JsonUtil.objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, DbColumn.class));
        StringBuilder sb = new StringBuilder();
        //@formatter:off
        sb.append("CREATE TABLE ").append(tableName).append(" (\r\n").append("guid varchar(38) NOT NULL, \r\n");
        //@formatter:off
        for (DbColumn dbc : dbColumnList) {
            String columnName = dbc.getColumnName();
            if ("guid".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName)) {
                continue;
            }
            sb.append(columnName).append(" ");
            String sType = dbc.getTypeName().toUpperCase();
            if ("CHAR".equals(sType) || "VARCHAR".equals(sType)) {
                sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
            } else if ("DECIMAL".equals(sType) || "NUMERIC".equals(sType)) {
                if (dbc.getDataScale() == null) {
                    sb.append(sType).append("(").append(dbc.getDataLength()).append(")");
                } else {
                    sb.append(sType).append("(").append(dbc.getDataLength()).append(",").append(dbc.getDataScale()).append(")");
                }
            } else {
                sb.append(sType);
            }
            if (!dbc.getNullable()) {
                sb.append(NOT_NULL_KEY);
            }
            if (!dbc.getComment().isEmpty()) {
                sb.append(COMMENT_KEY).append(dbc.getComment()).append("'");
            }
            sb.append(",\r\n");
        }
        sb.append("PRIMARY KEY (guid) \r\n").append(")");
        DbMetaDataUtil.executeDdl(dataSource, sb.toString());
    }

    public void dropTable(DataSource dataSource, String tableName) throws Exception {
        DbMetaDataUtil.executeDdl(dataSource, "drop table IF EXISTS " + tableName);
    }

    public void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
        DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
    }

    public void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
        DbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew);
    }
}