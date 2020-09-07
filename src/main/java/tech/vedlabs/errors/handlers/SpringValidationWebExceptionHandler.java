package tech.vedlabs.errors.handlers;

import lombok.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.message.TemplateAwareMessageSource;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static tech.vedlabs.errors.Argument.arg;

@Order(7)
@RequiredArgsConstructor
public class SpringValidationWebExceptionHandler implements ExceptionHandler {

    private final TypeMismatchWebExceptionHandler typeMismatchWebExceptionHandler;
    private final TemplateAwareMessageSource templateAwareMessageSource;

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MethodArgumentNotValidException || exception instanceof BindException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        BindingResult bindingResult = getBindingResult(exception);
        List<ValidationError> errors = bindingResult.getFieldErrors()
                .stream()
                .map((error) -> {
                    ErrorMessage errorMessage = errorMessage(error, error.getDefaultMessage());
                    templateAwareMessageSource.interpolate(errorMessage, locale);
                    return ValidationError.builder()
                            .errorCode(errorMessage.getErrorCode())
                            .message(error.getDefaultMessage())
                            .field(error.getField())
                            .rejectedValue(error.getRejectedValue())
                            .build();
                })
                .collect(Collectors.toList());
        ErrorMessage errorMessage = new ErrorMessage("VALIDATION_ERROR", Collections.singletonList(arg("size", errors.size())), String.format("Validation failed for object='%s'. Error count: %s", bindingResult.getObjectName(), errors.size()));
        return new HandledException(HttpStatus.BAD_REQUEST, errorMessage, new ValidationErrors(errors));
    }

    private BindingResult getBindingResult(Throwable exception) {
        return exception instanceof BindingResult ?
                ((BindingResult) exception) :
                ((MethodArgumentNotValidException) exception).getBindingResult();
    }

    private ErrorMessage errorMessage(ObjectError error, String defaultMessage) {
        String code = null;
        try {
           code = ConstraintViolations.getErrorCode(error.unwrap(ConstraintViolation.class));
        } catch (Exception ignored) {
        }

        if (code == null) {
            try {
                code = typeMismatchWebExceptionHandler.getErrorCode(error.unwrap(TypeMismatchException.class));
            } catch (Exception ignored) {
            }
        }

        if (code == null) code = "VALIDATION_ERROR";
        code = code.replace("{", "").replace("}", "");
        return new ErrorMessage(code, arguments(error), defaultMessage);
    }

    private List<Argument> arguments(ObjectError error) {
        try {
            ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
            return ConstraintViolations.getArguments(violation);
        } catch (Exception ignored) {
        }

        try {
            return typeMismatchWebExceptionHandler.getArguments(error.unwrap(TypeMismatchException.class));
        } catch (Exception ignored) {
        }

        return emptyList();
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class ValidationErrors implements ErrorDetail {
        private List<ValidationError> errors;

        public ValidationErrors(List<ValidationError> errors) {
            this.errors = errors;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private Object rejectedValue;
        private String message;
        private String errorCode;
    }
}

