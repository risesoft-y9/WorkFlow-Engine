package net.risesoft.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DbMetaDataUtil {
	private static Logger log = LoggerFactory.getLogger(DbMetaDataUtil.class);

	public static int[] batchexecuteDdl(DataSource dataSource, List<String> sqlList) throws SQLException {
		Connection connection = null;
		java.sql.Statement stmt = null;
		try {
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
			for (String sql : sqlList) {
				stmt.addBatch(sql);
			}
			stmt.addBatch("SET FOREIGN_KEY_CHECKS=1");
			return stmt.executeBatch();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			stmt.execute("SET FOREIGN_KEY_CHECKS=1");
			stmt.close();
			connection.close();
		}
	}

	public static Boolean executeDdl(DataSource dataSource, String DDL) throws SQLException {
		Connection connection = null;
		java.sql.Statement stmt = null;
		try {
			connection = dataSource.getConnection();
			stmt = connection.createStatement();
			Boolean b = stmt.execute(DDL);

			return b;
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			stmt.close();
			connection.close();
		}
	}

	public static int getDatabaseMajorVersion(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			return dbmd.getDatabaseMajorVersion();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			connection.close();
		}
	}

	public static int getDatabaseMinorVersion(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			return dbmd.getDatabaseMinorVersion();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			connection.close();
		}
	}

	public static String getDatabaseProductName(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			String s = dbmd.getDatabaseProductName();
			// System.out.println("getDatabaseProductName====" + s);
			return s;
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			connection.close();
		}
	}

	public static String getDatabaseProductVersion(DataSource dataSource) throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			return dbmd.getDatabaseProductVersion();
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			connection.close();
		}
	}

	public static List<DynaBean> listAllExportedKeys(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		List<DynaBean> rList = new ArrayList<DynaBean>();
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getExportedKeys(null, null, tableName);
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			rs.close();

			rList.addAll(rsdc.getRows());
			return rList;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
			}
			connection.close();
		}
	}

	public static List<DynaBean> listAllImportedKeys(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		List<DynaBean> rList = new ArrayList<DynaBean>();
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getImportedKeys(null, null, tableName);
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			rs.close();

			rList.addAll(rsdc.getRows());

			return rList;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	public static List<DynaBean> listAllIndexs(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getIndexInfo(null, null, tableName, false, false);
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			return rsdc.getRows();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	public static List<DynaBean> listAllRelations(DataSource dataSource, String tableName) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		List<DynaBean> rList = new ArrayList<DynaBean>();

		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getImportedKeys(null, null, tableName);
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			rs.close();

			rs = dbmd.getExportedKeys(null, null, tableName);
			RowSetDynaClass rsdc2 = new RowSetDynaClass(rs, true);
			rs.close();

			rList.addAll(rsdc.getRows());
			rList.addAll(rsdc2.getRows());

			return rList;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	public static List<DynaBean> listAllTables(DataSource dataSource, String catalog, String schemaPattern, String tableNamePattern, String types[]) throws Exception {
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
			return rsdc.getRows();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	public static List<Map<String, Object>> listAllTypes(DataSource dataSource) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			rs = dbmd.getTypeInfo();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>(16);
				map.put("TYPE_NAME", rs.getString("TYPE_NAME"));
				map.put("DATA_TYPE", rs.getInt("DATA_TYPE"));
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	public boolean checkTableExist(DataSource dataSource, String tableName) throws Exception {
		String databaseName = null;
		String tableSchema = null;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			try {
				databaseName = connection.getCatalog();
			} catch (Exception e) {
			}

			DatabaseMetaData dbmd = connection.getMetaData();
			String dialect = getDatabaseDialectName(dataSource);
			if ("mysql".equals(dialect)) {
				rs = dbmd.getTables(null, databaseName, tableName, new String[] { "TABLE" });
			} else if ("mssql".equals(dialect)) {
				rs = dbmd.getTables(databaseName, null, tableName, new String[] { "TABLE" });
			} else if ("oracle".equals(dialect)) {
				tableSchema = dbmd.getUserName().toUpperCase();
				rs = dbmd.getTables(null, tableSchema, tableName, new String[] { "TABLE" });
			} else if ("dm".equals(dialect)) {
				tableSchema = dbmd.getUserName().toUpperCase();
				rs = dbmd.getTables(null, tableSchema, tableName, new String[] { "TABLE" });
			} else if ("kingbase".equals(dialect)) {
				tableSchema = connection.getSchema();
				rs = dbmd.getTables(null, tableSchema, tableName, new String[] { "TABLE" });
			}

			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	public String getDatabaseDialectName(DataSource dataSource) {
		String databaseName = "";
		try {
			databaseName = getDatabaseProductName(dataSource).toLowerCase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (databaseName.indexOf("mysql") > -1) {
			return "mysql";
		} else if (databaseName.indexOf("kingbasees") > -1) {
			return "kingbase";
		} else if (databaseName.indexOf("oracle") > -1) {
			return "oracle";
		} else if (databaseName.indexOf("microsoft") > -1) {
			return "mssql";
		}

		return "";
	}

	@SuppressWarnings({ "resource" })
	public List<DbColumn> listAllColumns(DataSource dataSource, String tableName, String columnNamePatten) throws Exception {
		Connection connection = null;
		String tableSchema = null;
		String databaseName = null;

		Statement stmt = null;
		ResultSet rs = null;
		List<DbColumn> dbColumnList = new ArrayList<DbColumn>();
		if (this.checkTableExist(dataSource, tableName)) {
			try {
				connection = dataSource.getConnection();
				try {
					databaseName = connection.getCatalog();
				} catch (Exception e) {
				}

				DatabaseMetaData dbmd = connection.getMetaData();
				tableSchema = dbmd.getUserName().toUpperCase();
				String dialect = getDatabaseDialectName(dataSource);

				// 获得主键
				if ("mysql".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
				} else if ("mssql".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(databaseName, null, tableName);
				} else if ("oracle".equals(dialect)) {
					rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
				}
				List<String> pkList = new ArrayList<String>();
				while (rs.next()) {
					pkList.add(rs.getString("COLUMN_NAME"));
				}

				if (pkList.size() == 0) {
					// throw new Exception("***********没有主键？*************");
				}

				// 获得所有列的属性
				if ("mysql".equals(dialect)) {
					rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
				} else if ("mssql".equals(dialect)) {
					rs = dbmd.getColumns(databaseName, null, tableName, columnNamePatten);
				} else if ("oracle".equals(dialect)) {
					rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
				}

				while (rs.next()) {
					DbColumn dbColumn = new DbColumn();

					dbColumn.setTable_name(rs.getString("table_name").toUpperCase());

					String columnName = rs.getString("column_name".toLowerCase());
					dbColumn.setColumn_name(columnName);
					dbColumn.setColumn_name_old(columnName);

					if (pkList.contains(columnName)) {
						dbColumn.setPrimaryKey(true);
					} else {
						dbColumn.setPrimaryKey(false);
					}

					String remarks = rs.getString("remarks");
					if (StringUtils.isBlank(remarks)) {
						dbColumn.setComment(columnName.toUpperCase());
					} else {
						dbColumn.setComment(remarks);
					}

					int columnSize = rs.getInt("column_size");
					dbColumn.setData_length(columnSize);

					int data_type = rs.getInt("data_type");
					dbColumn.setData_type(data_type);
					dbColumn.setType_name((rs.getString("type_name")).toLowerCase());

					int decimal_digits = rs.getInt("decimal_digits");
					dbColumn.setData_scale(decimal_digits);

					String nullable = rs.getString("is_nullable");
					Boolean bNullable = false;
					if ("yes".equalsIgnoreCase(nullable)) {
						bNullable = true;
					}
					dbColumn.setNullable(bNullable);

					boolean exist = false;
					for (DbColumn field : dbColumnList) {
						if (field.getColumn_name().equalsIgnoreCase(columnName)) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						dbColumnList.add(dbColumn);
					}
				}

				/***********
				 * RowSetDynaClass rsdc2 = new RowSetDynaClass(rs, true); List columnList =
				 * rsdc2.getRows(); for (int i = 0; i < columnList.size(); i++) { BasicDynaBean
				 * bdb = (BasicDynaBean) columnList.get(i); DbColumn dbColumn = new DbColumn();
				 * 
				 * dbColumn.setTable_name((String) bdb.get("table_name"));
				 * 
				 * String columnName = (String) bdb.get("column_name");
				 * dbColumn.setColumn_name(columnName); dbColumn.setColumn_name_old(columnName);
				 * 
				 * if (pkList.contains(columnName)) { dbColumn.setPrimaryKey(true); } else {
				 * dbColumn.setPrimaryKey(false); }
				 * 
				 * String remarks = (String) bdb.get("remarks"); if
				 * (StringUtils.isBlank(remarks)) {
				 * dbColumn.setComment(columnName.toUpperCase()); } else {
				 * dbColumn.setComment(remarks); }
				 * 
				 * Object columnSize = bdb.get("column_size"); if (columnSize != null) { int cs
				 * = 10; if (columnSize instanceof BigDecimal) { cs = ((BigDecimal)
				 * columnSize).intValue(); } else { cs = ((Integer) columnSize).intValue(); }
				 * dbColumn.setData_length(cs); dbColumn.setData_precision(cs); } else {
				 * dbColumn.setData_length(10); }
				 * 
				 * Object dtObj = bdb.get("data_type"); int dt = 3; if (dtObj instanceof
				 * BigDecimal) { dt = ((BigDecimal) dtObj).intValue(); } else { dt = ((Integer)
				 * dtObj).intValue(); } dbColumn.setData_type(dt);
				 * dbColumn.setType_name(((String) bdb.get("type_name")).toLowerCase());
				 * 
				 * Object ddObj = bdb.get("decimal_digits"); if (ddObj != null) { int dd = 3; if
				 * (ddObj instanceof BigDecimal) { dd = ((BigDecimal) ddObj).intValue(); } else
				 * { dd = ((Integer) ddObj).intValue(); } dbColumn.setData_scale(dd);
				 * 
				 * // 在oracle中，整型和浮点型都是一致的 if (dd == 0 && (dt == Types.FLOAT || dt == Types.REAL
				 * || dt == Types.DOUBLE || dt == Types.NUMERIC || dt == Types.DECIMAL)) {
				 * dbColumn.setData_type(Types.INTEGER); } }
				 * 
				 * String nullable = (String) bdb.get("is_nullable"); Boolean bNullable = false;
				 * if ("yes".equalsIgnoreCase(nullable)) { bNullable = true; }
				 * dbColumn.setNullable(bNullable);
				 * 
				 * boolean exist = false; for (DbColumn field : dbColumnList) { if
				 * (field.getColumn_name().equalsIgnoreCase(dbColumn.getColumn_name())) { exist
				 * = true; break; } } if (!exist) { dbColumnList.add(dbColumn); } }
				 **************/
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			} finally {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (connection != null) {
					connection.close();
				}
			}
		}

		return dbColumnList;
	}

	public List<Map<String, String>> listAllTables(DataSource dataSource, String tableNamePattern) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();
			String username = dbmd.getUserName().toUpperCase();
			String dialect = getDatabaseDialectName(dataSource);
			if ("mysql".equals(dialect)) {
				rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] { "TABLE" });
			} else if ("mssql".equals(dialect)) {
				rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] { "TABLE" });
			} else if ("oracle".equals(dialect)) {
				rs = dbmd.getTables(null, username, tableNamePattern, new String[] { "TABLE" });
			}

			while (rs.next()) {
				if (!rs.getString("TABLE_NAME").contains("$")) {
					HashMap<String, String> map = new HashMap<String, String>(16);
					map.put("catalog", rs.getString("TABLE_CAT"));
					map.put("schema", rs.getString("TABLE_SCHEM"));
					map.put("name", rs.getString("TABLE_NAME"));
					list.add(map);
				}
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

	/***********
	 * 供应商 Catalog支持 Schema支持 Oracle 不支持 Oracle User ID MySQL 不支持 数据库名 MSSQL 数据库名
	 * 对象属主名 Sybase 数据库名 数据库属主名 Informix 不支持 不需要 PointBase 不支持 数据库名
	 ***********/
	public String listAllTablesTree(DataSource dataSource, String tableNamePattern) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Connection connection = null;
		ResultSet rs = null;
		ObjectMapper mapper = new ObjectMapper();
		String json = "[]";
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbmd = connection.getMetaData();

			String username = dbmd.getUserName().toUpperCase();
			String dialect = getDatabaseDialectName(dataSource);
			if ("mysql".equals(dialect)) {
				rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] { "TABLE" });
			} else if ("mssql".equals(dialect)) {
				rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] { "TABLE" });
			} else if ("oracle".equals(dialect)) {
				rs = dbmd.getTables(null, username, tableNamePattern, new String[] { "TABLE" });
			}
			String dbName = "";
			while (rs.next()) {
				if (!rs.getString("TABLE_NAME").contains("$")) {
					if ("mysql".equals(dialect)) {
						dbName = rs.getString(1);
					} else if ("oracle".equals(dialect)) {
						dbName = dbmd.getUserName();
					}
					HashMap<String, Object> map = new HashMap<String, Object>(16);
					map.put("text", rs.getString("TABLE_NAME"));
					HashMap<String, Object> attributes = new HashMap<String, Object>(16);
					attributes.put("catalog", rs.getString("TABLE_CAT"));
					attributes.put("schema", rs.getString("TABLE_SCHEM"));
					map.put("attributes", attributes);
					list.add(map);
				}
			}
			HashMap<String, Object> pNode = new HashMap<String, Object>(16);
			pNode.put("id", 0);
			pNode.put("text", dbName + "库表列表");
			pNode.put("iconCls", "icon-folder");
			pNode.put("children", list);
			List<Map<String, Object>> tree = new ArrayList<Map<String, Object>>();
			tree.add(pNode);
			json = mapper.writeValueAsString(tree);

			return json;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			rs.close();
			connection.close();
		}
	}

}
