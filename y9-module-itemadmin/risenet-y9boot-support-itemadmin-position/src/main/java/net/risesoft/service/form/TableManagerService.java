package net.risesoft.service.form;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.util.form.DdlKingbase;
import net.risesoft.util.form.DdlMysql;
import net.risesoft.util.form.DdlOracle;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbColumn;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
public class TableManagerService {

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final Y9TableRepository y9TableRepository;

    private final Y9TableFieldRepository y9TableFieldRepository;

    private String[] allFieldName = null;

    public TableManagerService(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant, Y9TableRepository y9TableRepository, Y9TableFieldRepository y9TableFieldRepository) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.y9TableRepository = y9TableRepository;
        this.y9TableFieldRepository = y9TableFieldRepository;
    }

    /**
     * 修改表结构
     *
     * @param td
     * @param dbcs
     * @return
     * @throws Exception
     */
    public Map<String, Object> addFieldToTable(Y9Table td, List<DbColumn> dbcs) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "操作成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            String tableName = td.getTableName();
            String tableId = td.getId();
            // 修改表
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            DataSource dataSource = jdbcTemplate4Tenant.getDataSource();
            Connection connection = dataSource.getConnection();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                DdlMysql ddLmysql = new DdlMysql();
                if (StringUtils.isNotBlank(td.getOldTableName()) && !td.getOldTableName().equalsIgnoreCase(tableName)) {
                    ddLmysql.renameTable(connection, td.getOldTableName(), tableName);
                    // 修改老表值
                    td.setOldTableName(td.getTableName());
                    this.saveOrUpdate(td);
                    LOGGER.info("修改表正常");
                }
                ddLmysql.addTableColumn(connection, tableName, dbcs);
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                DdlOracle ddLoracle = new DdlOracle();
                if (StringUtils.isNotBlank(td.getOldTableName()) && !td.getOldTableName().equalsIgnoreCase(tableName)) {
                    ddLoracle.renameTable(connection, td.getOldTableName(), tableName);
                    // 修改老表值
                    td.setOldTableName(td.getTableName());
                    this.saveOrUpdate(td);
                    LOGGER.info("修改表正常");
                }
                ddLoracle.addTableColumn(connection, tableName, dbcs);
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                DdlOracle ddLoracle = new DdlOracle();
                if (StringUtils.isNotBlank(td.getOldTableName()) && !td.getOldTableName().equalsIgnoreCase(tableName)) {
                    ddLoracle.renameTable(connection, td.getOldTableName(), tableName);
                    // 修改老表值
                    td.setOldTableName(td.getTableName());
                    this.saveOrUpdate(td);
                    LOGGER.info("修改表正常");
                }
                ddLoracle.addTableColumn(connection, tableName, dbcs);
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                DdlKingbase ddLkingbase = new DdlKingbase();
                if (StringUtils.isNotBlank(td.getOldTableName()) && !td.getOldTableName().equalsIgnoreCase(tableName)) {
                    ddLkingbase.renameTable(connection, td.getOldTableName(), tableName);
                    // 修改老表值
                    td.setOldTableName(td.getTableName());
                    this.saveOrUpdate(td);
                    LOGGER.info("修改表正常");
                }
                ddLkingbase.addTableColumn(connection, tableName, dbcs);
            }
            if (connection != null) {
                connection.close();
            }
            // 修改状态
            y9TableFieldRepository.updateState(tableId);
            LOGGER.info("修改字段正常");
        } catch (Exception ex) {
            map.put("msg", "操作失败");
            map.put(UtilConsts.SUCCESS, false);
            ex.printStackTrace();
        }
        return map;
    }

    /**
     * 创建表结构
     * @param td
     * @param dbcs
     * @return
     */
    public Map<String, Object> buildTable(Y9Table td, List<DbColumn> dbcs) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "操作成功");
        map.put(UtilConsts.SUCCESS, true);
        StringBuffer createSql = new StringBuffer();
        try {
            // 创建表
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            DataSource dataSource = jdbcTemplate4Tenant.getDataSource();
            Connection connection = dataSource.getConnection();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            String jsonDbColumns = Y9JsonUtil.writeValueAsString(dbcs);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                DdlMysql ddLmysql = new DdlMysql();
                if (StringUtils.isNotBlank(td.getOldTableName())) {
                    ddLmysql.dropTable(connection, td.getOldTableName());
                }
                ddLmysql.dropTable(connection, td.getTableName());
                ddLmysql.createTable(connection, td.getTableName(), jsonDbColumns);
            } else if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                DdlOracle ddLoracle = new DdlOracle();
                if (StringUtils.isNotBlank(td.getOldTableName())) {
                    ddLoracle.dropTable(connection, td.getOldTableName());
                }
                ddLoracle.dropTable(connection, td.getTableName());
                ddLoracle.createTable(connection, td.getTableName(), jsonDbColumns);
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                DdlOracle ddLoracle = new DdlOracle();
                if (StringUtils.isNotBlank(td.getOldTableName())) {
                    ddLoracle.dropTable(connection, td.getOldTableName());
                }
                ddLoracle.dropTable(connection, td.getTableName());
                ddLoracle.createTable(connection, td.getTableName(), jsonDbColumns);
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                DdlKingbase ddLkingbase = new DdlKingbase();
                if (StringUtils.isNotBlank(td.getOldTableName())) {
                    ddLkingbase.dropTable(connection, td.getOldTableName());
                }
                ddLkingbase.dropTable(connection, td.getTableName());
                ddLkingbase.createTable(connection, td.getTableName(), jsonDbColumns);
            }
            if (connection != null) {
                connection.close();
            }
            LOGGER.info("创建表正常");
            // 修改老表值
            td.setOldTableName(td.getTableName());
            this.saveOrUpdate(td);
            // 修改字段状态
            y9TableFieldRepository.updateState(td.getId());
            List<Y9TableField> fds = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(td.getId());
            for (Y9TableField y9TableField : fds) {
                y9TableField.setOldFieldName(y9TableField.getFieldName());
                y9TableFieldRepository.save(y9TableField);
            }
            LOGGER.info("创建表及字段正常");
        } catch (Exception ex1) {
            LOGGER.warn("失败操作语句：{}", createSql, ex1);
            map.put("msg", "操作失败");
            map.put(UtilConsts.SUCCESS, false);
        }
        return map;
    }

    /**
     * 按顺序提取定义的字段
     *
     * @param tableId
     */
    public void getAllFieldName(String tableId) {
        List<String> list = new ArrayList<String>();
        if (allFieldName != null) {
            return;
        }
        List<Y9TableField> list1 = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
        for (Y9TableField y9TableField : list1) {
            list.add(y9TableField.getFieldName());
        }
        if (list.size() > 0) {
            allFieldName = new String[list.size()];
            allFieldName = list.toArray(allFieldName);
        }
    }

    /**
     * 获取所有主键字段
     *
     * @param tableId
     * @return
     * @throws Exception
     */
    protected String getAllPrimaryKeyFields(String tableId) throws Exception {
        StringBuffer primaryKey = new StringBuffer();
        try {
            List<Y9TableField> list =
                y9TableFieldRepository.findByTableIdAndIsSystemFieldOrderByDisplayOrderAsc(tableId, 1);
            for (Y9TableField y9TableField : list) {
                String filedName = y9TableField.getFieldName();
                if (StringUtils.isNotBlank(filedName)) {
                    if (StringUtils.isEmpty(primaryKey.toString())) {
                        primaryKey.append(filedName);
                    } else {
                        primaryKey.append("," + filedName);
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return primaryKey.toString();
    }

    /**
     * 返回所有的业务表
     */
    public Map<String, Object> getDataSourceTableNames() {
        Map<String, Object> al = new HashMap<String, Object>(16);
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        String sql = "show tables";
        try {
            conn = jdbcTemplate4Tenant.getDataSource().getConnection();

            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(conn);
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables";
            }
            stmt = conn.createStatement();
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
                if (conn != null) {
                    conn.close();
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

    /**
     * 返回指定表中存在的字段
     *
     * @param tableId
     * @return
     */
    public Map<String, Object> getExistTableFields(String tableId) {
        Map<String, Object> al = new HashMap<String, Object>(16);
        String tableName = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Y9Table y9Table = y9TableRepository.findById(tableId).orElse(null);
            if (y9Table != null) {
                tableName = y9Table.getTableName();
            } else {
                return al;
            }
            conn = jdbcTemplate4Tenant.getDataSource().getConnection();

            String sql = "show tables like '" + tableName + "'";

            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(conn);
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables where table_name = '" + tableName + "'";
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables where table_name = '" + tableName + "'";
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sql = "SELECT table_name FROM all_tables where table_name = '" + tableName + "'";
            }

            List<Map<String, Object>> list = jdbcTemplate4Tenant.queryForList(sql);
            if (list == null || list.size() == 0) {
                return al;
            }
            sql = "Select * from " + tableName + " limit 0,0";

            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                sql = "Select * from \"" + tableName + "\"  where rownum = 0";
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                sql = "Select * from \"" + tableName + "\"  where rownum = 0";
            } else if (DialectEnum.KINGBASE.getValue().equals(dialect)) {
                sql = "Select * from \"" + tableName + "\"  where rownum = 0";
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData dt = rs.getMetaData();
            for (int i = 0; i < dt.getColumnCount(); i++) {
                String fieldName = dt.getColumnName(i + 1).toLowerCase();
                al.put(fieldName, fieldName);
            }

            /*
             * List<DbColumn> listMapTemp =
             * DbMetaDataUtil.listAllColumns(jdbcTemplate4Tenant.getDataSource(), tableName,
             * null); for (DbColumn column : listMapTemp) { String fieldName =
             * column.getColumn_name().toLowerCase(); al.put(fieldName, fieldName); }
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
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

    /**
     * 对应表的insert语句
     *
     * @param tableName
     * @param fieldList
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getInsertSqlStatement(String tableName, ArrayList fieldList) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        boolean isHaveField = false;
        StringBuffer sqlStr = new StringBuffer("");
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            List<DbColumn> list = dbMetaDataUtil.listAllColumns(connection, tableName, "");
            StringBuffer sqlStr1 = new StringBuffer(") values(");
            sqlStr.append("insert into " + tableName + " (");
            for (DbColumn column : list) {
                fieldList.add(column.getColumnName());
                if (isHaveField) {
                    sqlStr.append(",");
                }
                sqlStr.append(column.getColumnName());

                if (isHaveField) {
                    sqlStr1.append(",");
                }
                sqlStr1.append("?");

                isHaveField = true;
            }
            sqlStr1.append(")");
            sqlStr.append(sqlStr1);
            LOGGER.info("表" + tableName + "的insert语句：" + sqlStr.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return sqlStr.toString();
    }

    /**
     * 返回前一字段的名称
     *
     * @param fieldName
     * @return
     */
    public String getPreFieldName(String fieldName) {
        String strPreFieldName = null;
        int i = 0;
        for (; i < allFieldName.length; i++) {
            if (allFieldName[i].equals(fieldName)) {
                break;
            }
        }
        if (i < allFieldName.length && i > 0) {
            strPreFieldName = " after " + allFieldName[i - 1];
        } else if (i == 0) {
            strPreFieldName = " first";
        }
        if (strPreFieldName == null) {
            strPreFieldName = "";
        }
        return strPreFieldName;
    }

    /**
     * 对应表的update语句
     *
     * @param tableName
     * @return
     */
    public String getUpdateSqlStatement(String tableName) {
        DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
        Connection connection = null;
        boolean isHaveField = false;
        StringBuffer sqlStr = new StringBuffer("");
        try {
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            List<DbColumn> list = dbMetaDataUtil.listAllColumns(connection, tableName, "");
            sqlStr.append("update " + tableName + " set ");
            StringBuffer sqlStr1 = new StringBuffer("");
            for (DbColumn column : list) {
                if (column.getPrimaryKey()) {
                    sqlStr1.append(" where " + column.getColumnName() + "=?");
                    continue;
                }
                if (isHaveField) {
                    sqlStr.append(",");
                }
                sqlStr.append(column.getColumnName() + "=?");
                isHaveField = true;
            }
            sqlStr.append(sqlStr1);
            LOGGER.info("表" + tableName + "的update语句：" + sqlStr.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return sqlStr.toString();
    }

    public void saveOrUpdate(Y9Table table) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection connection = null;
        try {
            if (StringUtils.isBlank(table.getId())) {
                table.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = jdbcTemplate4Tenant.getDataSource().getConnection();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialect)) {
                table.setTableName(table.getTableName().toLowerCase());
            }
            table.setCreateTime(sdf.format(new Date()));
            y9TableRepository.save(table);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Y9TableServiceImpl saveOrUpdate error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
