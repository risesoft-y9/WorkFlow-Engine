package net.risesoft.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.DialectEnum;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9FileUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Slf4j
public class SyncYearTableService {

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate4Public;

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 定时生成年度表结构每天2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncYearTable() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        /**
         * 每年的12月20号开始生成年度表结构
         */
        boolean is1210 = sdf.format(date).contains("12-10");
        if (is1210) {
            LOGGER.info("********************定时生成年度表结构开始**********************");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
            String year = sdf1.format(date);
            String year0 = String.valueOf((Integer.parseInt(year) + 1));
            List<String> list = jdbcTemplate4Public.queryForList("select id from rs_common_tenant", String.class);
            for (String tenantId : list) {
                Y9LoginUserHolder.setTenantId(tenantId);
                String sql = "SELECT" + "	count(t.ID)" + " FROM" + "	rs_common_tenant_system t"
                    + " LEFT JOIN rs_common_system s on t.SYSTEMID = s.ID" + " WHERE" + "	t.TENANTID = '" + tenantId
                    + "'" + " and s.SYSTEMNAME = 'itemAdmin'";
                int count = jdbcTemplate4Public.queryForObject(sql, Integer.class);
                if (count > 0) {
                    this.syncYearTable(year0);
                } else {
                    LOGGER.info("********************该租户未租用事项管理系统:{}**********************", tenantId);
                }
            }
            LOGGER.info("********************定时生成年度表结构结束**********************");
        }
    }

    /**
     * 生成年度表结构
     *
     * @return
     */
    @Transactional(readOnly = false)
    public Map<String, Object> syncYearTable(String year) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "生成年度表结构成功");
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        Connection connection = null;
        try {
            if (StringUtils.isBlank(year)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                year = sdf.format(new Date());
            }
            DataSource dataSource = jdbcTemplate.getDataSource();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = dataSource.getConnection();
            String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
            String filePath = Y9Context.getWebRootRealPath() + "static" + File.separator + "yearTableSql"
                + File.separator + dialectName + File.separator + "yearTable.sql";
            File file = new File(filePath);
            byte[] buffer = null;
            String droptable = "droptable";
            String truncatetable = "truncatetable";
            if (file.exists()) {
                fis = new FileInputStream(file);
                String fileString = convertStreamToString(fis);
                fileString = fileString.toLowerCase().replaceAll("\\s*", "");
                if (fileString.indexOf(droptable) > 0) {
                    LOGGER.info("********************sql文件中包含删除表的语法(drop table)**********************");
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "sql文件中包含删除表的语法(drop table)");
                    if (fis != null) {
                        fis.close();
                    }
                    return map;
                } else if (fileString.indexOf(truncatetable) > 0) {
                    LOGGER.info("********************sql文件中包含清空表数据的语法(truncate table)**********************");
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "sql文件中包含清空表数据的语法(truncate table)");
                    if (fis != null) {
                        fis.close();
                    }
                    return map;
                }
                fis = new FileInputStream(file);
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                buffer = bos.toByteArray();
                String s = new String(buffer, "UTF-8");
                s = s.replace("Year4Table", year);
                List<String> sqlList = Y9FileUtil.loadSql(s, dialectName);
                if (DialectEnum.KINGBASE.getValue().equals(dialectName)) {
                    dbMetaDataUtil.batchexecuteDdl4Kingbase(connection, sqlList);
                } else if (DialectEnum.ORACLE.getValue().equals(dialectName)) {
                    dbMetaDataUtil.batchexecuteDdl4Kingbase(connection, sqlList);
                } else if (DialectEnum.DM.getValue().equals(dialectName)) {
                    dbMetaDataUtil.batchexecuteDdl4Kingbase(connection, sqlList);
                } else {
                    dbMetaDataUtil.batchexecuteDdl(connection, sqlList);
                }
                LOGGER.info("************************年度表生成成功****************************");
            } else {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "年度表sql文件不存在");
                LOGGER.info("************************年度表不存在：{}****************************", filePath);
            }
        } catch (Exception e) {
            LOGGER.warn("************************年度表生成异常********************************", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "年度表生成发生异常");
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
