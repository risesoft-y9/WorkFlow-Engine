package net.risesoft;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

@SpringBootApplication
@EnableScheduling
@ComponentScan(value = "net.risesoft.*")
@Slf4j
public class NewFilePreviewApplication {

    public static void main(String[] args) throws SevenZipNativeInitializationException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context =
            new SpringApplicationBuilder(NewFilePreviewApplication.class).logStartupInfo(false).run(args);
        stopWatch.stop();
        ServerProperties serverProperties = context.getBean(ServerProperties.class);
        Integer port = serverProperties.getPort();
        ServerProperties.Servlet servlet = serverProperties.getServlet();
        String contextPath = servlet.getContextPath();
        String urlSuffix = StringUtils.isBlank(contextPath) ? String.valueOf(port) : port + contextPath;
        LOGGER.info("FileView 服务启动完成，耗时:{}s，演示页请访问: http://127.0.0.1:{} ", stopWatch.getTotalTimeSeconds(), urlSuffix);
    }

}
