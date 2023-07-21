package filters;

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
public class CheckUserLoginFilter4ItemAdmin implements Filter {

    protected Logger logger = LoggerFactory.getLogger(CheckUserLoginFilter4ItemAdmin.class);

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

}
