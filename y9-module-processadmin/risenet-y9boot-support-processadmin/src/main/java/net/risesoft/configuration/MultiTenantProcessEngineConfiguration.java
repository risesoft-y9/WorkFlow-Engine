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

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.consts.InitDataConsts;
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

    private final JdbcTemplate jdbcTemplate4Public;
    private final HikariDataSource defaultDataSource;
    private final Y9Properties y9Properties;
    private final SystemApi systemApi;

    private final JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();

    public MultiTenantProcessEngineConfiguration(
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        @Qualifier("y9FlowableDS") HikariDataSource defaultDataSource,
        Y9Properties y9Properties,
        SystemApi systemApi) {
        super(getFlowableTenantInfoHolder());
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.defaultDataSource = defaultDataSource;
        this.y9Properties = y9Properties;
        this.systemApi = systemApi;
    }

    private static TenantInfoHolder getFlowableTenantInfoHolder() {
        FlowableTenantInfoHolder flowableTenantInfoHolder = new FlowableTenantInfoHolder();
        flowableTenantInfoHolder.addTenant(null);
        return flowableTenantInfoHolder;
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        String systemId = createSystem(y9Properties.getSystemName());
        LOGGER.info("start registerTenant");
        // 设置默认的数据源(y9_flowable)
        registerTenant(AbstractEngineConfiguration.NO_TENANT_ID, defaultDataSource);
        ProcessEngine processEngine = super.buildProcessEngine();
        // 初始化租户数据源配置
        initializeTenantDataSources(systemId);
        return processEngine;
    }

    private String createSystem(String systemName) {
        String systemId = "";
        try {
            net.risesoft.model.platform.System system = systemApi.getByName(systemName).getData();
            if (system == null) {
                // 注册系统并自动租用
                system = systemApi.registrySystem(systemName, "流程管理", "/server-" + systemName, InitDataConsts.TENANT_ID)
                    .getData();

            }
            systemId = system.getId();
        } catch (Exception e) {
            LOGGER.error("在数字底座创建[流程管理]系统失败", e);
        }
        return systemId;
    }

    /**
     * 初始化租户数据源配置
     */
    private void initializeTenantDataSources(String systemId) {
        if (systemId == null) {
            createDefaultTenantDataSource();
            return;
        }

        List<Map<String, Object>> tenantSystems = getTenantSystemsBySystemId(systemId);
        registerTenantDataSources(tenantSystems);
    }

    /**
     * 根据系统ID获取租户系统列表
     */
    private List<Map<String, Object>> getTenantSystemsBySystemId(String systemId) {
        return jdbcTemplate4Public.queryForList(
            "SELECT TENANT_ID, TENANT_DATA_SOURCE FROM Y9_COMMON_TENANT_SYSTEM T WHERE T.SYSTEM_ID = ?", systemId);
    }

    /**
     * 注册租户数据源
     *
     * @return 是否注册了默认租户数据源
     */
    private boolean registerTenantDataSources(List<Map<String, Object>> tenantSystems) {
        boolean isDefaultTenantRegistered = false;

        for (Map<String, Object> tenantSystem : tenantSystems) {
            String tenantId = (String)tenantSystem.get("TENANT_ID");
            String dataSourceId = (String)tenantSystem.get("TENANT_DATA_SOURCE");

            List<Map<String, Object>> dataSourceList =
                jdbcTemplate4Public.queryForList("select * from Y9_COMMON_DATASOURCE t where t.id = ?", dataSourceId);

            if (!dataSourceList.isEmpty()) {
                registerTenant(tenantId, dataSourceList.get(0));
                if (tenantId.equals(InitDataConsts.TENANT_ID)) {
                    isDefaultTenantRegistered = true;
                }
            }
        }

        return isDefaultTenantRegistered;
    }

    /**
     * 初始化默认租户数据源
     */
    private void createDefaultTenantDataSource() {
        List<Map<String, Object>> defaultTenant = jdbcTemplate4Public.queryForList(
            "SELECT ID, DEFAULT_DATA_SOURCE_ID FROM Y9_COMMON_TENANT WHERE ID=?", InitDataConsts.TENANT_ID);
        if (!defaultTenant.isEmpty()) {
            String defaultTenantId = defaultTenant.get(0).get("ID").toString();
            String defaultDataSourceId = defaultTenant.get(0).get("DEFAULT_DATA_SOURCE_ID").toString();
            List<Map<String, Object>> defaultDataSource = jdbcTemplate4Public
                .queryForList("SELECT * FROM Y9_COMMON_DATASOURCE T WHERE T.ID = ?", defaultDataSourceId);
            if (!defaultDataSource.isEmpty()) {
                registerTenant(defaultTenantId, defaultDataSource.get(0));
            }
        }
    }

    private void registerTenant(String tenantId, Map<String, Object> dsMap) {
        int type = Integer.parseInt(dsMap.get("TYPE").toString());
        String jndiName = (String)dsMap.get("JNDI_NAME");
        if (type == 1) {
            try {
                HikariDataSource dataSource = (HikariDataSource)this.jndiDataSourceLookup.getDataSource(jndiName);
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

            HikariDataSource ds = new HikariDataSource();
            ds.setMaximumPoolSize(maxActive);
            ds.setMinimumIdle(minIdle);
            ds.setJdbcUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
            if (driver.length() > 0) {
                ds.setDriverClassName(driver);
            }
            registerTenant(tenantId, ds);
        }
    }
}
