package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import tech.vedlabs.errors.ErrorMessage;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;

import java.util.Locale;

@Order(1)
public class ConversionFailedExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ConversionFailedException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        return  HandledException.builder()
                .statusCode(GenericErrorCode.CONVERSION_FAILED.getHttpStatus())
                .errorMessage(new ErrorMessage(GenericErrorCode.CONVERSION_FAILED.getCode(), null, exception.getMessage()))
                .build();
    }
}
