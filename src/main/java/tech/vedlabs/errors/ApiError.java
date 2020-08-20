package tech.vedlabs.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private ErrorCode errorCode;
    private String message;

    @JsonUnwrapped
    private ErrorDetail errorDetail;

    @JsonIgnore
    private Exception exception;

    @JsonIgnore
    private HttpServletRequest request;

    public ApiError(ErrorCode errorCode, String message, ErrorDetail errorDetail, Exception exception) {
       this(errorCode, message, errorDetail, exception, null);
    }

    public ApiError(ErrorCode errorCode, String message, ErrorDetail errorDetail, Exception exception, HttpServletRequest request) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorDetail = errorDetail;
        this.exception = exception;
        this.request = request;
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}