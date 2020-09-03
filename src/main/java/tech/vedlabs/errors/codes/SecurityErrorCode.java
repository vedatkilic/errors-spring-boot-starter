package tech.vedlabs.errors.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;

public enum SecurityErrorCode implements ErrorCode {

    ACCESS_DENIED(HttpStatus.FORBIDDEN,"security.access_denied"),
    ACCOUNT_EXPIRED(HttpStatus.BAD_REQUEST, "security.account_expired"),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "security.auth_required"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "security.internal_error"),
    BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "security.bad_credentials"),
    USER_LOCKED(HttpStatus.BAD_REQUEST, "security.user_locked"),
    USER_DISABLED(HttpStatus.BAD_REQUEST, "security.user_disabled");

    SecurityErrorCode(HttpStatus httpStatus, String code) {
       this(httpStatus, code, null);
    }

    SecurityErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Getter
    private final HttpStatus httpStatus;

    @Getter
    private final String code;

    @Getter
    private final String message;
}
