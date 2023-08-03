package net.risesoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class Y9SkipSsoFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(Y9SkipSsoFilter.class);

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpSession session = request.getSession();
        try {
            UserInfo loginUser = (UserInfo)session.getAttribute("loginUser");
            if (loginUser != null) {
                Y9LoginUserHolder.setTenantName((String)session.getAttribute("tenantName"));
                Y9LoginUserHolder.setTenantId(loginUser.getTenantId());
                Y9LoginUserHolder.setUserInfo(loginUser);
            }
            chain.doFilter(servletRequest, servletResponse);

        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        log.debug("......................................init Y9SkipSSOFilter ...");
    }
}
