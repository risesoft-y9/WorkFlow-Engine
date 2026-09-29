package net.risesoft.initializer;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.init.TenantDataInitializer;
import net.risesoft.service.init.InitTableDataService;
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
@RequiredArgsConstructor
public class ItemMultiTenantDataInitializer implements TenantDataInitializer {

    private final InitTableDataService initTableDataService;

    @Override
    public void init(String tenantId) {
        LOGGER.info("租户[{}]租用[{}]初始化数据.........", tenantId, Y9Context.getSystemName());
        initTableDataService.init(tenantId);
        LOGGER.info("租户[{}]租用[{}]初始化数据完成", tenantId, Y9Context.getSystemName());
    }

}
