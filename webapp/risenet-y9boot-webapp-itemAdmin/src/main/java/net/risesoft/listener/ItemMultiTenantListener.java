package net.risesoft.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.integrator.api.integrator.Y9TenantHibernateInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.resource.AppApi;
import net.risesoft.api.resource.SystemApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.App;
import net.risesoft.model.Tenant;
import net.risesoft.util.InitTableDataService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
public class ItemMultiTenantListener implements ApplicationListener<Y9EventCommon> {

    /** 事项id */
    public static final String ITEM_ID = "11111111-1111-1111-1111-111111111111";

    @Autowired
    @Qualifier("jdbcTemplate4Public")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    @Autowired
    private TenantApi tenantApi;

    @Autowired
    private Y9TenantDataSourceLookup y9TenantDataSourceLookup;

    @Autowired
    private SystemApi systemApi;

    @Autowired
    private InitTableDataService initTableDataService;

    @Autowired
    private AppApi appApi;

    @Autowired
    private Y9Properties y9Config;

    private void creatApp(String systemName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            net.risesoft.model.System y9System = systemApi.getByName(systemName);
            if (null != y9System) {
                App app = appApi.findBySystemIdAndCustomId(y9System.getId(), "banjian");
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
            e.printStackTrace();
        }
    }

    private void createTenantApp(String systemName, Tenant tenant) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        net.risesoft.model.System y9System = systemApi.getByName(systemName);
        if (null != y9System) {
            App app = appApi.findBySystemIdAndCustomId(y9System.getId(), "banjian");
            if (null != app) {
                String sql = "select ID from y9_common_tenant_app where TENANT_ID = '" + tenant.getId()
                    + "' and APP_ID = '" + app.getId() + "'";
                List<Map<String, Object>> qlist = jdbcTemplate.queryForList(sql);
                if (qlist.size() == 0) {
                    String sql1 =
                        "INSERT INTO y9_common_tenant_app (ID, TENANT_ID, TENANT_NAME, SYSTEM_ID, APP_ID,APP_NAME,CREATE_TIME,APPLY_NAME,APPLY_ID,APPLY_REASON,VERIFY_STATUS,TENANCY) VALUES ('"
                            + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "', '" + tenant.getId() + "', '"
                            + tenant.getName() + "', '" + y9System.getId() + "', '" + app.getId() + "','"
                            + app.getName() + "','" + sdf.format(new Date()) + "','系统管理员','"
                            + DefaultIdConsts.SYSTEM_MANAGER_ID + "','微内核默认租用',1,1)";
                    jdbcTemplate.execute(sql1);
                }
            }
        }

    }

    /**
     * 租户租用事项系统监听
     */
    @Override
    public void onApplicationEvent(Y9EventCommon event) {
        String eventType = event.getEventType();
        String target = event.getTarget();
        if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType) && Y9Context.getSystemName().equals(target)) {
            String tenantId = event.getEventObject().toString();
            LOGGER.info("租户:{}租用itemAdmin 初始化数据.........", tenantId);
            Tenant tenant = tenantApi.getById(tenantId);
            creatApp("itemAdmin");
            createTenantApp("itemAdmin", tenant);
            try {
                // 更新租户数据库里的表结构
                updateTenantSchema();
            } catch (Exception e) {
                e.printStackTrace();
            }
            initTableDataService.init(tenantId);
            LOGGER.info(Y9Context.getSystemName() + ", 同步租户数据源信息, 成功！");
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
