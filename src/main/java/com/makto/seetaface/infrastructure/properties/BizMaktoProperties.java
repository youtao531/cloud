package com.makto.seetaface.infrastructure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 业务配置
 *
 * @author Lcc 2023/10/12 15:38
 */
@Data
@Component
@ConfigurationProperties(prefix = "flexible.makto")
public class BizMaktoProperties {

    /**
     * Paddle服务地址
     */
    private String paddleUrl;
}