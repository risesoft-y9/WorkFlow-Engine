package net.risesoft.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class RemoveUrlJsessionIdFilter implements Filter {

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        if (httpServletRequest.isRequestedSessionIdFromURL()) {
            HttpSession session = httpServletRequest.getSession();
            if (null != session) {
                session.invalidate();
            }
        }
        HttpServletResponseWrapper wrappedResponse = new HttpServletResponseWrapper(httpServletResponse) {
            @Override
            public String encodeRedirectUrl(String url) {
                return url;
            }

            @Override
            public String encodeUrl(String url) {
                return url;
            }
        };
        chain.doFilter(request, wrappedResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
}
