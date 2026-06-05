package net.risesoft;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.filter.CheckUserLoginFilter4Flowable;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;

import feign.RequestInterceptor;

@Configuration
@EnableConfigurationProperties({Y9Properties.class, Y9FlowableProperties.class})
@Slf4j
public class FlowableUIConfiguration {

    @Bean
    public FilterRegistrationBean<CheckUserLoginFilter4Flowable> checkUserLoginFilter4FlowableUI() {
        LOGGER.debug("*****************************************init CheckUserLoginFilter4Flowable ...");
        FilterRegistrationBean<CheckUserLoginFilter4Flowable> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CheckUserLoginFilter4Flowable());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/vue/*");
        return filterBean;
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    /**
     * 调用第三方feignclient时添加通用参数
     */
    @Bean
    public RequestInterceptor commonParamsInterceptor() {
        return template -> {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String userId = Y9LoginUserHolder.getPersonId();
            String positionId = Y9FlowableHolder.getPositionId();
            template.header("X-Tenant-Id", tenantId);
            template.header("X-User-Id", userId);
            template.header("X-Position-Id", positionId);
        };
    }
}
