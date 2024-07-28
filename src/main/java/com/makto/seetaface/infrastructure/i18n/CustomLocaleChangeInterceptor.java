package com.makto.seetaface.infrastructure.i18n;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * 自定义国际化拦截器
 *
 * @author Yt on 2023/1/5 20:59
 */
public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {

    public CustomLocaleChangeInterceptor() {
        super();
    }

    public CustomLocaleChangeInterceptor(String paramName) {
        this();
        super.setParamName(paramName);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
        String newLocale = request.getParameter(this.getParamName());
        if (null == newLocale) {
            String headers = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            if (null != headers) {
                newLocale = headers.split(",")[0];
            }
        }
        if (null != newLocale) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }

            try {
                localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
            } catch (IllegalArgumentException var7) {
                if (!this.isIgnoreInvalidLocale()) {
                    throw var7;
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + var7.getMessage());
                }
            }
        }

        return true;
    }
}
