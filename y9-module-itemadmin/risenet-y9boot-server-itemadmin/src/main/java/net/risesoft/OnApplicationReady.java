package net.risesoft;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.ItemConsts;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.util.InitTableDataService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 事项id
     */
    public static final String ITEM_ID = "11111111-1111-1111-1111-111111111111";

    private final JdbcTemplate jdbcTemplate;

    private final InitTableDataService initTableDataService;

    private final Y9Properties y9Config;

    public OnApplicationReady(
        @NonNull @Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate,
        @NonNull InitTableDataService initTableDataService,
        @NonNull Y9Properties y9Config) {
        this.jdbcTemplate = jdbcTemplate;
        this.initTableDataService = initTableDataService;
        this.y9Config = y9Config;
    }

    private void creatApp() {
        try {
            String sql = "select * from y9_common_system where NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, ItemConsts.ITEMADMIN_KEY);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from Y9_COMMON_APP_STORE where CUSTOM_ID = ? and SYSTEM_ID = ?";
                List<Map<String, Object>> alist =
                    jdbcTemplate.queryForList(sql, ItemConsts.BANJIAN_KEY, smap.get("ID").toString());
                if (alist.isEmpty()) {
                    sql =
                        "INSERT INTO y9_common_app_store (ID,NAME, TAB_INDEX, URL, CHECKED, OPEN_TYPE,SYSTEM_ID,CREATE_TIME,CUSTOM_ID,TYPE,INHERIT,RESOURCE_TYPE,SHOW_NUMBER,ENABLED,HIDDEN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, Y9IdGenerator.genId(IdType.SNOWFLAKE), "办件", 0,
                        y9Config.getCommon().getFlowableBaseUrl() + "?itemId=" + ITEM_ID, 1, 1,
                        smap.get("ID").toString(), Y9DateTimeUtils.formatCurrentDateTime(), ItemConsts.BANJIAN_KEY, 2,
                        0, 0, 0, 1, 0);
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建应用失败", e);
        }
    }

    private void createSystem() {
        try {
            String sql = "select * from y9_common_system s where s.NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, ItemConsts.ITEMADMIN_KEY);
            if (list.isEmpty()) {
                sql =
                    "INSERT INTO y9_common_system (ID, CONTEXT_PATH, NAME, CN_NAME, TAB_INDEX, ENABLED, AUTO_INIT, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(sql, Y9IdGenerator.genId(IdType.SNOWFLAKE), ItemConsts.ITEMADMIN_KEY,
                    ItemConsts.ITEMADMIN_KEY, "事项管理", 100, 1, 1, Y9DateTimeUtils.formatCurrentDateTime());
            }
        } catch (Exception e) {
            LOGGER.error("创建系统失败", e);
        }
    }

    private void createTenantApp(Tenant tenant) {
        try {
            String sql = "select * from y9_common_system where NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, ItemConsts.ITEMADMIN_KEY);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from Y9_COMMON_APP_STORE where CUSTOM_ID = ? and SYSTEM_ID = ?";
                List<Map<String, Object>> alist =
                    jdbcTemplate.queryForList(sql, ItemConsts.BANJIAN_KEY, smap.get("ID").toString());
                if (alist.size() == 1) {
                    Map<String, Object> amap = alist.get(0);
                    sql = "select ID from y9_common_tenant_app where TENANT_ID = ? and APP_ID = ?";
                    List<Map<String, Object>> qlist =
                        jdbcTemplate.queryForList(sql, tenant.getId(), amap.get("ID").toString());
                    if (qlist.isEmpty()) {
                        sql =
                            "INSERT INTO y9_common_tenant_app (ID, TENANT_ID, TENANT_NAME, SYSTEM_ID, APP_ID,APP_NAME,CREATE_TIME,APPLY_NAME,APPLY_ID,APPLY_REASON,VERIFY_STATUS,TENANCY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        jdbcTemplate.update(sql, Y9IdGenerator.genId(IdType.SNOWFLAKE), tenant.getId(),
                            tenant.getName(), smap.get("ID").toString(), amap.get("ID").toString(),
                            amap.get("NAME").toString(), Y9DateTimeUtils.formatCurrentDateTime(),
                            ManagerLevelEnum.SYSTEM_MANAGER.getName(), "", "系统默认租用", 1, 1);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建租户应用失败", e);
        }
    }

    private void createTenantSystem(Tenant tenant) {
        try {
            String sql = "select * from y9_common_system y where y.NAME = ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, ItemConsts.ITEMADMIN_KEY);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from y9_common_tenant_system where TENANT_ID = ? and SYSTEM_ID = ?";
                List<Map<String, Object>> qlist =
                    jdbcTemplate.queryForList(sql, tenant.getId(), smap.get("ID").toString());
                if (qlist.isEmpty()) {
                    sql =
                        "INSERT INTO y9_common_tenant_system (ID, SYSTEM_ID, TENANT_ID, TENANT_DATA_SOURCE, CREATE_TIME) VALUES (?, ?, ?, ?, ?)";
                    jdbcTemplate.update(sql, Y9IdGenerator.genId(IdType.SNOWFLAKE), smap.get("ID").toString(),
                        tenant.getId(), tenant.getDefaultDataSourceId(), Y9DateTimeUtils.formatCurrentDateTime());
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建租户系统失败", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("itemAdmin ApplicationReady...");
        createSystem();
        creatApp();

        String sql = "select * from Y9_COMMON_TENANT";
        List<Map<String, Object>> tlist = jdbcTemplate.queryForList(sql);
        /*
         * 在一个环境中，如果只有一个租户，则初始化该租户的数据库表结构，并初始化该租户的数据库数据
         * 后续添加的租户，如果租用该系统，则走ItemMultiTenantListener初始化该租户的数据库数据
         */
        if (1 == tlist.size()) {
            for (Map<String, Object> map : tlist) {
                Tenant tenant = new Tenant();
                tenant.setId(map.get("ID").toString());
                tenant.setDefaultDataSourceId(map.get("DEFAULT_DATA_SOURCE_ID").toString());
                tenant.setName(map.get("NAME").toString());

                createTenantSystem(tenant);
                createTenantApp(tenant);
            }
            for (Map<String, Object> map : tlist) {
                initTableDataService.init(map.get("ID").toString());
            }
        }
    }
}