package net.risesoft.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.filter.CheckUserLoginFilter4Flowable;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

@Configuration
@EnableConfigurationProperties(Y9Properties.class)
public class FlowableUIConfiguraton implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(FlowableUIConfiguraton.class);

    // starter-log工程用到了RequestContextHolder
    // https://github.com/spring-projects/spring-boot/issues/2637
    // https://github.com/spring-projects/spring-boot/issues/4331
    @Bean
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public FilterRegistrationBean checkUserLoginFilter4FlowableUI() {
        log.debug("****************************************************************************init CheckUserLoginFilter4Flowable ...");
        FilterRegistrationBean filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CheckUserLoginFilter4Flowable());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/vue/*");
        return filterBean;
    }

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Bean
    // public FilterRegistrationBean checkEventHandleUserLoginFilter() {
    // FilterRegistrationBean filterBean = new FilterRegistrationBean();
    // filterBean.setFilter(new FlowableUICheckUserLoginFilter());
    // filterBean.setAsyncSupported(false);
    // filterBean.setOrder(50);
    // filterBean.addUrlPatterns("/*");
    // return filterBean;
    // }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Bean
    // public FilterRegistrationBean simpleCORSFilter() {
    // FilterRegistrationBean filterBean = new FilterRegistrationBean();
    // filterBean.setFilter(new SimpleCORSFilter());
    // filterBean.addUrlPatterns("/*");
    // return filterBean;
    // }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
