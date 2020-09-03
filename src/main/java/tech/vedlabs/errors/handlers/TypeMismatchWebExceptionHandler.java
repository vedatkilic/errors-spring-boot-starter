package tech.vedlabs.errors.handlers;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

import java.util.ArrayList;
import java.util.List;

import static tech.vedlabs.errors.Argument.arg;

@Order(8)
public class TypeMismatchWebExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof TypeMismatchException;
    }

    @NonNull
    @Override
    public HandledException handle(Throwable exception) {
        TypeMismatchException mismatchException = (TypeMismatchException) exception;
        List<Argument> arguments = getArguments(mismatchException);
        ErrorCode errorCode = getErrorCode(mismatchException);

        return new HandledException(errorCode, null, arguments, exception);
    }

    static List<Argument> getArguments(TypeMismatchException mismatchException) {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(arg("property", getPropertyName(mismatchException)));
        arguments.add(arg("invalid", mismatchException.getValue()));
        Class<?> requiredType = mismatchException.getRequiredType();
        if (requiredType != null) {
            arguments.add(arg("expected", requiredType.getName()));
        }

        return arguments;
    }

    ErrorCode getErrorCode(TypeMismatchException mismatchException) {
        return new ErrorCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public String getCode() {
                return String.format("%s.%s", "TYPE_MISMATCH", getPropertyName(mismatchException))
                        .replace("{", "").replace("}", "");
            }

            @Override
            public String getMessage() {
                return null;
            }
        };
    }

    private static String getPropertyName(TypeMismatchException mismatchException) {
        if (mismatchException instanceof MethodArgumentTypeMismatchException)
            return ((MethodArgumentTypeMismatchException) mismatchException).getName();

        return mismatchException.getPropertyName();
    }
}
