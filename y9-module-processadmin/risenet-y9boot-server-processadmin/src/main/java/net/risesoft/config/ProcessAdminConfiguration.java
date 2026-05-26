package net.risesoft.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

import net.risesoft.filter.ProcessAdminCheckUserLoginFilter;
import net.risesoft.liquibase.LiquibaseUtil;
import net.risesoft.listener.FlowableMultiTenantListener;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;
import net.risesoft.y9.configuration.feature.liquibase.Y9LiquibaseProperties;

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
}