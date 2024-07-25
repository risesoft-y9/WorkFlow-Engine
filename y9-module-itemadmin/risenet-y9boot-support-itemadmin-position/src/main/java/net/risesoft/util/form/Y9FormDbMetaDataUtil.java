package net.risesoft.util.form;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.SqlConstants;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9FormDbMetaDataUtil extends DbMetaDataUtil {

    /**
     * 返回所有的业务表
     */
    public static Map<String, Object> listAllTableNames(DataSource dataSource) throws Exception {
        Map<String, Object> al = new HashMap<>(16);

        ResultSet rs = null;
        String sql = "show tables";
        try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {

            String dialect = getDatabaseDialectNameByConnection(connection);
            switch (dialect) {
                case SqlConstants.DBTYPE_ORACLE:
                    sql = "SELECT table_name FROM all_tables";
                    break;
                case SqlConstants.DBTYPE_DM:
                    sql = "SELECT table_name FROM all_tables";
                    break;
                case SqlConstants.DBTYPE_KINGBASE:
                    sql = "SELECT table_name FROM all_tables";
                    break;
                default:
                    sql = "show tables";
                    break;
            }
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tableName = rs.getString(1);
                // mysql中不处理视图
                al.put(tableName.toLowerCase(), tableName);
            }
        } catch (Exception ex) {
            LOGGER.error("查询所有的业务表失败", ex);
            throw ex;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
        return al;
    }

    public static List<Map<String, Object>> listTypes(DataSource dataSource) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstants.DBTYPE_MYSQL:
                    list = listTypes4Mysql();
                    break;
                case SqlConstants.DBTYPE_ORACLE:
                    list = listTypes4Oracle();
                    break;
                case SqlConstants.DBTYPE_DM:
                    list = listTypes4Dm();
                    break;
                case SqlConstants.DBTYPE_KINGBASE:
                    list = listTypes4Kingbase();
                    break;
                default:
                    list = listTypes4Mysql();
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return list;
    }

    private static List<Map<String, Object>> listTypes4Dm() {
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

    private static List<Map<String, Object>> listTypes4Kingbase() {
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

    private static List<Map<String, Object>> listTypes4Mysql() {
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

    private static List<Map<String, Object>> listTypes4Oracle() {
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
