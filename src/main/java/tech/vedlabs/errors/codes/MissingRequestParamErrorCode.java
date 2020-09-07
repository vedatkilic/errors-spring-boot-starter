package tech.vedlabs.errors.codes;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import tech.vedlabs.errors.ErrorCode;

public enum MissingRequestParamErrorCode implements ErrorCode {

    MISSING_HEADER(HttpStatus.BAD_REQUEST),
    MISSING_COOKIE(HttpStatus.BAD_REQUEST),
    MISSING_MATRIX_VARIABLE(HttpStatus.BAD_REQUEST),
    MISSING_PART(HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST);

    MissingRequestParamErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Getter
    private final HttpStatus httpStatus;

    @Override
    public String getCode() {
        return this.name();
    }
}
