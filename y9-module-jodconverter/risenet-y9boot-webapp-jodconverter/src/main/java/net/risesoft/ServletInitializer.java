package net.risesoft;

import java.util.Collections;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(NewFilePreviewApplication.class);
    }

    @Override
    protected WebApplicationContext run(SpringApplication application) {
        WebApplicationContext ctx = super.run(application);
        Environment env = ctx.getEnvironment();
        String cookieSecure = env.getProperty("server.servlet.session.cookie.secure", "false");

        ServletContext servletContext = ctx.getServletContext();
        assert servletContext != null;
        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setSecure(Boolean.parseBoolean(cookieSecure));
        return ctx;
    }
}