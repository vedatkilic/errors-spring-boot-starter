package tech.vedlabs.errors.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;

public enum SecurityErrorCode implements ErrorCode {

    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    ACCOUNT_EXPIRED(HttpStatus.BAD_REQUEST),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_CREDENTIALS(HttpStatus.BAD_REQUEST),
    USER_LOCKED(HttpStatus.BAD_REQUEST),
    USER_DISABLED(HttpStatus.BAD_REQUEST);

    SecurityErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Getter
    private final HttpStatus httpStatus;

    public String getCode() {
        return this.name();
    }
}
