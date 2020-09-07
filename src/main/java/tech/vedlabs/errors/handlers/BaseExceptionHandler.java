package tech.vedlabs.errors.handlers;


import org.springframework.core.annotation.Order;
import tech.vedlabs.errors.BaseException;
import tech.vedlabs.errors.ErrorMessage;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

import java.util.Locale;

@Order(1)
public class BaseExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof BaseException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        BaseException baseException = (BaseException) exception;
        return HandledException.builder()
                .statusCode(baseException.getHttpStatus())
                .errorMessage(new ErrorMessage(baseException.getCode(), baseException.getMessage()))
                .errorDetail(baseException.getErrorDetail())
                .build();
    }
}
