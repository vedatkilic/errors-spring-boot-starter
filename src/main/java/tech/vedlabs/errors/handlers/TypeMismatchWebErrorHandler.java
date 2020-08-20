package tech.vedlabs.errors.handlers;

import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.CommonErrorCode;
import org.springframework.beans.TypeMismatchException;
import org.springframework.lang.NonNull;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

import static tech.vedlabs.errors.Argument.arg;

public class TypeMismatchWebErrorHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof TypeMismatchException;
    }

    @NonNull
    @Override
    public HandledException handle(Throwable exception) {
        TypeMismatchException mismatchException = (TypeMismatchException) exception;
        List<Argument> arguments = getArguments(mismatchException);

        return new HandledException(CommonErrorCode.TYPE_MISMATCH, null, arguments, exception);
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

    private static String getPropertyName(TypeMismatchException mismatchException) {
        if (mismatchException instanceof MethodArgumentTypeMismatchException)
            return ((MethodArgumentTypeMismatchException) mismatchException).getName();

        return mismatchException.getPropertyName();
    }
}
