package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.codes.GenericErrorCode;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static tech.vedlabs.errors.Argument.arg;

@Order(3)
public class MultipartWebExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MultipartException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        ErrorCode errorCode = GenericErrorCode.MULTIPART_EXPECTED;
        List<Argument> arguments = emptyList();

        if (exception instanceof MaxUploadSizeExceededException) {
            long maxSize = ((MaxUploadSizeExceededException) exception).getMaxUploadSize();
            errorCode = GenericErrorCode.MAX_SIZE;
            arguments.add(arg("max_size", maxSize));
        }

        return new HandledException(errorCode.getHttpStatus(), new ErrorMessage(errorCode.getCode(), arguments, exception.getMessage()));
    }
}
