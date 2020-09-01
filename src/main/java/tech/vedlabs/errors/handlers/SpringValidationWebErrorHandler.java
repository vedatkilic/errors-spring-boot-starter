package tech.vedlabs.errors.handlers;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

import javax.validation.ConstraintViolation;

import java.util.List;

import static java.util.Collections.emptyList;

public class SpringValidationWebErrorHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MethodArgumentNotValidException || exception instanceof BindException;
    }

    @Override
    public HandledException handle(Throwable exception) {
        BindingResult bindingResult = getBindingResult(exception);
        ObjectError error = bindingResult.getAllErrors().get(0);
         return HandledException.builder()
                 .errorCode(this.errorCode(error))
                 .arguments(this.arguments(error))
                 .exception(exception)
                 .build();
    }

    private BindingResult getBindingResult(Throwable exception) {
        return exception instanceof BindingResult ?
                ((BindingResult) exception) :
                ((MethodArgumentNotValidException) exception).getBindingResult();
    }

    private ErrorCode errorCode(ObjectError error) {
        ErrorCode errorCode = null;
        try {
           errorCode = ConstraintViolations.getErrorCode(error.unwrap(ConstraintViolation.class));
        } catch (Exception ignored) {
        }

        if (errorCode == null) {
            try {
                errorCode = TypeMismatchWebErrorHandler.getErrorCode(error.unwrap(TypeMismatchException.class));
            } catch (Exception ignored) {
            }
        }

        if (errorCode == null) errorCode = new ErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public String getCode() {
                return "BINDING_FAILURE";
            }

            @Override
            public String getMessage() {
                return null;
            }
        };

        return errorCode;
    }

    private List<Argument> arguments(ObjectError error) {
        try {
            ConstraintViolation<?> violation = error.unwrap(ConstraintViolation.class);
            return ConstraintViolations.getArguments(violation);
        } catch (Exception ignored) {
        }

        try {
            return TypeMismatchWebErrorHandler.getArguments(error.unwrap(TypeMismatchException.class));
        } catch (Exception ignored) {
        }

        return emptyList();
    }
}
