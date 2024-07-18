package net.risesoft.y9;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class FlowableTenantInfoHolder implements TenantInfoHolder {

    private static final Map<String, String> tenant = new HashMap<>(16);

    private static final ThreadLocal<String> CURRENTTENANTID = new ThreadLocal<>();

    public static void clear() {
        CURRENTTENANTID.remove();
    }

    public static String getTenantId() {
        return CURRENTTENANTID.get();
    }

    public static void setTenantId(String tenantId) {
        CURRENTTENANTID.set(tenantId);
    }

    public void addTenant(String tenantId) {
        tenant.put(tenantId, tenantId);
    }

    @Override
    public void clearCurrentTenantId() {
        CURRENTTENANTID.remove();
    }

    @Override
    public Collection<String> getAllTenants() {
        return tenant.keySet();
    }

    @Override
    public String getCurrentTenantId() {
        return CURRENTTENANTID.get() == null ? AbstractEngineConfiguration.NO_TENANT_ID : CURRENTTENANTID.get();
    }

    @Override
    public void setCurrentTenantId(String tenantId) {
        CURRENTTENANTID.set(tenantId);
    }
}
