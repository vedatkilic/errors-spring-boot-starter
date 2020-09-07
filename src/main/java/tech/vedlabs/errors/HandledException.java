package tech.vedlabs.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

@Data
@Builder
@AllArgsConstructor
public class HandledException {
    private final HttpStatus statusCode;
    private final ErrorMessage errorMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();;

    private ErrorDetail errorDetail;

    public HandledException(@NonNull HttpStatus statusCode,
                            @NonNull ErrorMessage errorMessage,
                            @Nullable ErrorDetail errorDetail) {
        timestamp = LocalDateTime.now();
        enforcePreconditions(errorMessage, statusCode);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.errorDetail = errorDetail;
    }

    public HandledException(@NonNull HttpStatus statusCode,
                            @Nullable ErrorMessage errorMessage) {
        this(statusCode, errorMessage, null);
    }

    private void enforcePreconditions(ErrorMessage errorMessage, HttpStatus statusCode) {
        requireNonNull(errorMessage.getErrorCode(), "Error code is required");
        requireNonNull(statusCode, "Status code is required");
    }
}