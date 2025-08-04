package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@SpringBootApplication
@EnableAsync
public class ItemAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemAdminApplication.class, args);
    }
}
