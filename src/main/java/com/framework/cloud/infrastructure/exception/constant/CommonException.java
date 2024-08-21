package com.framework.cloud.infrastructure.exception.constant;

import com.framework.cloud.domain.core.ComCodes;
import lombok.Getter;

/**
 * @author youtao531 on 2023/1/5 16:15
 */
@Getter
public abstract class CommonException extends RuntimeException {

    private final int code;
    private final String message;

    public CommonException(ComCodes codes, String message) {
        super(message);
        this.code = codes.getCode();
        this.message = message;
    }
}
