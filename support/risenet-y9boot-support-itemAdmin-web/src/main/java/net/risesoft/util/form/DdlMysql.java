package net.risesoft.util.form;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.type.TypeFactory;

import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public class DdlMysql {

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    public DdlMysql() {
        this.y9TableFieldRepository = Y9Context.getBean(Y9TableFieldRepository.class);
    }

    public void addTableColumn(Connection connection, String tableName, List<DbColumn> dbcs) throws Exception {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        if (dbMetaDataUtil.checkTableExist(connection, tableName)) {
            for (DbColumn dbc : dbcs) {
                String columnName = dbc.getColumnName();
                if ("guid".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName)) {
                    continue;
                }
                String ddl = "ALTER TABLE " + tableName;
                // String DDL = "ALTER TABLE " + tableName + " ADD COLUMN " + dbc.getColumnName() + " ";
                DatabaseMetaData dbmd = connection.getMetaData();
                String tableSchema = dbmd.getUserName().toUpperCase();
                ResultSet rs = dbmd.getColumns(null, tableSchema, tableName, dbc.getColumnName());
                String nullable = "";
                String dbColumnName = "";
                while (rs.next()) {
                    // 当前列目前是否可为空
                    nullable = rs.getString("is_nullable");
                    dbColumnName = rs.getString("column_name".toLowerCase());
                }
                boolean add = false;
                // 不存在旧字段则新增
                if (("".equals(dbColumnName) && StringUtils.isBlank(dbc.getColumnNameOld()))) {
                    ddl += " ADD COLUMN " + dbc.getColumnName() + " ";
                    add = true;
                } else {
                    // 存在旧字段，字段名称没有改变则修改属性
                    if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld()) || StringUtils.isBlank(dbc.getColumnNameOld())) {
                        ddl += " MODIFY COLUMN " + dbc.getColumnName() + " ";
                    } else {// 存在旧字段，字段名称改变则修改字段名称及属性
                        ddl += " CHANGE COLUMN " + dbc.getColumnNameOld() + " " + dbc.getColumnName() + " ";
                    }
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
                // 新增字段
                if ("".equals(nullable) && add) {
                    if (dbc.getNullable()) {
                        ddl += " DEFAULT NULL";
                    } else {
                        ddl += " NOT NULL";
                    }
                } else {
                    if (dbc.getNullable()) {
                        ddl += " DEFAULT NULL";
                    } else {
                        ddl += " NOT NULL";
                    }
                }

                if (dbc.getComment().length() > 0) {
                    ddl += " COMMENT '" + dbc.getComment() + "'";
                }
                dbMetaDataUtil.executeDdl(connection, ddl);
                y9TableFieldRepository.updateOldFieldName(dbc.getTableName(), dbc.getColumnName());
            }
        }
    }

    public void alterTableColumn(Connection connection, String tableName, String jsonDbColumns) throws Exception {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        if (!dbMetaDataUtil.checkTableExist(connection, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbcs) {
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

            if (dbc.getNullable() == true) {
                ddl += " DEFAULT NULL";
            } else {
                ddl += " NOT NULL";
            }
            if (dbc.getComment().length() > 0) {
                ddl += " COMMENT '" + dbc.getComment() + "'";
            }
            dbMetaDataUtil.executeDdl(connection, ddl);
        }
    }

    public void createTable(Connection connection, String tableName, String jsonDbColumns) throws Exception {
        List<DbColumn> dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns, Y9JsonUtil.objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, DbColumn.class));
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        StringBuilder sb = new StringBuilder();
        //@formatter:off
		sb.append("CREATE TABLE " + tableName + " (\r\n").append("guid varchar(38) NOT NULL, \r\n").append("processInstanceId varchar(64) NOT NULL, \r\n");
		//@formatter:off
		for (DbColumn dbc : dbcs) {
			String columnName = dbc.getColumnName();
			if ("guid".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName)) {
				continue;
			}
			sb.append(columnName).append(" ");
			String sType = dbc.getTypeName().toUpperCase();
			if ("CHAR".equals(sType) || "VARCHAR".equals(sType)) {
				sb.append(sType + "(" + dbc.getDataLength() + ")");
			} else if ("DECIMAL".equals(sType) || "NUMERIC".equals(sType)) {
				if (dbc.getDataScale() == null) {
					sb.append(sType + "(" + dbc.getDataLength() + ")");
				} else {
					sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
				}
			} else {
				sb.append(sType);
			}
			if (!dbc.getNullable()) {
				sb.append(" NOT NULL");
			}
			if (dbc.getComment().length() > 0) {
				sb.append(" COMMENT '" + dbc.getComment() + "'");
			}
			sb.append(",\r\n");
		}
		sb.append("PRIMARY KEY (guid) \r\n").append(")");
		dbMetaDataUtil.executeDdl(connection, sb.toString());
	}

	public void dropTable(Connection connection, String tableName) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		dbMetaDataUtil.executeDdl(connection, "drop table IF EXISTS " + tableName);
	}

	public void dropTableColumn(Connection connection, String tableName, String columnName) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		dbMetaDataUtil.executeDdl(connection, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public void renameTable(Connection connection, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		dbMetaDataUtil.executeDdl(connection, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew);
	}

}
