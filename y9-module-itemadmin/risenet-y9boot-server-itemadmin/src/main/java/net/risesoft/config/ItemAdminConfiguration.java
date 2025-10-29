package net.risesoft.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties({Y9Properties.class, Y9ItemAdminProperties.class})
@EnableKafka
@Slf4j
public class ItemAdminConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }
}
