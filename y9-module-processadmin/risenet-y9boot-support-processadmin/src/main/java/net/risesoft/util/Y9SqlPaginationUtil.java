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
 * @date 2022/12/30
 */
public class Y9SqlPaginationUtil {

    private static final Logger log = LoggerFactory.getLogger(Y9SqlPaginationUtil.class);

    private static String dbType;
    private static int dbVersion;

    public static String generatePagedSql(DataSource ds, String sql, int start, int limit) {
        String rSql;
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }
        if (dbType == null) {
            String microsoft = "microsoft";
            try (Connection connection = ds.getConnection()) {
                DatabaseMetaData dmd = connection.getMetaData();
                String databaseName = dmd.getDatabaseProductName().toLowerCase();
                if (databaseName.contains(DialectEnum.MYSQL.getValue())) {
                    dbType = "mysql";
                } else if (databaseName.contains(DialectEnum.ORACLE.getValue())) {
                    dbType = "oracle";
                } else if (databaseName.contains(microsoft)) {
                    dbType = "mssql";
                } else if (databaseName.contains(DialectEnum.KINGBASE.getValue())) {
                    dbType = "kingbase";
                } else {
                    dbType = "oracle";
                }
                dbVersion = dmd.getDatabaseMajorVersion();
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        rSql = generatePagedSql(dbType, dbVersion, sql, start, limit);
        return rSql;
    }

    public static String generatePagedSql(String databaseType, int databaseVersion, String sql, int start, int limit) {
        String rSql = "";
        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }
        if (DialectEnum.MYSQL.getValue().equalsIgnoreCase(databaseType)) {
            rSql = sql + " limit " + start + "," + limit;
        } else if (DialectEnum.MSSQL.getValue().equalsIgnoreCase(databaseType)) {
            int databaseVersion12 = 12;
            if (databaseVersion >= databaseVersion12) {
                String orderBy = " order by ";
                if (sql.toLowerCase().contains(orderBy)) {
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
        } else if (DialectEnum.ORACLE.getValue().equalsIgnoreCase(databaseType)) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        } else if (DialectEnum.KINGBASE.getValue().equalsIgnoreCase(databaseType)) {
            rSql = "select * from (select mytable.*,rownum as my_rownum from (" + sql + ") mytable) where my_rownum<="
                + (start + limit) + " and my_rownum>" + start;
        }
        return rSql;
    }
}