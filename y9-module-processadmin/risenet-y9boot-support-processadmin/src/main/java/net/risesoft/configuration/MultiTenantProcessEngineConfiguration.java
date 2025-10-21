package net.risesoft.configuration;

import java.util.List;
import java.util.Map;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.base64.Y9Base64Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@Component
public class MultiTenantProcessEngineConfiguration extends MultiSchemaMultiTenantProcessEngineConfiguration {

    public static final String SYSTEM_ID = "11111111-1111-1111-1111-111111111100";

    private final JdbcTemplate jdbcTemplate4Public;
    private final DruidDataSource defaultDataSource;
    private final Y9Properties y9Properties;

    private final JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();

    public MultiTenantProcessEngineConfiguration(
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        @Qualifier("y9FlowableDS") DruidDataSource defaultDataSource,
        Y9Properties y9Properties) {
        super(getFlowableTenantInfoHolder());
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.defaultDataSource = defaultDataSource;
        this.y9Properties = y9Properties;
    }

    private static TenantInfoHolder getFlowableTenantInfoHolder() {
        FlowableTenantInfoHolder flowableTenantInfoHolder = new FlowableTenantInfoHolder();
        flowableTenantInfoHolder.addTenant(null);
        return flowableTenantInfoHolder;
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        createSystem(y9Properties.getSystemName());// 创建系统
        createTenantSystem(y9Properties.getSystemName());// 租户租用系统
        LOGGER.info("start registerTenant");
        /*
         * 设置默认的租户数据源
         */
        registerTenant(AbstractEngineConfiguration.NO_TENANT_ID, defaultDataSource);

        ProcessEngine processEngine = super.buildProcessEngine();
        /*
         * 系统存在的话查看租户租用系统的数据源
         */
        List<Map<String, Object>> systems = jdbcTemplate4Public
            .queryForList("SELECT ID FROM Y9_COMMON_SYSTEM T WHERE T.NAME=?", y9Properties.getSystemName());
        if (!systems.isEmpty()) {
            Map<String, Object> map = systems.get(0);
            String systemId = (String)map.get("ID");
            List<Map<String, Object>> tenantSystems = jdbcTemplate4Public.queryForList(
                "SELECT TENANT_ID, TENANT_DATA_SOURCE FROM Y9_COMMON_TENANT_SYSTEM T WHERE T.SYSTEM_ID = ?", systemId);
            if (tenantSystems.isEmpty()) {
                createDefaultTenantDataSource();
            } else {
                boolean isCreateDefaultTenantDataSource = false;
                for (Map<String, Object> tenantSystem : tenantSystems) {
                    String tenantId = (String)tenantSystem.get("TENANT_ID");
                    String dataSourceId = (String)tenantSystem.get("TENANT_DATA_SOURCE");
                    List<Map<String, Object>> list3 = jdbcTemplate4Public
                        .queryForList("select * from Y9_COMMON_DATASOURCE t where t.id = ?", dataSourceId);
                    if (!list3.isEmpty()) {
                        registerTenant(tenantId, list3.get(0));
                        if (tenantId.equals(InitDataConsts.TENANT_ID)) {
                            isCreateDefaultTenantDataSource = true;
                        }
                    }
                }
                if (!isCreateDefaultTenantDataSource) {
                    createDefaultTenantDataSource();
                }
            }
        } else {
            createDefaultTenantDataSource();
        }
        return processEngine;
    }

    /**
     * 初始化默认租户数据源
     */
    private void createDefaultTenantDataSource() {
        List<Map<String, Object>> defaultTenant = jdbcTemplate4Public.queryForList(
            "SELECT ID, DEFAULT_DATA_SOURCE_ID FROM Y9_COMMON_TENANT WHERE ID=?", InitDataConsts.TENANT_ID);
        List<Map<String, Object>> defaultDataSource = jdbcTemplate4Public
            .queryForList("SELECT * FROM Y9_COMMON_DATASOURCE T WHERE T.ID = ?", InitDataConsts.DATASOURCE_ID);
        if (!defaultTenant.isEmpty() && !defaultDataSource.isEmpty()) {
            String defaultTenantId = defaultTenant.get(0).get("ID").toString();
            registerTenant(defaultTenantId, defaultDataSource.get(0));
        }
    }

    private void createSystem(String systemName) {
        try {
            String sql = "select * from y9_common_system where NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate4Public.queryForList(sql, systemName);
            if (list.isEmpty()) {
                sql =
                    "INSERT INTO y9_common_system (ID, CONTEXT_PATH, NAME, CN_NAME, TAB_INDEX, ENABLED, AUTO_INIT, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate4Public.update(sql, SYSTEM_ID, "processAdmin", systemName, "流程管理", 100, 1, 1,
                    Y9DateTimeUtils.formatCurrentDateTime());
            }
        } catch (Exception e) {
            LOGGER.error("在数字底座创建[流程管理]系统失败", e);
        }
    }

    private void createTenantSystem(String systemName) {
        try {
            String sql = "select * from y9_common_system where NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate4Public.queryForList(sql, systemName);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from Y9_COMMON_TENANT";
                List<Map<String, Object>> tlist = jdbcTemplate4Public.queryForList(sql);
                for (Map<String, Object> map : tlist) {
                    sql = "select * from y9_common_tenant_system where TENANT_ID = ? and SYSTEM_ID = ?";
                    List<Map<String, Object>> qlist =
                        jdbcTemplate4Public.queryForList(sql, map.get("ID").toString(), smap.get("ID").toString());
                    if (qlist.isEmpty()) {
                        long id = System.currentTimeMillis();
                        sql =
                            "INSERT INTO y9_common_tenant_system (ID, SYSTEM_ID, TENANT_ID, TENANT_DATA_SOURCE, CREATE_TIME) VALUES (?, ?, ?, ?, ?)";
                        jdbcTemplate4Public.update(sql, id, smap.get("ID").toString(), map.get("ID").toString(),
                            map.get("DEFAULT_DATA_SOURCE_ID").toString(), Y9DateTimeUtils.formatCurrentDateTime());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("createTenantSystem error {}", e.getMessage());
        }
    }

    private void registerTenant(String tenantId, Map<String, Object> dsMap) {
        int type = Integer.parseInt(dsMap.get("TYPE").toString());
        String jndiName = (String)dsMap.get("JNDI_NAME");
        if (type == 1) {
            try {
                DruidDataSource dataSource = (DruidDataSource)this.jndiDataSourceLookup.getDataSource(jndiName);
                registerTenant(tenantId, dataSource);
            } catch (DataSourceLookupFailureException e) {
                logger.error(e.getMessage());
            }
        } else {
            String url = (String)dsMap.get("URL");
            String username = (String)dsMap.get("USERNAME");
            String password = (String)dsMap.get("PASSWORD");
            String driver = dsMap.get("DRIVER") != null ? (String)dsMap.get("DRIVER") : "";
            password = Y9Base64Util.decode(password);

            int initialSize = Integer.parseInt(dsMap.get("INITIAL_SIZE").toString());
            int maxActive = Integer.parseInt(dsMap.get("MAX_ACTIVE").toString());
            int minIdle = Integer.parseInt(dsMap.get("MIN_IDLE").toString());

            DruidDataSource ds = new DruidDataSource();
            ds.setTestOnBorrow(true);
            ds.setTestOnReturn(true);
            ds.setTestWhileIdle(true);
            ds.setInitialSize(initialSize);
            ds.setMaxActive(maxActive);
            ds.setMinIdle(minIdle);
            ds.setValidationQuery("SELECT 1 FROM DUAL");
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setTimeBetweenConnectErrorMillis(60000);
            if (!"".equals(driver)) {
                ds.setDriverClassName(driver);
            }
            registerTenant(tenantId, ds);
        }
    }
}
