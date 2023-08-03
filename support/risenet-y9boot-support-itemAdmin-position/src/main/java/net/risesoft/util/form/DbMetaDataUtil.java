package net.risesoft.util.form;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.enums.DialectEnum;
import net.risesoft.y9.sqlddl.DbColumn;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public class DbMetaDataUtil {

    private static Logger log = LoggerFactory.getLogger(DbMetaDataUtil.class);

    private static String MICROSOFT = "microsoft";

    public static String getDatabaseProductName(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            String s = dbmd.getDatabaseProductName();
            return s;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            // connection.close();
        }
    }

    public static List<DynaBean> listAllExportedKeys(Connection connection, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<DynaBean>();
        try {
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
        }
    }

    public static List<DynaBean> listAllImportedKeys(Connection connection, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<DynaBean>();
        try {
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
        }
    }

    public static List<DynaBean> listAllIndexs(Connection connection, String tableName) throws Exception {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getIndexInfo(null, null, tableName, false, false);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            rs.close();
        }
    }

    public static List<DynaBean> listAllRelations(Connection connection, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<DynaBean>();

        try {
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
        }
    }

    public static List<DynaBean> listAllTables(Connection connection, String catalog, String schemaPattern,
        String tableNamePattern, String types[]) throws Exception {
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            rs.close();
        }
    }

    public static List<Map<String, Object>> listAllTypes(Connection connection) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            connection.getTypeMap();
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
        }
    }

    public int[] batchexecuteDdl(Connection connection, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        try {
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
        }
    }

    public int[] batchexecuteDdl4Kingbase(Connection connection, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        try {
            stmt = connection.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            stmt.close();
        }
    }

    public boolean checkTableExist(Connection connection, String tableName) throws Exception {
        String databaseName = null;
        String tableSchema = null;
        ResultSet rs = null;
        try {
            try {
                databaseName = connection.getCatalog();
            } catch (Exception e) {
            }

            DatabaseMetaData dbmd = connection.getMetaData();
            String dialect = getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, databaseName, tableName, new String[] {"TABLE"});
            } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(databaseName, null, tableName, new String[] {"TABLE"});
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                tableSchema = dbmd.getUserName().toUpperCase();
                rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                tableSchema = dbmd.getUserName().toUpperCase();
                rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                tableSchema = connection.getSchema();
                rs = dbmd.getTables(null, tableSchema, tableName, new String[] {"TABLE"});
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
        }
    }

    public Boolean executeDdl(Connection connection, String ddl) throws SQLException {
        java.sql.Statement stmt = null;
        try {
            stmt = connection.createStatement();
            Boolean b = stmt.execute(ddl);
            return b;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            stmt.close();
        }
    }

    public String getDatabaseDialectName(Connection connection) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductName(connection).toLowerCase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (databaseName.indexOf(DialectEnum.MYSQL.getValue()) > -1) {
            return "mysql";
        } else if (databaseName.indexOf(DialectEnum.ORACLE.getValue()) > -1) {
            return "oracle";
        } else if (databaseName.indexOf(DialectEnum.DM.getValue()) > -1) {
            return "dm";
        } else if (databaseName.indexOf(MICROSOFT) > -1) {
            return "mssql";
        } else if (databaseName.indexOf(DialectEnum.KINGBASE.getValue()) > -1) {
            return "kingbase";
        }

        return "";
    }

    public int getDatabaseMajorVersion(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMajorVersion();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
        }
    }

    public int getDatabaseMinorVersion(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMinorVersion();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
        }
    }

    public String getDatabaseProductVersion(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductVersion();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
        }
    }

    @SuppressWarnings({"resource"})
    public List<DbColumn> listAllColumns(Connection connection, String tableName, String columnNamePatten)
        throws Exception {
        String tableSchema = null, databaseName = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<DbColumn> dbColumnList = new ArrayList<DbColumn>();
        if (checkTableExist(connection, tableName)) {
            try {
                try {
                    databaseName = connection.getCatalog();
                } catch (Exception e) {
                }
                DatabaseMetaData dbmd = connection.getMetaData();
                tableSchema = dbmd.getUserName().toUpperCase();
                String dialect = getDatabaseDialectName(connection);
                rs = getPrimaryKeys(dialect, dbmd, databaseName, tableName, tableSchema);
                List<String> pkList = new ArrayList<String>();
                while (rs.next()) {
                    pkList.add(rs.getString("COLUMN_NAME"));
                }
                if (pkList.size() == 0) {
                    // throw new Exception("***********没有主键？*************");
                }
                rs = getColumns(dialect, dbmd, databaseName, tableName, tableSchema, columnNamePatten);
                while (rs.next()) {
                    DbColumn dbColumn = new DbColumn();
                    dbColumn.setTableName(rs.getString("table_name").toUpperCase());
                    String columnName = rs.getString("column_name".toLowerCase());
                    dbColumn.setColumnName(columnName);
                    dbColumn.setColumnNameOld(columnName);
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
                    dbColumn.setDataLength(columnSize);
                    int dataType = rs.getInt("data_type");
                    dbColumn.setDataType(dataType);
                    dbColumn.setTypeName((rs.getString("type_name")).toLowerCase());
                    int decimalDigits = rs.getInt("decimal_digits");
                    dbColumn.setDataScale(decimalDigits);
                    String nullable = rs.getString("is_nullable");
                    Boolean bNullable = false;
                    if ("yes".equalsIgnoreCase(nullable)) {
                        bNullable = true;
                    }
                    dbColumn.setNullable(bNullable);
                    boolean exist = false;
                    for (DbColumn field : dbColumnList) {
                        if (field.getColumnName().equalsIgnoreCase(columnName)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        dbColumnList.add(dbColumn);
                    }
                }
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
            }
        }
        return dbColumnList;
    }

    private ResultSet getPrimaryKeys(String dialect, DatabaseMetaData dbmd, String databaseName, String tableName,
        String tableSchema) throws SQLException {
        ResultSet rs = null;
        if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            rs = dbmd.getPrimaryKeys(null, databaseName, tableName);
        } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
            rs = dbmd.getPrimaryKeys(databaseName, null, tableName);
        } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
            rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
        } else if (DialectEnum.DM.getValue().equals(dialect)) {
            rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
        } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
            rs = dbmd.getPrimaryKeys(null, tableSchema, tableName);
        }
        return rs;
    }

    private ResultSet getColumns(String dialect, DatabaseMetaData dbmd, String databaseName, String tableName,
        String tableSchema, String columnNamePatten) throws SQLException {
        ResultSet rs = null;
        if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            rs = dbmd.getColumns(null, databaseName, tableName, columnNamePatten);
        } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
            rs = dbmd.getColumns(databaseName, null, tableName, columnNamePatten);
        } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
            rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
        } else if (DialectEnum.DM.getValue().equals(dialect)) {
            rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
        } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
            rs = dbmd.getColumns(null, tableSchema, tableName, columnNamePatten);
        }
        return rs;
    }

    public List<Map<String, String>> listAllTables(Connection connection, String tableNamePattern) throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
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
        }
    }

    /***********
     * 供应商 Catalog支持 Schema支持 Oracle 不支持 Oracle User ID MySQL 不支持 数据库名 MSSQL 数据库名 对象属主名 Sybase 数据库名 数据库属主名 Informix 不支持
     * 不需要 PointBase 不支持 数据库名
     ***********/
    public String listAllTablesTree(Connection connection, String tableNamePattern) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ResultSet rs = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = "[]";
        try {
            DatabaseMetaData dbmd = connection.getMetaData();

            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, connection.getCatalog(), tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
                rs = dbmd.getTables(connection.getCatalog(), null, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                rs = dbmd.getTables(null, username, tableNamePattern, new String[] {"TABLE"});
            }
            String dbName = "";
            while (rs.next()) {
                if (!rs.getString("TABLE_NAME").contains("$")) {
                    if ("mysql".equals(dialect)) {
                        dbName = rs.getString(1);
                    } else if ("oracle".equals(dialect)) {
                        dbName = dbmd.getUserName();
                    } else if ("dm".equals(dialect)) {
                        dbName = dbmd.getUserName();
                    } else if ("kingbase".equals(dialect)) {
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
        }
    }

    public List<Map<String, Object>> listTypes(Connection connection) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            String dialect = getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                list = listTypes4Mysql();
            } else if (DialectEnum.MSSQL.getValue().equals(dialect)) {
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                list = listTypes4Oracle();
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                list = listTypes4Dm();
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                list = listTypes4Kingbase();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {

        }
        return list;
    }

    private List<Map<String, Object>> listTypes4Mysql() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("typeName", "varchar");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "int");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "integer");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "longblob");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "text");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "longtext");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "datetime");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "date");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "bit");
        return list;
    }

    private List<Map<String, Object>> listTypes4Oracle() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }

    private List<Map<String, Object>> listTypes4Dm() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }

    private List<Map<String, Object>> listTypes4Kingbase() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<String, Object>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }
}
