package net.risesoft.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.DialectEnum;
import net.risesoft.service.SyncYearTableService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.util.Y9FileUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SyncYearTableServiceImpl implements SyncYearTableService {

    private final JdbcTemplate jdbcTemplate;

    private final JdbcTemplate jdbcTemplate4Public;

    private final SyncYearTableService self;

    public SyncYearTableServiceImpl(
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate,
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        @Lazy SyncYearTableService self) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.self = self;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("/n");
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
        Date date = new Date();
        // 每年的10月10号开始生成年度表结构
        String currentDate = Y9DateTimeUtils.formatCurrentDate();
        if (currentDate.endsWith("10-10")) {
            LOGGER.info("********************定时生成年度表结构开始**********************");
            String year = Y9DateTimeUtils.getYear(date);
            String year0 = String.valueOf((Integer.parseInt(year) + 1));
            List<String> list = jdbcTemplate4Public.queryForList("select id from y9_common_tenant", String.class);
            for (String tenantId : list) {
                try {
                    Y9LoginUserHolder.setTenantId(tenantId);
                    String sql = "SELECT count(t.id) FROM y9_common_tenant_system t"
                        + " LEFT JOIN y9_common_system s on t.system_id = s.ID WHERE t.tenant_id = ? and s.NAME = 'itemAdmin'";
                    int count = jdbcTemplate4Public.queryForObject(sql, Integer.class, tenantId);
                    if (count > 0) {
                        self.syncYearTable(year0);
                    } else {
                        LOGGER.info("********************该租户未租用事项管理系统:{}**********************", tenantId);
                    }
                } catch (Exception e) {
                    LOGGER.error("处理租户 {} 年度表结构时发生异常", tenantId, e);
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
    @Transactional
    @Override
    @SuppressWarnings("java:S2077")
    public Map<String, Object> syncYearTable(String year) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "生成年度表结构成功");
        try {
            if (StringUtils.isBlank(year)) {
                year = Y9DateTimeUtils.getYear(new Date());
            }
            Integer count = -1;
            try {
                count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM act_hi_actinst_" + year, Integer.class);
            } catch (Exception ignored) {
            }
            if (null != count && count >= 0) {
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", year + "年度表结构已存在！");
                LOGGER.info("************************{}年度表结构已存在！****************************", year);
                return map;
            }
            DataSource dataSource = jdbcTemplate.getDataSource();
            String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            String filePath = Y9Context.getWebRootRealPath() + "static" + File.separator + "yearTableSql"
                + File.separator + dialectName + File.separator + "yearTable.sql";
            File file = new File(filePath);
            if (!file.exists()) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "年度表sql文件不存在");
                LOGGER.info("************************年度表不存在：{}****************************", filePath);
                return map;
            }
            // 检查SQL文件安全性
            try (FileInputStream fis = new FileInputStream(file)) {
                String fileString = convertStreamToString(fis);
                fileString = fileString.toLowerCase().replaceAll("\\s*", "");
                if (fileString.contains("droptable")) {
                    LOGGER.info("********************sql文件中包含删除表的语法(drop table)**********************");
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "sql文件中包含删除表的语法(drop table)");
                    return map;
                } else if (fileString.contains("truncatetable")) {
                    LOGGER.info("********************sql文件中包含清空表数据的语法(truncate table)**********************");
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "sql文件中包含清空表数据的语法(truncate table)");
                    return map;
                }
            }
            // 执行SQL文件
            try (FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1) {
                    bos.write(b, 0, n);
                }
                String s = bos.toString(StandardCharsets.UTF_8);
                s = s.replace("Year4Table", year);
                List<String> sqlList = Y9FileUtil.loadSql(s);
                // 执行DDL语句
                if (DialectEnum.KINGBASE.getValue().equals(dialectName)
                    || DialectEnum.ORACLE.getValue().equals(dialectName)
                    || DialectEnum.DM.getValue().equals(dialectName)) {
                    DbMetaDataUtil.batchExecuteDdl4Kingbase(dataSource, sqlList);
                } else {
                    DbMetaDataUtil.batchExecuteDdl(dataSource, sqlList);
                }
                LOGGER.info("************************年度表生成成功****************************");
            }

        } catch (Exception e) {
            LOGGER.warn("************************年度表生成异常********************************", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "年度表生成发生异常");
        }
        return map;
    }
}
