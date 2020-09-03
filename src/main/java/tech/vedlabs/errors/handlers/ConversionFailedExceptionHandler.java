package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;

@Order(1)
public class ConversionFailedExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ConversionFailedException;
    }

    @Override
    public HandledException handle(Throwable exception) {
        return HandledException.builder()
                .errorCode(GenericErrorCode.CONVERSION_FAILED)
                .exception(exception)
                .build();
    }
}
