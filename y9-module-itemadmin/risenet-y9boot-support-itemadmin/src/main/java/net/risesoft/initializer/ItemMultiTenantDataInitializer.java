package net.risesoft.initializer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.ItemInitDataConsts;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.init.TenantDataInitializer;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.service.init.InitTableDataService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;

/**
 * 监听数据源变化
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@Component
public class ItemMultiTenantDataInitializer implements TenantDataInitializer {

    private final JdbcTemplate jdbcTemplate;

    private final TenantApi tenantApi;

    private final SystemApi systemApi;

    private final InitTableDataService initTableDataService;

    private final AppApi appApi;

    public ItemMultiTenantDataInitializer(
        @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate,
        TenantApi tenantApi,
        SystemApi systemApi,
        InitTableDataService initTableDataService,
        AppApi appApi) {
        this.jdbcTemplate = jdbcTemplate;
        this.tenantApi = tenantApi;
        this.systemApi = systemApi;
        this.initTableDataService = initTableDataService;
        this.appApi = appApi;
    }

    private void createTenantApp(Tenant tenant) {
        try {
            System y9System = systemApi.getByName(ItemInitDataConsts.Y9_SYSTEM_NAME).getData();
            if (null != y9System) {
                App app = appApi.findBySystemIdAndCustomId(y9System.getId(), ItemConsts.BANJIAN_KEY).getData();
                if (null != app) {
                    String sql = "select ID from y9_common_tenant_app where TENANT_ID = ? and APP_ID = ?";
                    List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql, tenant.getId(), app.getId());
                    if (qlist.isEmpty()) {
                        String sql1 =
                            "INSERT INTO y9_common_tenant_app (ID, TENANT_ID, TENANT_NAME, SYSTEM_ID, APP_ID,APP_NAME,CREATE_TIME,APPLY_NAME,APPLY_ID,APPLY_REASON,VERIFY_STATUS,TENANCY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                        jdbcTemplate.update(sql1, Y9IdGenerator.genId(IdType.SNOWFLAKE), tenant.getId(),
                            tenant.getName(), y9System.getId(), app.getId(), app.getName(),
                            Y9DateTimeUtils.formatCurrentDateTime(), ManagerLevelEnum.SYSTEM_MANAGER.getName(), "",
                            "数字底座生成的默认租户自动租用", 1, 1);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建租户应用失败", e);
        }
    }

    @Override
    public void init(String tenantId) {
        LOGGER.info("租户:{}租用itemAdmin 初始化数据.........", tenantId);
        Tenant tenant = tenantApi.getById(tenantId).getData();
        createTenantApp(tenant);
        initTableDataService.init(tenantId);
        LOGGER.info("{}, 同步租户数据源信息, 成功！", Y9Context.getSystemName());
    }

}
