package com.framework.cloud.infrastructure.exception.constant;

import com.framework.cloud.domain.model.basic.ComCodes;
import lombok.Getter;

/**
 * @author Yt on 2023/1/5 16:26
 */
@Getter
public class ErrorException extends ComException {

    private final Integer statusCode;
    private final String errorMessage;

    public ErrorException(Integer statusCode, String errorMessage) {
        super(ComCodes.getEnum(statusCode), errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
