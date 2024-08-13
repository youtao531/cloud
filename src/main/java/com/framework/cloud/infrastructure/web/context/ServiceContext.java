package com.framework.cloud.infrastructure.web.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author youtao531 2023/4/26 14:21
 */
@Slf4j
public class ServiceContext extends ConcurrentHashMap<String, Object> {

    public static final String REQUEST_KEY = "S_REQ";
    public static final String RESPONSE_KEY = "S_REP";
    public static final String OPEN_CONTEXT_KEY = "S_OPEN_CONTEXT";

    protected static Class<? extends ServiceContext> contextClass = ServiceContext.class;

    protected static final ThreadLocal<? extends ServiceContext> THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return contextClass.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    });

    public ServiceContext() {
        super();
    }

    /**
     * 获取 当前请求上下文
     *
     * @return 当前请求上下文
     */
    public static ServiceContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    /**
     * 通过开放网关进入的请求才有值
     *
     * @return 网关参数
     */
    public OpenContext getOpenContext() {
        return (OpenContext) get(OPEN_CONTEXT_KEY);
    }

    public void setOpenContext(OpenContext openContext) {
        set(OPEN_CONTEXT_KEY, openContext);
    }

    public void set(String key, Object value) {
        if (value != null) {
            put(key, value);
        } else {
            remove(key);
        }
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) get(REQUEST_KEY);
    }

    /**
     * 调用微服务拦截器入口处设置此参数
     * <p>
     * 并判断是否有日志标识，若无则设置
     * </p>
     * sets the HttpServletRequest into the "request" key
     */
    public void setRequest(HttpServletRequest request) {
        put(REQUEST_KEY, request);
    }

    /**
     * @return the HttpServletResponse from the "response" key
     */
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) get(RESPONSE_KEY);
    }

    /**
     * sets the "response" key to the HttpServletResponse passed in
     */
    public void setResponse(HttpServletResponse response) {
        set(RESPONSE_KEY, response);
    }

    /**
     * unsets the THREAD_LOCAL context. Done at the end of the request.
     */
    public void unset() {
        THREAD_LOCAL.remove();
    }
}