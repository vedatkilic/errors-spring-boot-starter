package tech.vedlabs.errors.handlers;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

public class DefaultExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return false;
    }

    @Override
    public HandledException handle(Throwable exception) {
        return HandledException.builder()
                .errorCode(DefaultErrorCode.UNKNOWN_ERROR)
                .exception(exception)
                .build();
    }

    enum DefaultErrorCode implements ErrorCode {

        UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

        DefaultErrorCode(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
        }

        @Getter
        private final HttpStatus httpStatus;

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getMessage() {
            return "Unknown Exception";
        }
    }
}
