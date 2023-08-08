package net.risesoft.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.service.ModelImageService;
import org.flowable.ui.modeler.service.ModelServiceImpl;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import net.risesoft.filter.ProcessAdminCheckUserLoginFilter;
import net.risesoft.filter.RemoveUrlJsessionIdFilter;
import net.risesoft.listener.FlowableMultiTenantListener;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.sso.Y9SsoClientProperties;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSource;
import net.risesoft.y9.tenant.datasource.Y9TenantDataSourceLookup;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableConfigurationProperties(Y9Properties.class)
@ImportResource({"classpath:/springconfigs/flowable.cfg.xml"})
@ComponentScan(basePackages = {"net.risesoft", "org.flowable.ui"})
public class ProcessAdminConfiguraton implements WebMvcConfigurer {

    /**
     * 
     * Description: starter-log工程用到了RequestContextHolder https://github.com/spring-projects/spring-boot/issues/2637
     * https://github.com/spring-projects/spring-boot/issues/4331
     * 
     * @return
     */
    @Bean
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    @Autowired
    private Environment environment;

    @Autowired
    Y9Properties y9config;

    Y9SsoClientProperties configProps;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/main/index");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
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

    @PostConstruct
    public void init() {
        configProps = y9config.getFeature().getSso();
    }

    @Bean(name = {"jdbcTemplate4Public"})
    public JdbcTemplate jdbcTemplate(@Qualifier("y9PublicDS") DruidDataSource y9PublicDs) {
        return new JdbcTemplate(y9PublicDs);
    }

    @Bean("jdbcTemplate4Tenant")
    @ConditionalOnMissingBean(name = "jdbcTemplate4Tenant")
    public JdbcTemplate jdbcTemplate4Tenant(@Qualifier("y9TenantDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
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
        final FilterRegistrationBean<ProcessAdminCheckUserLoginFilter> filterBean =
            new FilterRegistrationBean<ProcessAdminCheckUserLoginFilter>();
        filterBean.setFilter(new ProcessAdminCheckUserLoginFilter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(50);
        filterBean.addUrlPatterns("/*");
        return filterBean;
    }

    @Bean
    public FilterRegistrationBean<RemoveUrlJsessionIdFilter> removeUrlJsessionIdFilter() {
        final FilterRegistrationBean<RemoveUrlJsessionIdFilter> filterBean =
            new FilterRegistrationBean<RemoveUrlJsessionIdFilter>();
        filterBean.setFilter(new RemoveUrlJsessionIdFilter());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/*");
        if (this.configProps.getSingleSignOutFilterOrder() != null) {
            filterBean.setOrder(this.configProps.getSingleSignOutFilterOrder());
        } else {
            filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        }
        return filterBean;
    }

    @Bean(name = {"sqlSessionFactory"})
    public SqlSessionFactory sqlSessionFactory(@Qualifier("y9FlowableDS") DruidDataSource dataSource) throws Exception {
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

    @Primary
    @ConfigurationProperties("spring.datasource.druid.flowable")
    @Bean(name = {"y9FlowableDS"})
    public DruidDataSource y9FlowableDs() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    @ConfigurationProperties("spring.datasource.druid.y9-public")
    @Bean(name = {"y9PublicDS"})
    @ConditionalOnMissingBean(name = "y9PublicDS")
    public DruidDataSource y9PublicDs() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSource;
    }

    @Bean("y9TenantDataSource")
    public DataSource y9TenantDataSource(@Qualifier("y9FlowableDS") DruidDataSource y9FlowableDs,
        @Qualifier("y9TenantDataSourceLookup") Y9TenantDataSourceLookup y9TenantDataSourceLookup) {
        return new Y9TenantDataSource(y9FlowableDs, y9TenantDataSourceLookup);
    }

    @Bean("y9TenantDataSourceLookup")
    public Y9TenantDataSourceLookup y9TenantDataSourceLookup(@Qualifier("y9PublicDS") DruidDataSource ds) {
        return new Y9TenantDataSourceLookup(ds, environment.getProperty("y9.systemName"));
    }
}
