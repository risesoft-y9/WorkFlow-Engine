package net.risesoft.config;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.web.filter.AttributeSetFilter;
import net.risesoft.web.filter.BaseUrlFilter;
import net.risesoft.web.filter.ChinesePathFilter;
import net.risesoft.web.filter.TrustDirFilter;
import net.risesoft.web.filter.TrustHostFilter;
import net.risesoft.web.filter.UrlCheckFilter;
import net.risesoft.y9.Y9Context;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    /**
     * 访问外部文件配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = ConfigConstants.getFileDir();
        LOGGER.info("Add resource locations: {}", filePath);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/", "file:" + filePath);
    }

    /**
     * 针对经过反向代理的请求不能正确获得一些原始的请求信息，例如不能正确获得原始的 schema <br/>
     * 此过滤器更多是为了减少外部 servlet 容器的配置 <br/>
     *
     * @return {@code FilterRegistrationBean<ForwardedHeaderFilter> }
     * @see <a href= "https://docs.spring.io/spring-security/reference/servlet/appendix/proxy-server.html">Proxy Server
     *      Configuration</a>
     */
    @Bean
    FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        FilterRegistrationBean<ForwardedHeaderFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ChinesePathFilter> getChinesePathFilter() {
        ChinesePathFilter filter = new ChinesePathFilter();
        FilterRegistrationBean<ChinesePathFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(10);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TrustHostFilter> getTrustHostFilter() {
        Set<String> filterUri = new HashSet<>();
        filterUri.add("/onlinePreview");
        filterUri.add("/picturesPreview");
        filterUri.add("/getCorsFile");
        TrustHostFilter filter = new TrustHostFilter();
        FilterRegistrationBean<TrustHostFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setUrlPatterns(filterUri);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TrustDirFilter> getTrustDirFilter() {
        Set<String> filterUri = new HashSet<>();
        filterUri.add("/onlinePreview");
        filterUri.add("/picturesPreview");
        filterUri.add("/getCorsFile");
        TrustDirFilter filter = new TrustDirFilter();
        FilterRegistrationBean<TrustDirFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setUrlPatterns(filterUri);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<BaseUrlFilter> getBaseUrlFilter() {
        Set<String> filterUri = new HashSet<>();
        // filterUri.add("/index");
        // filterUri.add("/");
        // filterUri.add("/onlinePreview");
        // filterUri.add("/picturesPreview");
        BaseUrlFilter filter = new BaseUrlFilter();
        FilterRegistrationBean<BaseUrlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setUrlPatterns(filterUri);
        registrationBean.setOrder(20);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<UrlCheckFilter> getUrlCheckFilter() {
        UrlCheckFilter filter = new UrlCheckFilter();
        FilterRegistrationBean<UrlCheckFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setOrder(30);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<AttributeSetFilter> getWatermarkConfigFilter() {
        Set<String> filterUri = new HashSet<>();
        filterUri.add("/index");
        filterUri.add("/");
        filterUri.add("/onlinePreview");
        filterUri.add("/picturesPreview");
        AttributeSetFilter filter = new AttributeSetFilter();
        FilterRegistrationBean<AttributeSetFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.setUrlPatterns(filterUri);
        return registrationBean;
    }

}
