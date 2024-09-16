package com.framework.cloud.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 业务配置
 *
 * @author youtao531 on 2023/10/12 15:38
 */
@Data
@Component
@ConfigurationProperties(prefix = "flexible.paddle")
public class BizPaddleProperties {

    /**
     * Paddle服务地址
     */
    private String paddleUrl;
}