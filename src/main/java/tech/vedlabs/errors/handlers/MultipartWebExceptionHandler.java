package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;

import java.util.List;

import static java.util.Collections.emptyList;
import static tech.vedlabs.errors.Argument.arg;

@Order(3)
public class MultipartWebExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MultipartException;
    }

    @NonNull
    @Override
    public HandledException handle(Throwable exception) {
        ErrorCode errorCode = GenericErrorCode.MULTIPART_EXPECTED;
        List<Argument> arguments = emptyList();

        if (exception instanceof MaxUploadSizeExceededException) {
            long maxSize = ((MaxUploadSizeExceededException) exception).getMaxUploadSize();
            errorCode = GenericErrorCode.MAX_SIZE;
            arguments.add(arg("max_size", maxSize));
        }

        return new HandledException(errorCode, null, arguments, exception);
    }
}
