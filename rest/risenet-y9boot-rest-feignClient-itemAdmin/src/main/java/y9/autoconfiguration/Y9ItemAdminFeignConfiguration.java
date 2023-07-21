package y9.autoconfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients("y9.client.rest.itemadmin")
public class Y9ItemAdminFeignConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            HttpSession session = request.getSession(false);
            // 取出 Y9Oauth2ResourceFilter 往 HttpSession 里设置的 access_token
            String accessTokenInSession = session != null ? (String)session.getAttribute("access_token") : null;

            if (accessTokenInSession != null) {
                // 将 access_token 往下游服务传
                requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenInSession);
            }
        }
    }
}
