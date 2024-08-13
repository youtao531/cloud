package com.framework.cloud.infrastructure.web.interceptor;

import com.framework.cloud.domain.model.basic.ComHeaders;
import com.framework.cloud.infrastructure.web.WebParamUtil;
import com.framework.cloud.infrastructure.web.context.OpenContextImpl;
import com.framework.cloud.infrastructure.web.context.ServiceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.http.server.servlet.ServletUtil;
import org.dromara.hutool.json.JSONUtil;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * 进入的请求入口 拦截器
 *
 * @author youtao531 2023/4/26 14:52
 */
@Slf4j
public class ServiceContextInterceptor implements HandlerInterceptor {

    private final static String REQ_INTERCEPTOR_INSTANT_KEY = "S_INSTANT_KEY";
    private final static String REQ_INTERCEPTOR_MESSAGE_KEY = "S_MESSAGE_KEY";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        this.initOpenContext(request, response, handler);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        ServiceContext context = ServiceContext.getCurrentContext();

        Map<String, String> customHeaderMap = context.getOpenContext().getCustomHeaderMap();
        LocalDateTime now = LocalDateTime.now();
        long runMillis = ChronoUnit.MILLIS.between((LocalDateTime) context.get(REQ_INTERCEPTOR_INSTANT_KEY), now);
        String message = context.get(REQ_INTERCEPTOR_MESSAGE_KEY) +
                """
                        请求标头\t: %s
                        结束时间\t: %s
                        请求耗时\t: %s ms
                        =================== End ===================
                        """.formatted(JSONUtil.toJsonStr(customHeaderMap), now, runMillis);
        log.info(message);

        context.unset();
        MDC.remove(ComHeaders.NONCE_KEY);
    }

    private void initOpenContext(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String remoteIp = ServletUtil.getClientIP(request);
        LocalDateTime now = LocalDateTime.now();
        String message = """
                =================== Start ===================
                开始时间\t: %s
                参数信息\t: %s %s %s
                """.formatted(now, remoteIp, request.getMethod(), request.getRequestURI());

        ServiceContext context = ServiceContext.getCurrentContext();
        context.set(REQ_INTERCEPTOR_INSTANT_KEY, now);
        context.set(REQ_INTERCEPTOR_MESSAGE_KEY, message);
        context.setRequest(request);
        context.setResponse(response);
        if (handler instanceof HandlerMethod
                //解决 multiform/data 请求的 OpenContext 为空问题
                || handler instanceof ResourceHttpRequestHandler
        ) {
            Map<String, String> headerMap = ServletUtil.getHeaderMap(request);
            Map<String, Object> requestParams = WebParamUtil.getRequestParams(request);
            context.setOpenContext(new OpenContextImpl(requestParams, headerMap, remoteIp));
        }
    }
}