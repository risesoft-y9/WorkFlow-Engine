package org.flowable.ui.common.tenant;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service
@Slf4j
public class DefaultTenantProvider implements TenantProvider {

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
