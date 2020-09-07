package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MissingMatrixVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.MissingRequestParamErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static tech.vedlabs.errors.Argument.arg;

@Order(2)
public class MissingRequestParametersExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof MissingRequestHeaderException ||
                exception instanceof MissingRequestCookieException ||
                exception instanceof MissingMatrixVariableException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        List<Argument> arguments = new ArrayList<>();
        ErrorCode errorCode = GenericErrorCode.UNKNOWN_ERROR;

        if (exception instanceof MissingRequestHeaderException) {
            MissingRequestHeaderException headerException = (MissingRequestHeaderException) exception;
            arguments.add(arg("name", headerException.getHeaderName()));
            arguments.add(arg("expected", headerException.getParameter()));

            errorCode = MissingRequestParamErrorCode.MISSING_HEADER;
        } else if (exception instanceof MissingRequestCookieException) {
            MissingRequestCookieException cookieException = (MissingRequestCookieException) exception;
            arguments.add(arg("name", cookieException.getCookieName()));
            arguments.add(arg("expected", cookieException.getParameter()));

            errorCode = MissingRequestParamErrorCode.MISSING_COOKIE;
        } else if (exception instanceof MissingMatrixVariableException) {
            MissingMatrixVariableException variableException = (MissingMatrixVariableException) exception;
            arguments.add(arg("name", variableException.getVariableName()));
            arguments.add(arg("expected", variableException.getParameter()));

            errorCode = MissingRequestParamErrorCode.MISSING_MATRIX_VARIABLE;
        }

        return new HandledException(errorCode.getHttpStatus(), new ErrorMessage(errorCode.getCode(), arguments, exception.getMessage()));
    }
}
