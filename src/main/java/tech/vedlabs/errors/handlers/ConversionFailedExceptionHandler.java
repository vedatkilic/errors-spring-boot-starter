package tech.vedlabs.errors.handlers;

import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.CommonErrorCode;
import org.springframework.core.convert.ConversionFailedException;

public class ConversionFailedExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ConversionFailedException;
    }

    @Override
    public HandledException handle(Throwable exception) {
        return HandledException.builder()
                .errorCode(CommonErrorCode.CONVERSION_FAILED)
                .exception(exception)
                .build();
    }
}
