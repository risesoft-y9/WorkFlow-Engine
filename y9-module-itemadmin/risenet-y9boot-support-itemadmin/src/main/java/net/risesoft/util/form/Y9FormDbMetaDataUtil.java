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
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9FormDbMetaDataUtil extends DbMetaDataUtil {

    /**
     * 返回所有的业务表
     */
    public static Map<String, Object> listAllTableNames(DataSource dataSource) throws Exception {
        Map<String, Object> al = new HashMap<>(16);

        ResultSet rs = null;
        String sql;
        try (Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()) {

            String dialect = getDatabaseDialectNameByConnection(connection);
            switch (dialect) {
                case SqlConstants.DBTYPE_ORACLE:
                case SqlConstants.DBTYPE_DM:
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

    public static List<Map<String, Object>> listTypes(DataSource dataSource) {
        List<Map<String, Object>> list;
        try {
            String dialect = getDatabaseDialectName(dataSource);
            switch (dialect) {
                case SqlConstants.DBTYPE_ORACLE:
                case SqlConstants.DBTYPE_DM:
                case SqlConstants.DBTYPE_KINGBASE:
                    list = listTypes4Common();
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

    private static List<Map<String, Object>> listTypes4Mysql() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] typeNames =
            {"varchar", "int", "integer", "double", "float", "longblob", "text", "longtext", "datetime", "date", "bit"};
        for (String typeName : typeNames) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("typeName", typeName);
            list.add(map);
        }
        return list;
    }

    private static List<Map<String, Object>> listTypes4Common() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] typeNames = {"VARCHAR2", "NUMBER", "FLOAT", "BINARY_FLOAT", "BINARY_DOUBLE", "LONG", "TIMESTAMP",
            "BLOB", "DATE", "INTEGER", "CLOB"};
        for (String typeName : typeNames) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("typeName", typeName);
            list.add(map);
        }
        return list;
    }
}
