package net.risesoft.log.service.impl;

import net.risesoft.model.log.FlowableAccessLog;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.FlowableAccessLogReporter;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;

/**
 * 访问日志 kafka 推送
 *
 * @author qinman
 * @date 2025/05/20
 */
@Slf4j
@RequiredArgsConstructor
public class FlowableAccessLogKafkaReporter implements FlowableAccessLogReporter {

    private final KafkaTemplate<String, Object> y9KafkaTemplate;

    @Override
    public void report(final FlowableAccessLog log) {
        try {
            String jsonString = Y9JsonUtil.writeValueAsString(log);
            y9KafkaTemplate.send(Y9TopicConst.Y9_ACCESSLOG_MESSAGE_FLOWABLE, jsonString);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
