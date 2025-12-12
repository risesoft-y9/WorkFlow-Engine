package net.risesoft.initializer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.platform.resource.AppApi;
import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.ItemInitDataConsts;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.service.init.InitTableDataService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * 启动完成后初始化默认数据和默认租户的数据
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ItemAdminApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private final InitTableDataService initTableDataService;

    private final SystemApi systemApi;
    private final AppApi appApi;
    private final RoleApi roleApi;
    private final Y9Properties y9Config;

    private void creatApp() {
        try {
            System y9System = systemApi.getByName(ItemInitDataConsts.Y9_SYSTEM_NAME).getData();
            if (null != y9System) {
                App app = appApi.findBySystemIdAndCustomId(y9System.getId(), ItemInitDataConsts.SYSTEM_NAME).getData();
                if (null == app) {
                    String appUrl = y9Config.getCommon().getBaseUrl() + "/" + Y9Context.getSystemName() + "?itemId="
                        + ItemInitDataConsts.ITEM_ID;
                    // 生成应用并自动租用
                    appApi
                        .registerApp(ItemInitDataConsts.Y9_SYSTEM_NAME, ItemInitDataConsts.SYSTEM_CN_NAME, appUrl,
                            ItemInitDataConsts.SYSTEM_NAME, InitDataConsts.TENANT_ID)
                        .getData();
                }
            }
        } catch (Exception e) {
            LOGGER.error("创建办件app失败", e);
        }
    }

    private String createSystem() {

        String systemId = "";
        try {
            System system = systemApi.getByName(ItemConsts.ITEMADMIN_KEY).getData();
            if (system == null) {
                // 注册系统并自动租用
                System y9System = systemApi
                    .registrySystem(ItemConsts.ITEMADMIN_KEY, "事项管理", "/server-itemadmin", InitDataConsts.TENANT_ID)
                    .getData();
            } else {
                systemId = system.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return systemId;
    }

    private void createPublicRoles() {
        try {
            // 创建流程监控公共角色
            roleApi.createRole(Y9IdGenerator.genId(IdType.SNOWFLAKE), ItemInitDataConsts.MONITOR_ROLE,
                InitDataConsts.TOP_PUBLIC_ROLE_ID, ItemInitDataConsts.MONITOR_ROLE_CUSTOM_ID, RoleTypeEnum.ROLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("itemAdmin ApplicationReady...");
        try {
            createSystem();
            creatApp();
            createPublicRoles();

            // 初始化默认租户的表数据
            // Y9LoginUserHolder.setTenantId(InitDataConsts.TENANT_ID);
            // initTableDataService.init(InitDataConsts.TENANT_ID);
        } catch (Exception e) {
            LOGGER.error("初始化数据失败", e);
        }
    }
}