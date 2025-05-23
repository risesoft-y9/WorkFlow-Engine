package net.risesoft.log.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.log.service.FlowableAccessLogReporter;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author qinman
 * @date 2025/05/20
 */
@DependsOn("y9Context")
@Slf4j
public class FlowableLogAdvice implements MethodInterceptor {

    private final FlowableAccessLogReporter flowableAccessLogReporter;

    public FlowableLogAdvice(FlowableAccessLogReporter flowableAccessLogReporter) {
        this.flowableAccessLogReporter = flowableAccessLogReporter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.nanoTime();
        String errorMessage = "";
        String throwable = "";
        String success = "成功";
        String userAgent = "";
        String hostIp = "";
        String systemName = "";
        String processSerialNumber = "", title = "";

        Object ret;
        try {
            ret = invocation.proceed();
        } catch (Exception e) {
            success = "出错";
            errorMessage = e.getMessage();

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            throwable = stringWriter.toString();
            throw e;
        } finally {
            Method method = invocation.getMethod();
            FlowableLog flowableLog = method.getAnnotation(FlowableLog.class);
            HttpServletResponse response = null;
            try {
                ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                if (sra != null) {
                    HttpServletRequest request = sra.getRequest();
                    response = sra.getResponse();
                    userAgent = request.getHeader("User-Agent");
                    hostIp = Y9Context.getIpAddr(request);
                    processSerialNumber = request.getHeader("processSerialNumber");
                    title = StringUtils.hasText(request.getHeader("title"))
                        ? URLDecoder.decode(request.getHeader("title"), StandardCharsets.UTF_8) : "";
                }
                systemName = Y9Context.getSystemName();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            if (flowableLog != null && flowableLog.enable()) {
                long end = System.nanoTime();
                long elapsedTime = end - start;

                FlowableAccessLog flowableAccessLog = new FlowableAccessLog();
                try {
                    flowableAccessLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    flowableAccessLog.setProcessSerialNumber(processSerialNumber);
                    flowableAccessLog.setOperateType(flowableLog.operationType().getValue());
                    flowableAccessLog.setLogTime(new Date());
                    flowableAccessLog.setLogLevel(FlowableLogLevelEnum.COMMON.toString());
                    flowableAccessLog.setTitle(title);
                    flowableAccessLog.setSystemName(systemName);
                    flowableAccessLog.setModularName("工作流");
                    flowableAccessLog.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());
                    flowableAccessLog.setElapsedTime(String.valueOf(elapsedTime));
                    flowableAccessLog.setServerIp(Y9Context.getHostIp());
                    flowableAccessLog.setSuccess(success);
                    flowableAccessLog.setErrorMessage(errorMessage);
                    flowableAccessLog.setThrowable(throwable);
                    flowableAccessLog.setUserHostIp(hostIp);
                    flowableAccessLog.setUserAgent(userAgent);

                    Map<String, Object> map = Y9LoginUserHolder.getMap();
                    if (map != null) {
                        String userHostIp = (String)map.get("userHostIP");
                        if (userHostIp != null) {
                            flowableAccessLog.setUserHostIp(userHostIp);
                        }
                        String requestUrl = (String)map.get("requestURL");
                        if (requestUrl != null) {
                            flowableAccessLog.setRequestUrl(requestUrl);
                        }
                    }

                    if (StringUtils.hasText(flowableLog.operationName())) {
                        flowableAccessLog.setOperateName(flowableLog.operationName());
                    } else {
                        flowableAccessLog.setOperateName(method.getName());
                    }
                    if (null != flowableLog.logLevel()) {
                        flowableAccessLog.setLogLevel(flowableLog.logLevel().toString());
                    }

                    UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
                    if (null != userInfo) {
                        flowableAccessLog.setUserId(userInfo.getPersonId());
                        flowableAccessLog.setGuidPath(userInfo.getGuidPath());
                        flowableAccessLog.setUserName(userInfo.getName());
                        flowableAccessLog.setLoginName(userInfo.getLoginName());
                        flowableAccessLog.setPersonType(userInfo.getPersonType());
                        flowableAccessLog.setDn(userInfo.getDn());
                        flowableAccessLog.setTenantId(userInfo.getTenantId());
                        flowableAccessLog.setTenantName(Y9LoginUserHolder.getTenantName());
                    }
                    flowableAccessLogReporter.report(flowableAccessLog);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if (response != null) {
                // LogFilter 见到这个标志后，就不再记录日志了，因为这里已经写了日志，不需要重复写。
                response.addHeader("y9aoplog", "true");
            }
        }
        return ret;
    }
}