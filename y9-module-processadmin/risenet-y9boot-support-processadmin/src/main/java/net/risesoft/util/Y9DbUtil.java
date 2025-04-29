package net.risesoft.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.util.DbUtil;
import org.slf4j.Logger;

/**
 * 重写DbUtil
 *
 * @author mengjuhua
 */
public class Y9DbUtil extends DbUtil {

    /**
     * 获取数据库类型（重写）添加Kingbase数据库适配
     *
     * @param dataSource 数据库
     * @param logger 日志组件
     * @param databaseTypeMappings 属性文件
     * @return
     */
    public static String determineDatabaseType(DataSource dataSource, Logger logger, Properties databaseTypeMappings) {
        Connection connection = null;
        String databaseType = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            logger.debug("database product name: '{}'", databaseProductName);

            // CRDB does not expose the version through the jdbc driver, so we need to fetch it through version().
            if (PRODUCT_NAME_POSTGRES.equalsIgnoreCase(databaseProductName)) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("select version() as version;");
                    ResultSet resultSet = preparedStatement.executeQuery()) {
                    String version = null;
                    if (resultSet.next()) {
                        version = resultSet.getString("version");
                    }

                    if (StringUtils.isNotEmpty(version)
                        && version.toLowerCase().startsWith(PRODUCT_NAME_CRDB.toLowerCase())) {
                        databaseProductName = PRODUCT_NAME_CRDB;
                        logger.info("CockroachDB version '{}' detected", version);
                    }
                }
            }

            databaseType = databaseTypeMappings.getProperty(databaseProductName);
            // Kingbase数据库适配
            if (databaseProductName.equals("KingbaseES")) {
                databaseType = DATABASE_TYPE_POSTGRES;
            }

            if (databaseType == null) {
                throw new FlowableException(
                    "couldn't deduct database type from database product name '" + databaseProductName + "'");
            }
            logger.debug("using database type: {}", databaseType);

        } catch (SQLException e) {
            throw new RuntimeException("Exception while initializing Database connection", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Exception while closing the Database connection", e);
            }
        }

        return databaseType;
    }
}
