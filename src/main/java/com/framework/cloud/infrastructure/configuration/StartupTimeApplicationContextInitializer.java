package com.framework.cloud.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.NonNull;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 应用启动时间
 *
 * @author youtao531 2024/5/8 上午11:42
 */
@Slf4j
public class StartupTimeApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    private LocalDateTime startTime;

    @Override
    public void initialize(@NonNull ConfigurableWebApplicationContext applicationContext) {
        startTime = LocalDateTime.now();
        log.info("Application startup time {}", startTime);
    }

    public long getMillis() {
        return ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
    }
}
