package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.risesoft.y9.spring.boot.Y9Banner;

/**
 * @author qinman
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FlowableUIApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(FlowableUIApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
