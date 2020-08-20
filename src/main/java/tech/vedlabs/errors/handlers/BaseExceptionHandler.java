package tech.vedlabs.errors.handlers;


import tech.vedlabs.errors.BaseException;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

public class BaseExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof BaseException;
    }

    @Override
    public HandledException handle(Throwable exception) {
        BaseException baseException = (BaseException) exception;

        return HandledException.builder()
                .errorCode(baseException.getErrorCode())
                .exception(exception)
                .build();
    }
}
