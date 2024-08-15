package com.framework.cloud.infrastructure.web.filter;

import com.framework.cloud.infrastructure.web.MyHttpServletRequestWrapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * 重写请求体 过滤器
 *
 * @author youtao531 on 2023/4/26 11:41
 */
@Component
@Order(-101)
@WebFilter(filterName = "RewriteRequestFilter", urlPatterns = "/**")
public class RewriteRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //文件上传类型 不需要处理，否则会报java.nio.charset.MalformedInputException: Input length = 1异常
        if (Objects.isNull(request) || Optional.ofNullable(request.getContentType()).orElse(StringUtils.EMPTY).startsWith("multipart/")) {
            chain.doFilter(request, response);
            return;
        }
        MyHttpServletRequestWrapper requestWrapper = null;
        if (request instanceof HttpServletRequest) {
            //自定义wrapper 处理流，必须在过滤器中处理，然后通过FilterChain传下去， 否则重写后的getInputStream（）方法不会被调用
            requestWrapper = new MyHttpServletRequestWrapper((HttpServletRequest) request);
        }
        chain.doFilter(Objects.requireNonNullElse(requestWrapper, request), response);
    }
}