package net.risesoft;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Tenant;
import net.risesoft.util.InitTableDataService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    /** 事项id */
    public static final String ITEM_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    @Qualifier("jdbcTemplate4Public") private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant") private JdbcTemplate jdbcTemplate4Tenant;

    @Autowired
    private Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Autowired
    private InitTableDataService initTableDataService;

    @Autowired
    private Y9Properties y9Config;

    private void creatApp(String systemName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "select * from y9_common_system where NAME = '" + systemName + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from Y9_COMMON_APP_STORE where CUSTOM_ID = 'banjian' and SYSTEM_ID = '"
                    + smap.get("ID").toString() + "'";
                List<Map<String, Object>> alist = jdbcTemplate.queryForList(sql);
                if (alist.size() == 0) {
                    sql =
                        "INSERT INTO y9_common_app_store (ID,NAME, TAB_INDEX, URL, CHECKED, OPEN_TYPE,SYSTEM_ID,CREATE_TIME,CUSTOM_ID,TYPE,INHERIT,RESOURCE_TYPE,SHOW_NUMBER,ENABLED,HIDDEN) VALUES ('"
                            + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "','办件', 0, '"
                            + y9Config.getCommon().getFlowableBaseUrl() + "?itemId=" + ITEM_ID + "', 1, 1,'"
                            + smap.get("ID").toString() + "','" + sdf.format(new Date()) + "','banjian',2,0,0,0,1,0)";
                    jdbcTemplate.execute(sql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "', 'itemAdmin', '" + systemName
                        + "', '事项管理', 100,1,1,'" + sdf.format(new Date()) + "')";
                jdbcTemplate.execute(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTenantApp(String systemName, Tenant tenant) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select * from y9_common_system where NAME = '" + systemName + "'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list.size() == 1) {
            Map<String, Object> smap = list.get(0);
            sql = "select * from Y9_COMMON_APP_STORE where CUSTOM_ID = 'banjian' and SYSTEM_ID = '"
                + smap.get("ID").toString() + "'";
            List<Map<String, Object>> alist = jdbcTemplate.queryForList(sql);
            if (alist.size() == 1) {
                Map<String, Object> amap = alist.get(0);
                sql = "select ID from y9_common_tenant_app where TENANT_ID = '" + tenant.getId() + "' and APP_ID = '"
                    + amap.get("ID").toString() + "'";
                List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql);
                if (qlist.size() == 0) {
                    String sql1 =
                        "INSERT INTO y9_common_tenant_app (ID, TENANT_ID, TENANT_NAME, SYSTEM_ID, APP_ID,APP_NAME,CREATE_TIME,APPLY_NAME,APPLY_ID,APPLY_REASON,VERIFY_STATUS,TENANCY) VALUES ('"
                            + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "', '" + tenant.getId() + "', '"
                            + tenant.getName() + "', '" + smap.get("ID").toString() + "', '" + amap.get("ID").toString()
                            + "','" + amap.get("NAME").toString() + "','" + sdf.format(new Date()) + "','"
                            + ManagerLevelEnum.SYSTEM_MANAGER.getName() + "','','微内核默认租用',1,1)";
                    jdbcTemplate.execute(sql1);
                }
            }
        }

    }

    private void createTenantSystem(String systemName, Tenant tenant) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "select * from y9_common_system where NAME = '" + systemName + "'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() == 1) {
                Map<String, Object> smap = list.get(0);
                sql = "select * from y9_common_tenant_system where TENANT_ID = '" + tenant.getId()
                    + "' and SYSTEM_ID = '" + smap.get("ID").toString() + "'";
                List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql);
                if (qlist.size() == 0) {
                    String sql1 =
                        "INSERT INTO y9_common_tenant_system (ID, SYSTEM_ID, TENANT_ID, TENANT_DATA_SOURCE, CREATE_TIME) VALUES ('"
                            + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "', '" + smap.get("ID").toString() + "', '"
                            + tenant.getId() + "', '" + tenant.getDefaultDataSourceId() + "','" + sdf.format(new Date())
                            + "')";
                    jdbcTemplate.execute(sql1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("itemAdmin ApplicationReady...");
        try {
            createSystem("itemAdmin");// 创建系统
            creatApp("itemAdmin");// 创建应用

            String sql = "select * from Y9_COMMON_TENANT where TENANT_TYPE = 3";
            List<Map<String, Object>> tlist = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> map : tlist) {
                Tenant tenant = new Tenant();
                tenant.setId(map.get("ID").toString());
                tenant.setDefaultDataSourceId(map.get("DEFAULT_DATA_SOURCE_ID").toString());
                tenant.setName(map.get("NAME").toString());

                createTenantSystem("itemAdmin", tenant);// 创建租户系统
                createTenantApp("itemAdmin", tenant);// 创建租户应用
            }
            try {
                // 更新租户数据库里的表结构
                // updateTenantSchema();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Map<String, Object> map : tlist) {
                initTableDataService.init(map.get("ID").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTenantSchema() {
        Map<String, DruidDataSource> map = y9TenantDataSourceLookup.getDataSources();
        Set<String> list = map.keySet();
        for (String tenantId : list) {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9TenantHibernateInfoHolder.schemaUpdate(Y9Context.getEnvironment());
        }
    }

}
