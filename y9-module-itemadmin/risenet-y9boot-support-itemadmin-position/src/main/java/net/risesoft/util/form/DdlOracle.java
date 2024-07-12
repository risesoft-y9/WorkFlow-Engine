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
import net.risesoft.y9.sqlddl.DbColumn;

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

    public void addTableColumn(DataSource dataSource, String tableName, List<DbColumn> dbcs) throws Exception {
        StringBuilder sb = new StringBuilder();
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        if (dbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            for (DbColumn dbc : dbcs) {
                String columnName = dbc.getColumnName();
                if ("GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName)) {
                    continue;
                }
                sb = new StringBuilder();
                sb.append("ALTER TABLE \"" + tableName + "\"");
                String nullable = "";
                String dbColumnName = "";
                ResultSet rs = null;
                try (Connection connection = dataSource.getConnection()) {
                    DatabaseMetaData dbmd = connection.getMetaData();
                    String tableSchema = dbmd.getUserName().toUpperCase();
                    rs = dbmd.getColumns(null, tableSchema, tableName, dbc.getColumnName().toUpperCase());
                    while (rs.next()) {
                        nullable = rs.getString("is_nullable");
                        dbColumnName = rs.getString("column_name".toLowerCase());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
                boolean add = false;
                // 不存在旧字段则新增
                if ("".equals(dbColumnName) && org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                    sb.append(" ADD " + dbc.getColumnName() + " ");
                    add = true;
                } else {
                    // 存在旧字段，字段名称没有改变则修改属性
                    if (columnName.equalsIgnoreCase(dbc.getColumnNameOld())
                        || org.apache.commons.lang3.StringUtils.isBlank(dbc.getColumnNameOld())) {
                        sb.append(" MODIFY " + dbc.getColumnName() + " ");
                    } else {
                        // 存在旧字段，字段名称改变则修改字段名称及属性
                        try {
                            StringBuilder sb1 = new StringBuilder();
                            sb1.append("ALTER TABLE \"" + tableName + "\"");
                            dbMetaDataUtil.executeDdl(dataSource,
                                sb1.append(" RENAME COLUMN " + dbc.getColumnNameOld() + " TO " + dbc.getColumnName())
                                    .toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 重命名之后再MODIFY
                        sb.append(" MODIFY " + dbc.getColumnName() + " ");
                    }
                }
                String sType = dbc.getTypeName().toUpperCase();
                if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType)
                    || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + ")");
                } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
                    || "NUMBER".equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }
                // 新增字段
                if ("".equals(nullable) && add) {
                    if (dbc.getNullable()) {
                        sb.append(" NULL");
                    } else {
                        sb.append(" NOT NULL");
                    }
                } else {// 修改字段
                    if (dbc.getNullable() && "NO".equals(nullable)) {
                        sb.append(" NULL");
                    }
                    if (!dbc.getNullable() && "YES".equals(nullable)) {
                        sb.append(" NOT NULL");
                    }
                }

                dbMetaDataUtil.executeDdl(dataSource, sb.toString());
                if (StringUtils.hasText(dbc.getComment())) {
                    dbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                        + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                }
                y9TableFieldRepository.updateOldFieldName(dbc.getTableName(), dbc.getColumnName());
            }
        }
    }

    public void alterTableColumn(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        if (!dbMetaDataUtil.checkTableExist(dataSource, tableName)) {
            throw new Exception("数据库中不存在这个表：" + tableName);
        }

        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        for (DbColumn dbc : dbcs) {
            if (StringUtils.hasText(dbc.getColumnNameOld())) {
                StringBuilder sb = new StringBuilder();
                sb.append("ALTER TABLE \"" + tableName + "\"");
                // 字段名称有改变
                if (!dbc.getColumnName().equalsIgnoreCase(dbc.getColumnNameOld())) {
                    try {
                        dbMetaDataUtil.executeDdl(dataSource,
                            sb.append(" RENAME COLUMN " + dbc.getColumnNameOld() + " TO " + dbc.getColumnName())
                                .toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sb.append(" MODIFY " + dbc.getColumnName() + " ");
                String sType = dbc.getTypeName().toUpperCase();
                if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType)
                    || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
                    sb.append(sType + "(" + dbc.getDataLength() + ")");
                } else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType)
                    || "NUMBER".equalsIgnoreCase(sType)) {
                    if (dbc.getDataScale() == null) {
                        sb.append(sType + "(" + dbc.getDataLength() + ")");
                    } else {
                        sb.append(sType + "(" + dbc.getDataLength() + "," + dbc.getDataScale() + ")");
                    }
                } else {
                    sb.append(sType);
                }

                List<DbColumn> list = dbMetaDataUtil.listAllColumns(dataSource, tableName, dbc.getColumnNameOld());
                if (dbc.getNullable()) {
                    if (!list.get(0).getNullable()) {
                        sb.append(" NULL");
                    }
                } else {
                    if (list.get(0).getNullable()) {
                        sb.append(" NOT NULL");
                    }
                }
                dbMetaDataUtil.executeDdl(dataSource, sb.toString());
                if (StringUtils.hasText(dbc.getComment())) {
                    if (!list.get(0).getComment().equals(dbc.getComment())) {
                        dbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"."
                            + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
                    }
                }
            }
        }
    }

    public void createTable(DataSource dataSource, String tableName, String jsonDbColumns) throws Exception {
        StringBuilder sb = new StringBuilder();
        DbColumn[] dbcs = Y9JsonUtil.objectMapper.readValue(jsonDbColumns,
            TypeFactory.defaultInstance().constructArrayType(DbColumn.class));
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        //@formatter:off
		sb.append("CREATE TABLE \"" + tableName + "\" (\r\n").append("GUID varchar2(38) NOT NULL, \r\n").append("PROCESSINSTANCEID nvarchar2(64) NOT NULL, \r\n");
		//@formatter:off
		for (DbColumn dbc : dbcs) {
			String columnName = dbc.getColumnName();
			if ("GUID".equalsIgnoreCase(columnName) || "PROCESSINSTANCEID".equalsIgnoreCase(columnName)) {
				continue;
			}
			sb.append(columnName).append(" ");
			String sType = dbc.getTypeName().toUpperCase();
			if ("CHAR".equals(sType) || "NCHAR".equals(sType) || "VARCHAR2".equals(sType) || "NVARCHAR2".equals(sType) || "RAW".equals(sType)) {
				sb.append(sType + "(" + dbc.getDataLength() + ")");
			} else if ("DECIMAL".equalsIgnoreCase(sType) || "NUMERIC".equalsIgnoreCase(sType) || "NUMBER".equalsIgnoreCase(sType)) {
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
			sb.append(",\r\n");
		}
		sb.append("PRIMARY KEY (GUID) \r\n").append(")");
		dbMetaDataUtil.executeDdl(dataSource, sb.toString());

		for (DbColumn dbc : dbcs) {
			if (StringUtils.hasText(dbc.getComment())) {
				dbMetaDataUtil.executeDdl(dataSource, "COMMENT ON COLUMN \"" + tableName + "\"." + dbc.getColumnName().trim().toUpperCase() + " IS '" + dbc.getComment() + "'");
			}
		}
	}

	public void dropTable(DataSource dataSource, String tableName) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		if (dbMetaDataUtil.checkTableExist(dataSource, tableName)) {
			dbMetaDataUtil.executeDdl(dataSource, "DROP TABLE \"" + tableName + "\"");
		}
	}

	public void dropTableColumn(DataSource dataSource, String tableName, String columnName) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		dbMetaDataUtil.executeDdl(dataSource, "ALTER TABLE \"" + tableName + "\" DROP COLUMN " + columnName);
	}

	public void renameTable(DataSource dataSource, String tableNameOld, String tableNameNew) throws Exception {
		DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
		dbMetaDataUtil.executeDdl(dataSource, "RENAME \"" + tableNameOld + "\" TO \"" + tableNameNew + "\"");
	}
}
