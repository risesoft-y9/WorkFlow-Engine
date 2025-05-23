package y9.autoconfiguration.log;

import java.util.concurrent.Executor;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.RequestContextFilter;

import com.alibaba.ttl.threadpool.TtlExecutors;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.aop.FlowableLogAdvice;
import net.risesoft.log.aop.FlowableLogAdvisor;
import net.risesoft.log.service.FlowableAccessLogReporter;
import net.risesoft.log.service.impl.FlowableAccessLogApiReporter;
import net.risesoft.log.service.impl.FlowableAccessLogKafkaReporter;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.feature.log.FlowableLogProperties;

/**
 * @author qinman
 */
@EnableAsync
@Configuration
@ConditionalOnProperty(name = "y9.app.flowable.log.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({Y9Properties.class, FlowableLogProperties.class})
public class FlowableLogConfiguration {

    // https://github.com/spring-projects/spring-boot/issues/2637
    // https://github.com/spring-projects/spring-boot/issues/4331
    @Bean
    @ConditionalOnMissingFilterBean(RequestContextFilter.class)
    public static RequestContextFilter requestContextFilter() {
        return new OrderedRequestContextFilter();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(FlowableLogAdvice.class)
    @DependsOn({"y9Context"})
    public FlowableLogAdvice flowableLogAdvice(FlowableAccessLogReporter flowableAccessLogReporter) {
        return new FlowableLogAdvice(flowableAccessLogReporter);
    }

    @Bean
    public FlowableLogAdvisor flowableLogAdvisor(FlowableLogAdvice flowableLogAdvice) {
        FlowableLogAdvisor bean = new FlowableLogAdvisor();
        bean.setAdvice(flowableLogAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Slf4j
    @Configuration
    @AutoConfigureAfter(KafkaAutoConfiguration.class)
    @ConditionalOnProperty(value = "y9.app.flowable.log.reportMethod", havingValue = "kafka", matchIfMissing = true)
    static class FlowableLogKafkaConfiguration {

        @Primary
        @Bean("y9KafkaTemplate")
        public KafkaTemplate<?, ?> y9KafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory) {
            FlowableLogKafkaConfiguration.LOGGER.error("FlowableLogKafkaConfiguration y9KafkaTemplate init ......");
            return new KafkaTemplate<>(kafkaProducerFactory);
        }

        @Bean
        public FlowableAccessLogReporter flowableAccessLogKafkaPusher(KafkaTemplate<String, Object> y9KafkaTemplate) {
            return new FlowableAccessLogKafkaReporter(y9KafkaTemplate);
        }

    }

    @Configuration
    @ConditionalOnProperty(value = "y9.app.flowable.log.reportMethod", havingValue = "api")
    static class FlowableLogApiConfiguration {

        @Bean
        public FlowableAccessLogReporter flowableAccessLogApiPusher(Y9Properties y9Properties) {
            return new FlowableAccessLogApiReporter(y9Properties);
        }

    }

    @Primary
    @Bean(name = {"y9ThreadPoolTaskExecutor"})
    public Executor y9ThreadPoolTaskExecutor(TaskExecutorBuilder builder) {
        ThreadPoolTaskExecutor taskExecutor = builder.build();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("y9-flowable-log-");
        taskExecutor.initialize();
        return TtlExecutors.getTtlExecutor(taskExecutor);
    }
}
