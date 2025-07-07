package net.risesoft.config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class CheckUserLoginFilter4ItemAdmin implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpSession session = req.getSession();
        try {
            // 从session中获取最新的数据
            UserInfo loginUser = (UserInfo)session.getAttribute("loginUser");
            if (loginUser != null) {
                Y9LoginUserHolder.setUserInfo(loginUser);
                Y9LoginUserHolder.setTenantId(loginUser.getTenantId());
            }
            chain.doFilter(request, response);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }
}
