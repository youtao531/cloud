package com.makto.seetaface.infrastructure.exception.constant;

import com.makto.seetaface.domain.model.basic.ComCodes;

/**
 * 资源不存在
 *
 * @author Yt on 2023/1/5 18:42
 */
public class NotFoundException extends ErrorException {

    public NotFoundException() {
        this(null);
    }

    public NotFoundException(String errorMessage) {
        super(ComCodes.NOT_FOUND.getCode(), errorMessage);
    }
}
