package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import net.risesoft.y9.spring.boot.Y9Banner;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@SpringBootApplication
@EnableAsync
public class ItemAdminApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ItemAdminApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
