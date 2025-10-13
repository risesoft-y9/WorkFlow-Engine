package net.risesoft.log.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.log.service.FlowableAccessLogReporter;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 *
 * @author qinman
 * @date 2025/05/20
 */
@DependsOn("y9Context")
@Slf4j
public class FlowableLogAdvice implements MethodInterceptor {

    private final FlowableAccessLogReporter flowableAccessLogReporter;

    private final ProcessParamApi processParamApi;

    public FlowableLogAdvice(FlowableAccessLogReporter flowableAccessLogReporter, ProcessParamApi processParamApi) {
        this.flowableAccessLogReporter = flowableAccessLogReporter;
        this.processParamApi = processParamApi;
    }

    private ExceptionInfo handleException(Exception e) {
        String stackTrace = getStackTraceAsString(e);
        return new ExceptionInfo("出错", e.getMessage(), stackTrace);
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private RequestInfo collectRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        try {
            ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                requestInfo.setResponse(sra.getResponse());
                requestInfo.setUserAgent(request.getHeader("User-Agent"));
                requestInfo.setHostIp(Y9Context.getIpAddr(request));
                requestInfo.setProcessSerialNumber(request.getHeader("processSerialNumber"));
                requestInfo.setOptName(StringUtils.hasText(request.getHeader("optName"))
                    ? URLDecoder.decode(request.getHeader("optName"), StandardCharsets.UTF_8) : "");
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return requestInfo;
    }

    private FlowableAccessLog buildFlowableAccessLog(Method method, FlowableLog flowableLog,
        MethodInvocation invocation, long elapsedTime, RequestInfo requestInfo, ExceptionInfo exceptionInfo) {
        FlowableAccessLog flowableAccessLog = new FlowableAccessLog();
        initializeBasicLogInfo(flowableAccessLog, method, flowableLog, elapsedTime, requestInfo, exceptionInfo);
        setMethodArguments(flowableAccessLog, method, invocation, requestInfo);
        setTitleInformation(flowableAccessLog, requestInfo.getProcessSerialNumber());
        setUserInfo(flowableAccessLog);
        setOperationName(flowableAccessLog, flowableLog, method, requestInfo.getOptName());
        setLogLevel(flowableAccessLog, flowableLog);
        return flowableAccessLog;
    }

    private void setTitleInformation(FlowableAccessLog flowableAccessLog, String processSerialNumber) {
        if (StringUtils.hasText(processSerialNumber)) {
            try {
                ProcessParamModel processParam =
                    processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber)
                        .getData();
                if (null != processParam) {
                    flowableAccessLog.setSystemName(processParam.getSystemCnName());
                    flowableAccessLog.setModularName(processParam.getItemName());
                    flowableAccessLog.setTitle(processParam.getTitle());
                }
            } catch (Exception e) {
                LOGGER.warn("获取流程参数信息失败", e);
            }
        }
    }

    private void setUserInfo(FlowableAccessLog flowableAccessLog) {
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
    }

    private void setOperationName(FlowableAccessLog flowableAccessLog, FlowableLog flowableLog, Method method,
        String optName) {
        if (StringUtils.hasText(optName)) {
            flowableAccessLog.setOperateName(optName);
        } else if (StringUtils.hasText(flowableLog.operationName())) {
            flowableAccessLog.setOperateName(flowableLog.operationName());
        } else {
            flowableAccessLog.setOperateName(method.getName());
        }
    }

    private void setLogLevel(FlowableAccessLog flowableAccessLog, FlowableLog flowableLog) {
        if (null != flowableLog.logLevel()) {
            flowableAccessLog.setLogLevel(flowableLog.logLevel().toString());
        } else {
            flowableAccessLog.setLogLevel(FlowableLogLevelEnum.COMMON.toString());
        }
    }

    private void setMethodArguments(FlowableAccessLog flowableAccessLog, Method method, MethodInvocation invocation,
        RequestInfo requestInfo) {
        ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        if (null != paramNames) {
            Object[] args = invocation.getArguments();
            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                String paramName = paramNames.length > i ? paramNames[i] : "arg" + i;
                Object paramValue = args[i];
                if (paramValue instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile)paramValue;
                    paramValue = file.getOriginalFilename();
                } else {
                    if ("processSerialNumber".equalsIgnoreCase(paramName)) {
                        requestInfo.setProcessSerialNumber((String)paramValue);
                    }
                }
                paramMap.put(paramName, paramValue);
            }
            if (args.length > 0) {
                flowableAccessLog.setArguments(Y9JsonUtil.writeValueAsString(paramMap));
            }
        }
    }

    private void initializeBasicLogInfo(FlowableAccessLog log, Method method, FlowableLog flowableLog, long elapsedTime,
        RequestInfo requestInfo, ExceptionInfo exceptionInfo) {

        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setOperateType(flowableLog.operationType().getValue());
        log.setLogTime(new Date());
        log.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());
        log.setElapsedTime(String.valueOf(elapsedTime));
        log.setServerIp(Y9Context.getHostIp());

        if (exceptionInfo != null) {
            log.setSuccess(exceptionInfo.getSuccess());
            log.setErrorMessage(exceptionInfo.getErrorMessage());
            log.setThrowable(exceptionInfo.getThrowableStr());
        } else {
            log.setSuccess("成功");
            log.setErrorMessage("");
            log.setThrowable("");
        }

        log.setUserHostIp(requestInfo.getHostIp());
        log.setUserAgent(requestInfo.getUserAgent());
        log.setProcessSerialNumber(requestInfo.getProcessSerialNumber());

        Map<String, Object> map = Y9LoginUserHolder.getMap();
        if (map != null) {
            String userHostIp = (String)map.get("userHostIP");
            if (userHostIp != null) {
                log.setUserHostIp(userHostIp);
            }
            String requestUrl = (String)map.get("requestURL");
            if (requestUrl != null) {
                log.setRequestUrl(requestUrl);
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.nanoTime();
        ExceptionInfo exceptionInfo = null;
        Object result;

        try {
            result = invocation.proceed();
        } catch (Exception e) {
            exceptionInfo = handleException(e);
            throw e;
        } finally {
            processLogging(invocation, startTime, exceptionInfo);
        }
        return result;
    }

    private void processLogging(MethodInvocation invocation, long startTime, ExceptionInfo exceptionInfo) {
        Method method = invocation.getMethod();
        FlowableLog flowableLog = method.getAnnotation(FlowableLog.class);
        if (flowableLog != null && flowableLog.enable()) {
            RequestInfo requestInfo = collectRequestInfo();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            try {
                FlowableAccessLog flowableAccessLog =
                    buildFlowableAccessLog(method, flowableLog, invocation, elapsedTime, requestInfo, exceptionInfo);
                flowableAccessLogReporter.report(flowableAccessLog);
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            // 设置响应头、LogFilter 见到这个标志后，就不再记录日志了，因为这里已经写了日志，不需要重复写。
            if (requestInfo.getResponse() != null) {
                requestInfo.getResponse().setHeader("y9aoplog", "true");
            }
        }
    }

    @Getter
    private static class ExceptionInfo {
        private final String success;
        private final String errorMessage;
        private final String throwableStr;

        public ExceptionInfo(String success, String errorMessage, String throwableStr) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.throwableStr = throwableStr;
        }
    }

    @Setter
    @Getter
    private static class RequestInfo {
        private String userAgent = "";
        private String hostIp = "";
        private String processSerialNumber = "";
        private String optName = "";
        private HttpServletResponse response;
    }
}