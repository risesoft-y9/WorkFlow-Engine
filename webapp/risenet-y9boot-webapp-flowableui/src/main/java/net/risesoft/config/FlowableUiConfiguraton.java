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

import net.risesoft.filter.Y9SkipSsoFilter;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableConfigurationProperties(Y9Properties.class)
public class FlowableUiConfiguraton implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(FlowableUiConfiguraton.class);

    /**
     * Description: starter-log工程用到了RequestContextHolder https://github.com/spring-projects/spring-boot/issues/2637
     * https://github.com/spring-projects/spring-boot/issues/4331
     * 
     * @return
     */
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
    public FilterRegistrationBean checkUserLoginFilter4FlowableUi() {
        log.debug(
            "****************************************************************************init Y9SkipSSOFilter ...");
        FilterRegistrationBean filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new Y9SkipSsoFilter());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/vue/*");
        return filterBean;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
