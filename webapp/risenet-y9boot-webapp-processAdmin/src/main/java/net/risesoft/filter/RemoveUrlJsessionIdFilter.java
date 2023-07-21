package net.risesoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
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
