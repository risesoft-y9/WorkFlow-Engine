package net.risesoft;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(value = "net.risesoft.*")
@Slf4j
public class FilePreviewApplication {
    public static void main(String[] args) {
        Properties properties = System.getProperties();
        LOGGER.info("user.dir:{}", properties.get("user.dir"));
        SpringApplication.run(FilePreviewApplication.class, args);
    }
}
