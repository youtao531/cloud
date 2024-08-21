package com.framework.cloud.infrastructure.exception.constant;

import com.framework.cloud.domain.core.ComCodes;

/**
 * @author youtao531 on 2023/1/5 18:51
 */
public class ForbiddenCommonException extends ErrorException {

    public ForbiddenCommonException() {
        this(null);
    }

    public ForbiddenCommonException(String errorMessage) {
        super(ComCodes.FORBIDDEN.getCode(), errorMessage);
    }
}
