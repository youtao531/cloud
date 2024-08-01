package com.makto.seetaface.infrastructure.exception.constant;

import com.makto.seetaface.domain.model.basic.ComCodes;

/**
 * @author Yt on 2023/1/5 18:51
 */
public class ForbiddenException extends ErrorException {

    public ForbiddenException() {
        this(null);
    }

    public ForbiddenException(String errorMessage) {
        super(ComCodes.FORBIDDEN.getCode(), errorMessage);
    }
}
