package net.risesoft.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.risesoft.enums.DialectEnum;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class Y9SqlPaginationUtil {
    private static Logger log = LoggerFactory.getLogger(Y9SqlPaginationUtil.class);

    private static String MICROSOFT = "microsoft";
    private static String ORDERBY = "order by";
    private static int VERSION = 12;

    private static String dbType;
    private static int dbVersion;

    public static String generatePagedSql(DataSource ds, String sql, int start, int limit) throws Exception {
        String rSql = "";
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }

        if (dbType == null) {
            Connection connection = null;
            try {
                connection = ds.getConnection();
                DatabaseMetaData dbmd = connection.getMetaData();
                String databaseName = dbmd.getDatabaseProductName().toLowerCase();
                if (databaseName.indexOf(DialectEnum.MYSQL.getValue()) > -1) {
                    dbType = "mysql";
                } else if (databaseName.indexOf(DialectEnum.ORACLE.getValue()) > -1) {
                    dbType = "oracle";
                } else if (databaseName.indexOf(MICROSOFT) > -1) {
                    dbType = "mssql";
                } else if (databaseName.indexOf(DialectEnum.DM.getValue()) > -1) {
                    dbType = "dm";
                } else if (databaseName.indexOf(DialectEnum.KINGBASE.getValue()) > -1) {
                    dbType = "kingbase";
                }

                dbVersion = dbmd.getDatabaseMajorVersion();
            } catch (SQLException e) {
                log.error(e.getMessage());
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }
            }
        }

        rSql = generatePagedSql(dbType, dbVersion, sql, start, limit);
        return rSql;
    }

    public static String generatePagedSql(String databaseType, int databaseVersion, String sql, int start, int limit)
        throws Exception {
        String rSql = "";
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }

        if (databaseType.equalsIgnoreCase(DialectEnum.MYSQL.getValue())) {
            rSql = sql + " limit " + start + "," + limit;
        } else if (databaseType.equalsIgnoreCase(DialectEnum.MSSQL.getValue())) {
            if (databaseVersion >= VERSION) {
                if (sql.toLowerCase().contains(ORDERBY)) {
                    // 只适用mssql2012版本
                    rSql = sql + " OFFSET " + start + " ROW FETCH NEXT " + limit + " rows only";
                } else {
                    rSql = "SELECT TOP " + limit
                        + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql
                        + ") B ) A WHERE A.RowNumber > " + start;
                }
            } else {
                rSql = "SELECT TOP " + limit
                    + " A.* FROM ( SELECT ROW_NUMBER() OVER (ORDER BY (select NULL)) AS RowNumber,B.* FROM ( " + sql
                    + ") B ) A WHERE A.RowNumber > " + start;
            }
        } else if (databaseType.equalsIgnoreCase(DialectEnum.ORACLE.getValue())) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        } else if (databaseType.equalsIgnoreCase(DialectEnum.MYSQL.getValue())) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        } else if (databaseType.equalsIgnoreCase(DialectEnum.KINGBASE.getValue())) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        }

        return rSql;
    }

}
