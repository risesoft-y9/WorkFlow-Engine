package net.risesoft.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableConfigurationProperties(Y9Properties.class)
public class Configuraton implements WebMvcConfigurer {

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

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}
