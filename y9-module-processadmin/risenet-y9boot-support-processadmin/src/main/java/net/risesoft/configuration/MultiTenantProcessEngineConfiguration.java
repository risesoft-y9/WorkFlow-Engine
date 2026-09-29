package net.risesoft.configuration;

import java.util.Map;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.model.platform.System;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@Component
public class MultiTenantProcessEngineConfiguration extends MultiSchemaMultiTenantProcessEngineConfiguration {

    private final DruidDataSource defaultDataSource;

    private final Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    private final SystemApi systemApi;

    private final Y9Properties y9Properties;

    public MultiTenantProcessEngineConfiguration(
        @Qualifier("defaultDataSource") DruidDataSource defaultDataSource,
        Y9TenantDataSourceLookup y9TenantDataSourceLookup,
        SystemApi systemApi,
        Y9Properties y9Properties) {
        super(getFlowableTenantInfoHolder());
        this.defaultDataSource = defaultDataSource;
        this.y9TenantDataSourceLookup = y9TenantDataSourceLookup;
        this.systemApi = systemApi;
        this.y9Properties = y9Properties;
    }

    private static TenantInfoHolder getFlowableTenantInfoHolder() {
        FlowableTenantInfoHolder flowableTenantInfoHolder = new FlowableTenantInfoHolder();
        flowableTenantInfoHolder.addTenant(null);
        return flowableTenantInfoHolder;
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        createSystem(y9Properties.getSystemName());
        LOGGER.info("start registerTenant");
        // 设置默认的数据源(y9_flowable)
        registerTenant(AbstractEngineConfiguration.NO_TENANT_ID, defaultDataSource);
        ProcessEngine processEngine = super.buildProcessEngine();
        // 初始化租户数据源配置
        initializeTenantDataSources();
        return processEngine;
    }

    private void createSystem(String systemName) {
        try {
            System system = systemApi.getByName(systemName).getData();
            if (system == null) {
                // 注册系统并自动租用
                systemApi.registrySystem(systemName, "流程管理", "/server-" + systemName).getData();
            }
        } catch (Exception e) {
            LOGGER.error("在数字底座创建[流程管理]系统失败", e);
        }
    }

    /**
     * 初始化租户数据源配置
     */
    private void initializeTenantDataSources() {
        Map<String, DruidDataSource> dataSources = y9TenantDataSourceLookup.getDataSources();
        for (String tenantId : dataSources.keySet()) {
            registerTenant(tenantId, dataSources.get(tenantId));
        }
    }

}
