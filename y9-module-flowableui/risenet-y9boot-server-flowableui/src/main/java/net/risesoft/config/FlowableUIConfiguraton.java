package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.filter.CheckUserLoginFilter4Flowable;
import net.risesoft.filter.MobileV1Filter;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;

@Configuration
@EnableConfigurationProperties({Y9Properties.class, Y9FlowableProperties.class})
@Slf4j
public class FlowableUIConfiguraton {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public FilterRegistrationBean checkUserLoginFilter4FlowableUI() {
        LOGGER.debug(
            "****************************************************************************init CheckUserLoginFilter4Flowable ...");
        FilterRegistrationBean filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new CheckUserLoginFilter4Flowable());
        filterBean.setAsyncSupported(false);
        filterBean.addUrlPatterns("/vue/*");
        return filterBean;
    }

    @Bean
    public FilterRegistrationBean<MobileV1Filter> mobileV1Filter() {
        FilterRegistrationBean<MobileV1Filter> filterBean = new FilterRegistrationBean<>();
        filterBean.setFilter(new MobileV1Filter());
        filterBean.setAsyncSupported(false);
        filterBean.setOrder(50);
        filterBean.addUrlPatterns("/mobile/v1/*");
        return filterBean;
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
