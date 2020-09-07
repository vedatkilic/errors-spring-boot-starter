package tech.vedlabs.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {
    private final String errorCode;
    private final String message;

    @JsonIgnore
    private final HttpStatus statusCode;

    @JsonUnwrapped
    private ErrorDetail errorDetail;
}