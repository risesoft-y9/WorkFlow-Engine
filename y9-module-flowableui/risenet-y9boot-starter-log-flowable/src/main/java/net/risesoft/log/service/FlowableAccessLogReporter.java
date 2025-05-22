package net.risesoft.log.service;

import org.springframework.scheduling.annotation.Async;

import net.risesoft.model.log.FlowableAccessLog;

/**
 * 访问日志推送
 *
 * @author qinman
 * @date 2025/05/20
 */
public interface FlowableAccessLogReporter {

    @Async("y9ThreadPoolTaskExecutor")
    void report(FlowableAccessLog accessLog);

}
