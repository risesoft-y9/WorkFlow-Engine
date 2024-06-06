package net.risesoft.listener;

import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.init.TenantDataInitializer;
import net.risesoft.model.platform.App;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.Tenant;
import net.risesoft.util.InitTableDataService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
public class ItemMultiTenantListener implements TenantDataInitializer {

    /**
     * 事项id
     */
    public static final String ITEM_ID = "11111111-1111-1111-1111-111111111111";

    private final JdbcTemplate jdbcTemplate;

    private final TenantApi tenantApi;

    private final SystemApi systemApi;

    private final InitTableDataService initTableDataService;

    private final AppApi appApi;

    private final Y9Properties y9Config;

    public ItemMultiTenantListener(@Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate, TenantApi tenantApi, SystemApi systemApi, InitTableDataService initTableDataService, AppApi appApi, Y9Properties y9Config) {
        this.jdbcTemplate = jdbcTemplate;
        this.tenantApi = tenantApi;
        this.systemApi = systemApi;
        this.initTableDataService = initTableDataService;
        this.appApi = appApi;
        this.y9Config = y9Config;
    }

    private void creatApp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            System y9System = systemApi.getByName("itemAdmin").getData();
            if (null != y9System) {
                App app = appApi.findBySystemIdAndCustomId(y9System.getId(), "banjian").getData();
                if (null == app) {
                    String sql =
                            "INSERT INTO y9_common_app_store (ID,NAME, TAB_INDEX, URL, CHECKED, OPEN_TYPE,SYSTEM_ID,CREATE_TIME,CUSTOM_ID,TYPE,INHERIT,RESOURCE_TYPE,SHOW_NUMBER,ENABLED,HIDDEN) VALUES ('"
                                    + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "','办件', 0, '"
                                    + y9Config.getCommon().getFlowableBaseUrl() + "?itemId=" + ITEM_ID + "', 1, 1,'"
                                    + y9System.getId() + "','" + sdf.format(new Date()) + "','banjian',2,0,0,0,1,0)";
                    jdbcTemplate.execute(sql);
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建办件app失败", e);
        }
    }

    private void createTenantApp(String systemName, Tenant tenant) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System y9System = systemApi.getByName(systemName).getData();
        if (null != y9System) {
            App app = appApi.findBySystemIdAndCustomId(y9System.getId(), "banjian").getData();
            if (null != app) {
                String sql = "select ID from y9_common_tenant_app where TENANT_ID = '" + tenant.getId()
                        + "' and APP_ID = '" + app.getId() + "'";
                List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql);
                if (qlist.isEmpty()) {
                    String sql1 =
                            "INSERT INTO y9_common_tenant_app (ID, TENANT_ID, TENANT_NAME, SYSTEM_ID, APP_ID,APP_NAME,CREATE_TIME,APPLY_NAME,APPLY_ID,APPLY_REASON,VERIFY_STATUS,TENANCY) VALUES ('"
                                    + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "', '" + tenant.getId() + "', '"
                                    + tenant.getName() + "', '" + y9System.getId() + "', '" + app.getId() + "','"
                                    + app.getName() + "','" + sdf.format(new Date()) + "','"
                                    + ManagerLevelEnum.SYSTEM_MANAGER.getName() + "','','数字底座生成的默认租户自动租用',1,1)";
                    jdbcTemplate.execute(sql1);
                }
            }
        }

    }

    @Override
    public void init(String tenantId) {
        LOGGER.info("租户:{}租用itemAdmin 初始化数据.........", tenantId);
        Tenant tenant = tenantApi.getById(tenantId).getData();
        creatApp();
        createTenantApp("itemAdmin", tenant);
        initTableDataService.init(tenantId);
        LOGGER.info(Y9Context.getSystemName() + ", 同步租户数据源信息, 成功！");
    }

}
