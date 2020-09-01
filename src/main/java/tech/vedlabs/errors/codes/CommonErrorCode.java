package tech.vedlabs.errors.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;

public enum CommonErrorCode implements ErrorCode {

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    CONVERSION_FAILED(HttpStatus.BAD_REQUEST),
    MAX_SIZE(HttpStatus.BAD_REQUEST),
    MULTIPART_EXPECTED(HttpStatus.BAD_REQUEST);

    CommonErrorCode(HttpStatus httpStatus) {
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
        return null;
    }
}
