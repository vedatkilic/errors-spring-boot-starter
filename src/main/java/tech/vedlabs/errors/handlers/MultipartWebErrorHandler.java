package tech.vedlabs.errors.handlers;

import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.CommonErrorCode;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

import static tech.vedlabs.errors.Argument.arg;
import static java.util.Collections.emptyList;

public class MultipartWebErrorHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MultipartException;
    }

    @NonNull
    @Override
    public HandledException handle(Throwable exception) {
        ErrorCode errorCode = CommonErrorCode.MULTIPART_EXPECTED;
        List<Argument> arguments = emptyList();

        if (exception instanceof MaxUploadSizeExceededException) {
            long maxSize = ((MaxUploadSizeExceededException) exception).getMaxUploadSize();
            errorCode = CommonErrorCode.MAX_SIZE;
            arguments.add(arg("max_size", maxSize));
        }

        return new HandledException(errorCode, null, arguments, exception);
    }
}
