package com.framework.cloud.infrastructure.exception.constant;

import com.framework.cloud.domain.model.basic.ComCodes;

/**
 * 资源不存在
 *
 * @author youtao531 on 2023/1/5 18:42
 */
public class NotFoundException extends ErrorException {

    public NotFoundException() {
        this(null);
    }

    public NotFoundException(String errorMessage) {
        super(ComCodes.NOT_FOUND.getCode(), errorMessage);
    }
}
