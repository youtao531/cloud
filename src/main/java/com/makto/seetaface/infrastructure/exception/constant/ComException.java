package com.makto.seetaface.infrastructure.exception.constant;

import com.makto.seetaface.domain.model.basic.ComCodes;
import lombok.Getter;

/**
 * @author Yt on 2023/1/5 16:15
 */
@Getter
public abstract class ComException extends RuntimeException {

    private final int code;
    private final String message;

    public ComException(ComCodes codes, String message) {
        super(message);
        this.code = codes.getCode();
        this.message = message;
    }
}
