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

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DbMetaDataUtil {

    private static final String MICROSOFT = "microsoft";

    public static String getDatabaseProductName(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductName();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static String getDatabaseProductNameByConnection(Connection connection) throws SQLException {
        try {
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductName();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    public static List<DynaBean> listAllExportedKeys(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getExportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());
            return rList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllImportedKeys(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getImportedKeys(null, null, tableName);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            rs.close();

            rList.addAll(rsdc.getRows());

            return rList;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllIndexs(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getIndexInfo(null, null, tableName, false, false);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllRelations(DataSource dataSource, String tableName) throws Exception {
        ResultSet rs = null;
        List<DynaBean> rList = new ArrayList<>();
        Connection connection = null;
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
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<DynaBean> listAllTables(DataSource dataSource, String catalog, String schemaPattern,
        String tableNamePattern, String[] types) throws Exception {
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            rs = dbmd.getTables(catalog, schemaPattern, tableNamePattern, types);
            RowSetDynaClass rsdc = new RowSetDynaClass(rs, true);
            return rsdc.getRows();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<Map<String, Object>> listAllTypes(DataSource dataSource) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            connection.getTypeMap();
            rs = dbmd.getTypeInfo();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                map.put("DATA_TYPE", rs.getInt("DATA_TYPE"));
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public int[] batchexecuteDdl(DataSource dataSource, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        Connection connection = null;
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
            LOGGER.error(e.getMessage());
            throw e;
        } finally {

            if (stmt != null) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int[] batchexecuteDdl4Kingbase(DataSource dataSource, List<String> sqlList) throws SQLException {
        java.sql.Statement stmt = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.createStatement();
            for (String sql : sqlList) {
                stmt.addBatch(sql);
            }
            return stmt.executeBatch();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public boolean checkTableExist(DataSource dataSource, String tableName) throws Exception {
        String databaseName = null;
        String tableSchema = null;
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            try {
                databaseName = connection.getCatalog();
            } catch (Exception e) {
            }

            DatabaseMetaData dbmd = connection.getMetaData();
            String dialect = getDatabaseDialectNameByConnection(connection);
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
            return rs != null && rs.next();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public Boolean executeDdl(DataSource dataSource, String ddl) throws SQLException {
        java.sql.Statement stmt = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.createStatement();
            return stmt.execute(ddl);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
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

    public String getDatabaseDialectName(DataSource dataSource) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductName(dataSource).toLowerCase();
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

    public String getDatabaseDialectNameByConnection(Connection connection) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductNameByConnection(connection).toLowerCase();
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

    public int getDatabaseMajorVersion(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMajorVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public int getDatabaseMinorVersion(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseMinorVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public String getDatabaseProductVersion(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            return dbmd.getDatabaseProductVersion();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
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

    @SuppressWarnings({"resource"})
    public List<DbColumn> listAllColumns(DataSource dataSource, String tableName, String columnNamePatten)
        throws Exception {
        String tableSchema = null, databaseName = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<DbColumn> dbColumnList = new ArrayList<>();

        if (checkTableExist(dataSource, tableName)) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                try {
                    databaseName = connection.getCatalog();
                } catch (Exception e) {
                }
                DatabaseMetaData dbmd = connection.getMetaData();
                tableSchema = dbmd.getUserName().toUpperCase();
                String dialect = getDatabaseDialectNameByConnection(connection);
                rs = getPrimaryKeys(dialect, dbmd, databaseName, tableName, tableSchema);
                List<String> pkList = new ArrayList<>();
                while (rs.next()) {
                    pkList.add(rs.getString("COLUMN_NAME"));
                }
                if (pkList.isEmpty()) {
                    // throw new Exception("***********没有主键？*************");
                }
                rs = getColumns(dialect, dbmd, databaseName, tableName, tableSchema, columnNamePatten);
                while (rs.next()) {
                    DbColumn dbColumn = new DbColumn();
                    dbColumn.setTableName(rs.getString("table_name").toUpperCase());
                    String columnName = rs.getString("column_name".toLowerCase());
                    dbColumn.setColumnName(columnName);
                    dbColumn.setColumnNameOld(columnName);
                    dbColumn.setPrimaryKey(pkList.contains(columnName));
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
                LOGGER.error(e.getMessage());
                throw e;
            } finally {
                if (connection != null) {
                    connection.close();
                }
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

    /**
     * 返回所有的业务表
     */
    public Map<String, Object> listAllTableNames(DataSource dataSource) throws Exception {
        Map<String, Object> al = new HashMap<>(16);
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = null;
        String sql = "show tables";
        try {
            connection = dataSource.getConnection();
            String dialect = getDatabaseDialectNameByConnection(connection);
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            }
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                // mysql中不处理视图
                al.put(tableName.toLowerCase(), tableName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return al;
    }

    public List<Map<String, String>> listAllTables(DataSource dataSource, String tableNamePattern) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectNameByConnection(connection);
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
                    HashMap<String, String> map = new HashMap<>(16);
                    map.put("catalog", rs.getString("TABLE_CAT"));
                    map.put("schema", rs.getString("TABLE_SCHEM"));
                    map.put("name", rs.getString("TABLE_NAME"));
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    LOGGER.error("关闭数据库连接失败", e);
                }
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    /***********
     * 供应商 Catalog支持 Schema支持 Oracle 不支持 Oracle User ID MySQL 不支持 数据库名 MSSQL 数据库名 对象属主名 Sybase 数据库名 数据库属主名 Informix 不支持
     * 不需要 PointBase 不支持 数据库名
     ***********/
    public String listAllTablesTree(DataSource dataSource, String tableNamePattern) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet rs = null;
        ObjectMapper mapper = new ObjectMapper();
        String json = "[]";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();

            String username = dbmd.getUserName().toUpperCase();
            String dialect = getDatabaseDialectName(dataSource);
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
                    HashMap<String, Object> map = new HashMap<>(16);
                    map.put("text", rs.getString("TABLE_NAME"));
                    HashMap<String, Object> attributes = new HashMap<>(16);
                    attributes.put("catalog", rs.getString("TABLE_CAT"));
                    attributes.put("schema", rs.getString("TABLE_SCHEM"));
                    map.put("attributes", attributes);
                    list.add(map);
                }
            }
            HashMap<String, Object> pNode = new HashMap<>(16);
            pNode.put("id", 0);
            pNode.put("text", dbName + "库表列表");
            pNode.put("iconCls", "icon-folder");
            pNode.put("children", list);
            List<Map<String, Object>> tree = new ArrayList<>();
            tree.add(pNode);
            json = mapper.writeValueAsString(tree);

            return json;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<Map<String, Object>> listTypes(DataSource dataSource) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String dialect = getDatabaseDialectName(dataSource);
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
            LOGGER.error(e.getMessage());
            throw e;
        }
        return list;
    }

    private List<Map<String, Object>> listTypes4Dm() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }

    private List<Map<String, Object>> listTypes4Kingbase() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }

    private List<Map<String, Object>> listTypes4Mysql() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        map.put("typeName", "varchar");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "int");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "integer");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "longblob");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "text");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "longtext");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "datetime");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "date");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "bit");
        return list;
    }

    private List<Map<String, Object>> listTypes4Oracle() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(16);
        map.put("typeName", "VARCHAR2");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "NUMBER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "LONG");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "TIMESTAMP");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "BLOB");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "DATE");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "INTEGER");
        list.add(map);
        map = new HashMap<>(16);
        map.put("typeName", "CLOB");
        list.add(map);
        return list;
    }
}
