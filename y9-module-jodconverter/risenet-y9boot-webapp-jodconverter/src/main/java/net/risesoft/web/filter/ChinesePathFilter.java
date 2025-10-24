package net.risesoft.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class ChinesePathFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // 空实现 - 该过滤器不需要在初始化时执行特定逻辑
        // 字符编码设置在 doFilter 方法中处理
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 空实现 - 该过滤器不需要在销毁时执行特定清理逻辑
    }
}
