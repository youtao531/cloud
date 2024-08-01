package com.makto.seetaface.infrastructure.exception;

import com.makto.seetaface.domain.model.basic.ComCodes;
import com.makto.seetaface.domain.model.basic.ComResult;
import com.makto.seetaface.infrastructure.exception.constant.ComException;
import com.makto.seetaface.infrastructure.exception.constant.ErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Yt on 2022/12/8 11:40
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static ComResult<Void> buildResponseBodyForThrowable(Object throwable) {
        log.warn("全局Exception异常处理器 {}, e={}", throwable.getClass().getSimpleName(), throwable);
        ComException comException = null;
        if (throwable instanceof ComException) {
            comException = (ComException) throwable;
        }
        if (null == comException) {
            Throwable t = (Throwable) throwable;
            comException = new ErrorException(ComCodes.BAD_REQUEST.getCode(), t.getMessage());
        }

        ComCodes codes = ComCodes.getEnum(comException.getCode());
        String message = comException.getMessage();
        return ComResult.fail(codes, message);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = ComException.class)
    public ComResult<Void> handleComException(ComException exception) {
        return buildResponseBodyForThrowable(exception);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public ComResult<Void> handleException(Throwable throwable) {
        return buildResponseBodyForThrowable(throwable);
    }
}
