package com.makto.seetaface.infrastructure.exception.constant;

import com.makto.seetaface.domain.model.basic.ComCodes;

/**
 * 未授权
 *
 * @author Yt on 2023/1/6 14:46
 */
public class UnauthorizedException extends ErrorException {

    public UnauthorizedException() {
        this(null);
    }

    public UnauthorizedException(String errorMessage) {
        super(ComCodes.UNAUTHORIZED.getCode(), errorMessage);
    }
}
