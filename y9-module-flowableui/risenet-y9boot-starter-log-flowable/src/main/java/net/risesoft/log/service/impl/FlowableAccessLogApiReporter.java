package net.risesoft.log.service.impl;

import java.util.List;

import org.apache.commons.httpclient.NameValuePair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.service.FlowableAccessLogReporter;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.RemoteCallUtil;

/**
 * 访问日志 API 推送
 *
 * @author qinman
 * @date 2025/05/20
 */
@Slf4j
@RequiredArgsConstructor
public class FlowableAccessLogApiReporter implements FlowableAccessLogReporter {

    private final Y9Properties y9Properties;

    @Override
    public void report(final FlowableAccessLog log) {
        String logBaseUrl = y9Properties.getCommon().getLogBaseUrl();
        String url = logBaseUrl + "/services/rest/v1/accessLog/flowable/asyncSaveLog";
        List<NameValuePair> requestBody = RemoteCallUtil.objectToNameValuePairList(log);
        RemoteCallUtil.post(url, null, requestBody, Object.class);
    }
}
