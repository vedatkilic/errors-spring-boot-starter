package tech.vedlabs.errors.handlers;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorMessage;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static tech.vedlabs.errors.Argument.arg;

@Order(8)
public class TypeMismatchWebExceptionHandler implements ExceptionHandler {

    public static final String TYPE_MISMATCH = "TYPE_MISMATCH";

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof TypeMismatchException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        TypeMismatchException mismatchException = (TypeMismatchException) exception;
        List<Argument> arguments = getArguments(mismatchException);
        String errorCode = getErrorCode(mismatchException);
        return new HandledException(HttpStatus.BAD_REQUEST, new ErrorMessage(errorCode, arguments, exception.getMessage()));
    }

    public List<Argument> getArguments(TypeMismatchException mismatchException) {
        List<Argument> arguments = new ArrayList<>();
        arguments.add(arg("property", getPropertyName(mismatchException)));
        arguments.add(arg("invalid", mismatchException.getValue()));
        Class<?> requiredType = mismatchException.getRequiredType();
        if (requiredType != null) {
            arguments.add(arg("expected", requiredType.getName()));
        }

        return arguments;
    }

    public String getErrorCode(TypeMismatchException mismatchException) {
        return String.format("%s.%s", TYPE_MISMATCH, getPropertyName(mismatchException));
    }

    private String getPropertyName(TypeMismatchException mismatchException) {
        if (mismatchException instanceof MethodArgumentTypeMismatchException)
            return ((MethodArgumentTypeMismatchException) mismatchException).getName();

        return mismatchException.getPropertyName();
    }
}
