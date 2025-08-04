package net.risesoft;

import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.flowable.spring.boot.eventregistry.EventRegistryServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.ui.common.security.FlowableUiSecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, IdmEngineServicesAutoConfiguration.class,
    FlowableSecurityAutoConfiguration.class, EventRegistryServicesAutoConfiguration.class,
    FlowableUiSecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@EnableAsync
public class ProcessAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProcessAdminApplication.class, args);
    }
}
