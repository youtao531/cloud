package com.framework.cloud.infrastructure.exception;

import com.framework.cloud.domain.model.ComCodes;
import com.framework.cloud.domain.model.ComResult;
import com.framework.cloud.infrastructure.exception.constant.CommonException;
import com.framework.cloud.infrastructure.exception.constant.ErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author youtao531 on 2022/12/8 11:40
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = CommonException.class)
    public ComResult<Void> handleComException(CommonException exception) {
        return buildResponseBodyForThrowable(exception);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public ComResult<Void> handleException(Throwable throwable) {
        return buildResponseBodyForThrowable(throwable);
    }

    public static ComResult<Void> buildResponseBodyForThrowable(Object throwable) {
        CommonException commonException = null;
        if (throwable instanceof CommonException) {
            commonException = (CommonException) throwable;
        }
        if (null == commonException) {
            Throwable t = (Throwable) throwable;
            commonException = new ErrorException(ComCodes.BAD_REQUEST.getCode(), t.getMessage());
        }

        ComCodes codes = ComCodes.getEnum(commonException.getCode());
        String message = commonException.getMessage();
        return ComResult.fail(codes, message);
    }
}
