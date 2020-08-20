package tech.vedlabs.errors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class HandledException {
    private ErrorCode errorCode;
    private ErrorDetail errorDetail;
    private List<Argument> arguments;
    private Throwable exception;
}