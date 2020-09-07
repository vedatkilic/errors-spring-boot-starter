package tech.vedlabs.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException implements Serializable {
    private ErrorCode errorCode;
    private String message;

    private ErrorDetail errorDetail;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.errorCode.getHttpStatus();
    }

    public String getCode() {
        return this.errorCode.getCode();
    }
}

