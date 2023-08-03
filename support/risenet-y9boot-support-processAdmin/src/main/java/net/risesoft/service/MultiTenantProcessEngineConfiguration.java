package net.risesoft.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.stereotype.Service;

import com.alibaba.druid.pool.DruidDataSource;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.base64.Y9Base64Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
public class MultiTenantProcessEngineConfiguration extends MultiSchemaMultiTenantProcessEngineConfiguration {

    private static Logger logger = LoggerFactory.getLogger(MultiTenantProcessEngineConfiguration.class);

    public static final String SYSTEM_ID = "11111111-1111-1111-1111-111111111100";

    private static TenantInfoHolder getFlowableTenantInfoHolder() {
        FlowableTenantInfoHolder flowableTenantInfoHolder = new FlowableTenantInfoHolder();
        flowableTenantInfoHolder.addTenant(null);
        return flowableTenantInfoHolder;
    }

    private JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();

    @Resource(name = "jdbcTemplate4Public")
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "y9FlowableDS")
    private DruidDataSource defaultDataSource;

    @Autowired
    private Y9Properties y9Config;

    public MultiTenantProcessEngineConfiguration() {
        super(getFlowableTenantInfoHolder());
    }

    public MultiTenantProcessEngineConfiguration(TenantInfoHolder tenantInfoHolder) {
        super(tenantInfoHolder);
    }

    @Override
    public ProcessEngine buildProcessEngine() {

        createSystem(y9Config.getApp().getProcessAdmin().getSystemName());// 创建系统
        createTenantSystem(y9Config.getApp().getProcessAdmin().getSystemName());// 租户租用系统

        /**
         * 1设置默认的租户数据源
         */
        registerTenant(AbstractEngineConfiguration.NO_TENANT_ID, defaultDataSource);

        ProcessEngine processEngine = super.buildProcessEngine();
        /**
         * 2系统存在的话查看租户租用系统的数据源
         */
        List<Map<String, Object>> systems = jdbcTemplate.queryForList(
            "SELECT ID FROM Y9_COMMON_SYSTEM T WHERE T.NAME=?", y9Config.getApp().getProcessAdmin().getSystemName());
        if (systems.size() > 0) {
            Map<String, Object> map = systems.get(0);
            String systemId = (String)map.get("ID");
            List<Map<String, Object>> tenantSystems = jdbcTemplate.queryForList(
                "SELECT TENANT_ID, TENANT_DATA_SOURCE FROM Y9_COMMON_TENANT_SYSTEM T WHERE T.SYSTEM_ID = ?", systemId);
            if (tenantSystems.isEmpty()) {
                createDefaultTenantDataSource();
            } else {
                boolean isCreateDefaultTenantDataSource = false;
                for (Map<String, Object> tenantSystem : tenantSystems) {
                    String tenantId = (String)tenantSystem.get("TENANT_ID");
                    String dataSourceId = (String)tenantSystem.get("TENANT_DATA_SOURCE");
                    List<Map<String, Object>> list3 =
                        jdbcTemplate.queryForList("select * from Y9_COMMON_DATASOURCE t where t.id = ?", dataSourceId);
                    if (list3.size() > 0) {
                        registerTenant(tenantId, list3.get(0));
                        if (tenantId.equals(DefaultIdConsts.TENANT_ID)) {
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
        List<Map<String, Object>> defaultTenant = jdbcTemplate.queryForList(
            "SELECT ID, DEFAULT_DATA_SOURCE_ID FROM Y9_COMMON_TENANT WHERE ID=?", DefaultIdConsts.TENANT_ID);
        List<Map<String, Object>> defaultDataSource = jdbcTemplate
            .queryForList("SELECT * FROM Y9_COMMON_DATASOURCE T WHERE T.ID = ?", DefaultIdConsts.DATASOURCE_ID);
        if (!defaultTenant.isEmpty() && !defaultDataSource.isEmpty()) {
            String defaultTenantId = defaultTenant.get(0).get("ID").toString();
            registerTenant(defaultTenantId, defaultDataSource.get(0));
        }
    }

    private void createSystem(String systemName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "select * from y9_common_system where NAME = '" + systemName + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() == 0) {
                sql =
                    "INSERT INTO y9_common_system (ID, CONTEXT_PATH, NAME, CN_NAME, TAB_INDEX,ENABLED,AUTO_INIT,CREATE_TIME) VALUES ('"
                        + SYSTEM_ID + "', 'processAdmin', '" + systemName + "', '流程管理', 100,1,1,'"
                        + sdf.format(new Date()) + "')";
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTenantSystem(String systemName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "select * from y9_common_system where NAME = '" + systemName + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from Y9_COMMON_TENANT where TENANT_TYPE = 3";
                List<Map<String, Object>> tlist = jdbcTemplate.queryForList(sql);
                for (Map<String, Object> map : tlist) {
                    sql = "select * from y9_common_tenant_system where TENANT_ID = '" + map.get("ID").toString()
                        + "' and SYSTEM_ID = '" + smap.get("ID").toString() + "'";
                    List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql);
                    if (qlist.size() == 0) {
                        Long id = System.currentTimeMillis();
                        String sql1 =
                            "INSERT INTO y9_common_tenant_system (ID, SYSTEM_ID, TENANT_ID, TENANT_DATA_SOURCE, CREATE_TIME) VALUES ('"
                                + id + "', '" + smap.get("ID").toString() + "', '" + map.get("ID").toString() + "', '"
                                + map.get("DEFAULT_DATA_SOURCE_ID").toString() + "','" + sdf.format(new Date()) + "')";
                        jdbcTemplate.execute(sql1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerTenant(String tenantId, Map<String, Object> dsMap) {
        Integer type = Integer.valueOf(dsMap.get("TYPE").toString());
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

            Integer initialSize = Integer.valueOf(dsMap.get("INITIAL_SIZE").toString());
            Integer maxActive = Integer.valueOf(dsMap.get("MAX_ACTIVE").toString());
            Integer minIdle = Integer.valueOf(dsMap.get("MIN_IDLE").toString());

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
