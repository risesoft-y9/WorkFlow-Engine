package org.flowable.ui.common.tenant;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
public class DefaultTenantProvider implements TenantProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTenantProvider.class);

    private String tenantId;

    public DefaultTenantProvider() {
        String configuredTenantId = FlowableTenantInfoHolder.getTenantId();
        if (!StringUtils.isBlank(configuredTenantId)) {
            configuredTenantId = configuredTenantId.trim();
            LOGGER.debug("Found configured tenantId: '{}'", configuredTenantId);
            this.tenantId = configuredTenantId;
        }
    }

    @Override
    public String getTenantId() {
        tenantId = FlowableTenantInfoHolder.getTenantId();
        if (StringUtils.isNoneBlank(tenantId)) {
            LOGGER.debug("Using configured tenantId: '{}'", tenantId);
            return tenantId;
        }
        tenantId = AbstractEngineConfiguration.NO_TENANT_ID;
        LOGGER.debug("Using user tenantId: '{}'", tenantId);
        return tenantId;
    }
}
