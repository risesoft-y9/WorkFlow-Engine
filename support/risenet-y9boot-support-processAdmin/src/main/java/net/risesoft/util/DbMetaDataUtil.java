package net.risesoft.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.risesoft.enums.DialectEnum;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class DbMetaDataUtil {
    private static Logger log = LoggerFactory.getLogger(DbMetaDataUtil.class);

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

    public String getDatabaseDialectName(Connection connection) {
        String databaseName = "";
        try {
            databaseName = getDatabaseProductName(connection).toLowerCase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String microsoft = "microsoft";
        if (databaseName.indexOf(DialectEnum.MYSQL.getValue()) > -1) {
            return "mysql";
        } else if (databaseName.indexOf(DialectEnum.ORACLE.getValue()) > -1) {
            return "oracle";
        } else if (databaseName.indexOf(DialectEnum.DM.getValue()) > -1) {
            return "dm";
        } else if (databaseName.indexOf(microsoft) > -1) {
            return "mssql";
        } else if (databaseName.indexOf(DialectEnum.KINGBASE.getValue()) > -1) {
            return "kingbase";
        }

        return "";
    }

}
