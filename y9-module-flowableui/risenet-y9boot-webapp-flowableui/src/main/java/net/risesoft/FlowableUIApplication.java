package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowableUIApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowableUIApplication.class, args);
    }
}
