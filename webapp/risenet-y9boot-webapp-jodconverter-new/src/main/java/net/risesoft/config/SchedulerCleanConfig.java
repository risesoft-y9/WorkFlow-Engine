package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.cache.CacheService;
import net.risesoft.utils.KkFileUtils;

@Slf4j
@Component
@ConditionalOnExpression("'${cache.clean.enabled:false}'.equals('true')")
public class SchedulerCleanConfig {

    private final CacheService cacheService;

    public SchedulerCleanConfig(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    private final String fileDir = ConfigConstants.getFileDir();

    // 默认每晚3点执行一次
    @Scheduled(cron = "${cache.clean.cron:0 0 3 * * ?}")
    public void clean() {
        LOGGER.info("Cache clean start");
        cacheService.cleanCache();
        KkFileUtils.deleteDirectory(fileDir);
        LOGGER.info("Cache clean end");
    }
}
