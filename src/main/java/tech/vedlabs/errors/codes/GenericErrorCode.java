package tech.vedlabs.errors.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;

public enum GenericErrorCode implements ErrorCode {

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    CONVERSION_FAILED(HttpStatus.BAD_REQUEST),
    MAX_SIZE(HttpStatus.BAD_REQUEST),
    MULTIPART_EXPECTED(HttpStatus.BAD_REQUEST),
    NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),
    INVALID_OR_MISSING_BODY(HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),
    NO_HANDLER(HttpStatus.NOT_FOUND);


    GenericErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Getter
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }
}
