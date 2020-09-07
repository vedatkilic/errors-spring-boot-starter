package tech.vedlabs.errors.handlers;

import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorMessage;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

import java.util.Locale;

public class DefaultExceptionHandler implements ExceptionHandler {

    public static final String ERROR_CODE = "UNKNOWN_ERROR";

    @Override
    public boolean canHandle(Throwable exception) {
        return false;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        return new HandledException(HttpStatus.INTERNAL_SERVER_ERROR, new ErrorMessage(ERROR_CODE, null, exception.getMessage()));
    }
}
