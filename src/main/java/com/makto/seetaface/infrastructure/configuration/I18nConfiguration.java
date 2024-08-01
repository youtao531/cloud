package com.makto.seetaface.infrastructure.configuration;

import com.makto.seetaface.infrastructure.i18n.CustomLocaleChangeInterceptor;
import com.makto.seetaface.infrastructure.web.interceptor.ServiceContextInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * 国际化配置
 *
 * @author Yt on 2022/12/10 18:25
 */
@Configuration
public class I18nConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new ServiceContextInterceptor())
                .addPathPatterns("/api/**")
                .order(1);

        registry
                .addInterceptor(new CustomLocaleChangeInterceptor("lang"))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/favicon.ico")
                .order(2);
    }

    /**
     * 重要：Bean Name 只能为`localeResolver`
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
}
