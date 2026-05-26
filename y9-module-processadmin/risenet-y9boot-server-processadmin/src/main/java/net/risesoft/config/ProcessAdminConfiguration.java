package net.risesoft.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.service.ModelImageService;
import org.flowable.ui.modeler.service.ModelServiceImpl;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.druid.pool.DruidDataSource;

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
    public FlowableModelerAppProperties flowableModelerAppProperties() {
        return new FlowableModelerAppProperties();
    }

    @Bean
    public FlowableModelQueryService flowableModelQueryService() {
        return new FlowableModelQueryService();
    }

    @Bean
    public FlowableMultiTenantListener flowableMultiTenantListener() {
        return new FlowableMultiTenantListener();
    }

    @Bean
    public ModelImageService modelImageService() {
        return new ModelImageService();
    }

    @Bean
    public ModelServiceImpl modelService() {
        return new ModelServiceImpl();
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

    @Bean(name = {"sqlSessionFactory"})
    public SqlSessionFactory sqlSessionFactory(@Qualifier("defaultDataSource") DruidDataSource dataSource)
        throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        Resource resource =
            new PathMatchingResourcePatternResolver().getResource("classpath:mybatis/mybatis-config.xml");
        bean.setConfigLocation(resource);
        return bean.getObject();
    }

    @Bean(name = {"flowableModeler"})
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public SpringLiquibase y9FlowableSpringLiquibase(Y9LiquibaseProperties properties,
        @Qualifier("defaultDataSource") DruidDataSource dataSource, ResourceLoader resourceLoader) {
        return LiquibaseUtil.getSpringLiquibase(dataSource, properties, resourceLoader, false);
    }
}