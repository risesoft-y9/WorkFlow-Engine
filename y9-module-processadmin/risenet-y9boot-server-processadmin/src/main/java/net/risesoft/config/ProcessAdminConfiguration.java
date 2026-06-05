package net.risesoft.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

import net.risesoft.filter.CommonParamsFilter;
import net.risesoft.filter.ProcessAdminCheckUserLoginFilter;
import net.risesoft.liquibase.LiquibaseUtil;
import net.risesoft.listener.FlowableMultiTenantListener;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;
import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;

import feign.RequestInterceptor;
import liquibase.integration.spring.SpringLiquibase;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({Y9Properties.class, Y9ProcessAdminProperties.class})
@ImportResource({"classpath:/spring-configs/flowable.cfg.xml"})
@ComponentScan(basePackages = {"net.risesoft", "org.flowable.ui"})
public class ProcessAdminConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/main/index");
    }

    @Bean
    public FlowableMultiTenantListener flowableMultiTenantListener() {
        return new FlowableMultiTenantListener();
    }

    @Bean
    public FilterRegistrationBean<ProcessAdminCheckUserLoginFilter> processAdminCheckUserLoginFilter() {
        final FilterRegistrationBean<ProcessAdminCheckUserLoginFilter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new ProcessAdminCheckUserLoginFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(50);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public SpringLiquibase y9FlowableSpringLiquibase(Y9LiquibaseProperties properties,
        @Qualifier("defaultDataSource") HikariDataSource dataSource, ResourceLoader resourceLoader) {
        return LiquibaseUtil.getSpringLiquibase(dataSource, properties, resourceLoader, false);
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

    /**
     * 针对feignclient调用，设置用户信息
     */
    @Bean
    public FilterRegistrationBean<CommonParamsFilter> commonParamsFilterRegistration() {
        FilterRegistrationBean<CommonParamsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CommonParamsFilter());
        registration.addUrlPatterns("/services/rest/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}