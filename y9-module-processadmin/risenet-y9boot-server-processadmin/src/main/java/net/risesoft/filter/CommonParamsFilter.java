package net.risesoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.user.UserApi;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 针对feignclient调用，设置用户信息
 */
public class CommonParamsFilter implements Filter {

    private UserApi userApi;
    private PositionApi positionApi;

    @Override
    public void init(FilterConfig filterConfig) {
        this.userApi = Y9Context.getBean(UserApi.class);
        this.positionApi = Y9Context.getBean(PositionApi.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String tenantId = httpRequest.getHeader("X-Tenant-Id");
        String userId = httpRequest.getHeader("X-User-Id");
        String positionId = httpRequest.getHeader("X-Position-Id");
        try {
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);

                if (StringUtils.isNotBlank(userId)) {
                    UserInfo userInfo = userApi.get(tenantId, userId).getData();
                    Y9LoginUserHolder.setUserInfo(userInfo);
                }

                if (StringUtils.isNotBlank(positionId)) {
                    Position position = positionApi.get(tenantId, positionId).getData();
                    Y9FlowableHolder.setPositionId(positionId);
                    Y9FlowableHolder.setPosition(position);
                }
            }
            chain.doFilter(request, response);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }
}
