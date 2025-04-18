package net.risesoft.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
