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

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.exception.ErrorCode;
import net.risesoft.exception.FlowableUIErrorCodeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

@Slf4j
public class MobileV1Filter implements Filter {

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        try {
            String tenantId = request.getHeader("auth-tenantId");
            String userId = request.getHeader("auth-userId");
            String positionId = request.getHeader("auth-positionId");
            if (StringUtils.isBlank(tenantId)) {
                setResponse(response, FlowableUIErrorCodeEnum.AUTH_TENANTID_NOT_FOUND);
                return;
            }
            if (StringUtils.isBlank(userId)) {
                setResponse(response, FlowableUIErrorCodeEnum.AUTH_USERID_NOT_FOUND);
                return;
            }
            if (StringUtils.isBlank(positionId)) {
                setResponse(response, FlowableUIErrorCodeEnum.AUTH_POSITIONID_NOT_FOUND);
                return;
            }
            PersonApi personApi = Y9Context.getBean(PersonApi.class);
            Person person = personApi.get(tenantId, userId).getData();
            if (person == null) {
                setResponse(response, FlowableUIErrorCodeEnum.PERSON_NOT_FOUND);
                return;
            }
            PositionApi positionApi = Y9Context.getBean(PositionApi.class);
            Position position = positionApi.get(tenantId, positionId).getData();
            if (position == null) {
                setResponse(response, FlowableUIErrorCodeEnum.POSITION_NOT_FOUND);
                return;
            }
            Y9LoginUserHolder.setPerson(person);
            Y9LoginUserHolder.setPosition(position);
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("......................................init MobileV1Filter ...");
        }
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(Y9JsonUtil.writeValueAsString(Y9Result.failure(errorCode)));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
