package net.risesoft.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import net.risesoft.config.ConfigConstants;

public class BaseUrlFilter implements Filter {

    private static String BASE_URL;

    public static String getBaseUrl() {
        String baseUrl;
        try {
            baseUrl = (String)RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl", 0);
        } catch (Exception e) {
            baseUrl = BASE_URL;
        }
        return baseUrl;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        String baseUrl;
        String configBaseUrl = ConfigConstants.getBaseUrl();

        final HttpServletRequest servletRequest = (HttpServletRequest)request;
        // 1、支持通过 http header 中 X-Base-Url 来动态设置 baseUrl 以支持多个域名/项目的共享使用
        final String urlInHeader = servletRequest.getHeader("X-Base-Url");
        if (StringUtils.isNotEmpty(urlInHeader)) {
            baseUrl = urlInHeader;
        } else if (configBaseUrl != null && !ConfigConstants.DEFAULT_VALUE.equalsIgnoreCase(configBaseUrl)) {
            // 2、如果配置文件中配置了 baseUrl 且不为 default 则以配置文件为准
            baseUrl = configBaseUrl;
        } else {
            // 3、默认动态拼接 baseUrl
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + servletRequest.getContextPath() + "/";
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl.concat("/");
        }

        BASE_URL = baseUrl;
        request.setAttribute("baseUrl", baseUrl);
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
