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
import net.risesoft.y9.sqlddl.pojo.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public class DdlMysql {

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    public DdlMysql() {
        this.y9TableFieldRepository = Y9Context.getBean(Y9TableFieldRepository.class);
    }

    public void addTableColumn(DataSource dataSource, String tableName, List<DbColumn> dbColumnList) throws Exception {
        if (Y9FormDbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            for (DbColumn dbc : dbColumnList) {
                String columnName = dbc.getColumnName();
                if ("guid".equalsIgnoreCase(columnName) || "processInstanceId".equalsIgnoreCase(columnName)) {
                    continue;
                }
                String ddl = "ALTER TABLE " + tableName;
                // String DDL = "ALTER TABLE " + tableName + " ADD COLUMN " + dbc.getColumnName() + " ";
                String dbColumnName = "";
                ResultSet rs = null;
                try (Connection connection = dataSource.getConnection()) {
                    DatabaseMetaData databaseMetaData = connection.getMetaData();
                    String tableSchema = databaseMetaData.getUserName().toUpperCase();
                    rs = databaseMetaData.getColumns(null, tableSchema, tableName, dbc.getColumnName());
                    while (rs.next()) {
                        dbColumnName = rs.getString("column_name".toLowerCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
                // 不存在旧字段则新增
                if (("".equals(dbColumnName) && StringUtils.isBlank(dbc.getColumnNameOld()))) {
                    ddl += " ADD COLUMN " + dbc.getColumnName() + " ";
                } else {
                    // 存在旧字段，字段名称没有改变则修改属性
                    if (dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())
                        || StringUtils.isBlank(dbc.getColumnNameOld())) {
                        ddl += " MODIFY COLUMN " + dbc.getColumnName() + " ";
                    } else {
                        // 存在旧字段，字段名称改变则修改字段名称及属性
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
                if (dbc.getNullable()) {
                    ddl += " DEFAULT NULL";
                } else {
                    ddl += " NOT NULL";
                }
                if (!dbc.getComment().isEmpty()) {
                    ddl += " COMMENT '" + dbc.getComment() + "'";
                }
                Y9FormDbMetaDataUtil.executeDdl(dataSource, ddl);
                y9TableFieldRepository.updateOldFieldName(dbc.getTableName(), dbc.getColumnName());
            }
        }
    }

    public void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        if (!Y9FormDbMetaDataUtil.checkTableExist(dataSource, tableName)) {
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
                ddl += " NOT NULL";
            }
            if (!dbc.getComment().isEmpty()) {
                ddl += " COMMENT '" + dbc.getComment() + "'";
            }
            Y9FormDbMetaDataUtil.executeDdl(dataSource, ddl);
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
				sb.append(" NOT NULL");
			}
			if (!dbc.getComment().isEmpty()) {
				sb.append(" COMMENT '").append(dbc.getComment()).append("'");
			}
			sb.append(",\r\n");
		}
		sb.append("PRIMARY KEY (guid) \r\n").append(")");
		Y9FormDbMetaDataUtil.executeDdl(dataSource, sb.toString());
	}

	public void dropTable(DataSource dataSource, String tableName) throws Exception {
		Y9FormDbMetaDataUtil.executeDdl(dataSource, "drop table IF EXISTS " + tableName);
	}

	public void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		Y9FormDbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
	}

	public void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		Y9FormDbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE " + tableNameOld + " RENAME " + tableNameNew);
	}

}
