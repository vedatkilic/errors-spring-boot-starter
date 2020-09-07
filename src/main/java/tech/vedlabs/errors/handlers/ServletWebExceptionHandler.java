package tech.vedlabs.errors.handlers;

import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.MissingRequestParamErrorCode;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;
import static tech.vedlabs.errors.Argument.arg;

@Order(5)
public class ServletWebExceptionHandler implements ExceptionHandler {

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof HttpMediaTypeNotAcceptableException ||
                exception instanceof HttpMediaTypeNotSupportedException ||
                exception instanceof HttpRequestMethodNotSupportedException ||
                exception instanceof MissingServletRequestParameterException ||
                exception instanceof MissingServletRequestPartException ||
                exception instanceof NoHandlerFoundException ||
                exception instanceof HttpMessageNotReadableException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        ErrorCode errorCode = GenericErrorCode.UNKNOWN_ERROR;
        List<Argument> args = null;

        if (exception instanceof HttpMessageNotReadableException) errorCode = GenericErrorCode.INVALID_OR_MISSING_BODY;

        if (exception instanceof HttpMediaTypeNotAcceptableException) {
            Set<String> types = getMediaTypes(((HttpMediaTypeNotAcceptableException) exception).getSupportedMediaTypes());
            errorCode = GenericErrorCode.NOT_ACCEPTABLE;
            args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
        }

        if (exception instanceof HttpMediaTypeNotSupportedException) {
            MediaType contentType = ((HttpMediaTypeNotSupportedException) exception).getContentType();
            if (contentType != null) args = singletonList(arg("type", contentType.toString()));
            errorCode = GenericErrorCode.NOT_SUPPORTED;
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            String method = ((HttpRequestMethodNotSupportedException) exception).getMethod();
            errorCode = GenericErrorCode.METHOD_NOT_ALLOWED;
            args = singletonList(arg("method", method));
        }

        if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException actualException = (MissingServletRequestParameterException) exception;
            String name = actualException.getParameterName();
            String type = actualException.getParameterType();
            errorCode = MissingRequestParamErrorCode.MISSING_PART;
            args = asList(arg("name", name), arg("expected", type));
        }

        if (exception instanceof MissingServletRequestPartException) {
            String name = ((MissingServletRequestPartException) exception).getRequestPartName();
            errorCode = MissingRequestParamErrorCode.MISSING_PART;
            args = singletonList(arg("name", name));
        }

        if (exception instanceof NoHandlerFoundException) {
            String url = ((NoHandlerFoundException) exception).getRequestURL();
            errorCode = GenericErrorCode.NO_HANDLER;
            args = singletonList(arg("path", url));
        }

        return new HandledException(errorCode.getHttpStatus(), new ErrorMessage(errorCode.getCode(), args, exception.getMessage()));
    }

    private Set<String> getMediaTypes(List<MediaType> mediaTypes) {
        if (mediaTypes == null) return emptySet();

        return mediaTypes.stream().map(MediaType::toString).collect(toSet());
    }
}
