package net.risesoft.filter;

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
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class MobileV1Filter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        try {
            String tenantId = request.getHeader("auth-tenantId");
            String userId = request.getHeader("auth-userId");
            String positionId = request.getHeader("auth-positionId");
            if (StringUtils.isBlank(tenantId)) {
                setResponse(response, HttpStatus.UNAUTHORIZED, FlowableUIErrorCodeEnum.AUTH_TENANTID_NOT_FOUND);
                return;
            }
            if (StringUtils.isBlank(userId)) {
                setResponse(response, HttpStatus.UNAUTHORIZED, FlowableUIErrorCodeEnum.AUTH_USERID_NOT_FOUND);
                return;
            }
            if (StringUtils.isBlank(positionId)) {
                setResponse(response, HttpStatus.UNAUTHORIZED, FlowableUIErrorCodeEnum.AUTH_POSITIONID_NOT_FOUND);
                return;
            }
            PersonApi personApi = Y9Context.getBean(PersonApi.class);
            Person person = personApi.get(tenantId, userId).getData();
            if (person == null) {
                setResponse(response, HttpStatus.UNAUTHORIZED, FlowableUIErrorCodeEnum.PERSON_NOT_FOUND);
                return;
            }
            PositionApi positionApi = Y9Context.getBean(PositionApi.class);
            Position position = positionApi.get(tenantId, positionId).getData();
            if (position == null) {
                setResponse(response, HttpStatus.UNAUTHORIZED, FlowableUIErrorCodeEnum.POSITION_NOT_FOUND);
                return;
            }
            Y9LoginUserHolder.setPerson(person);
            Y9LoginUserHolder.setPosition(position);
            chain.doFilter(servletRequest, servletResponse);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

    private void setResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorCode errorCode) {
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(Y9JsonUtil.writeValueAsString(Y9Result.failure(errorCode)));
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("......................................init MobileV1Filter ...");
        }
    }
}
