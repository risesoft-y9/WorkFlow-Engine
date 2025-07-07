package net.risesoft.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.ManagerApi;
import net.risesoft.model.platform.Manager;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
public class ProcessAdminCheckUserLoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest)request;
            StringBuffer path = req.getRequestURL();
            HttpSession session = req.getSession();
            UserInfo loginUser = Y9LoginUserHolder.getUserInfo();
            boolean b = path.indexOf("/modeler.html") > 0;
            if (b) {
                String personId = request.getParameter("personId");
                if (StringUtils.isNotBlank(personId)) {
                    try {
                        ManagerApi managerApi = Y9Context.getBean(ManagerApi.class);
                        Manager manager = managerApi.get(personId.split(":")[0], personId.split(":")[1]).getData();
                        loginUser = manager.toUserInfo();
                    } catch (BeansException e) {
                        LOGGER.error("Failed to get managerApi bean");
                    }
                }
            }
            if (loginUser == null) {
                loginUser = (UserInfo)session.getAttribute("loginUser");
            }
            if (loginUser != null) {
                Y9LoginUserHolder.setUserInfo(loginUser);
                Y9LoginUserHolder.setTenantId(loginUser.getTenantId());
                FlowableTenantInfoHolder.setTenantId(loginUser.getTenantId());
                session.setAttribute("loginUser", loginUser);
            }
            chain.doFilter(request, response);
        } finally {
            Y9LoginUserHolder.clear();
            FlowableTenantInfoHolder.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ProcessAdminCheckUserLoginFilter init........................");
        }
    }
}
