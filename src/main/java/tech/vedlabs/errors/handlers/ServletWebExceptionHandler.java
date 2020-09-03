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
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.MissingRequestParamErrorCode;

import java.util.List;
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
    public HandledException handle(Throwable exception) {
        if (exception instanceof HttpMessageNotReadableException)
            return new HandledException(GenericErrorCode.INVALID_OR_MISSING_BODY, null, null, exception);

        if (exception instanceof HttpMediaTypeNotAcceptableException) {
            Set<String> types = getMediaTypes(((HttpMediaTypeNotAcceptableException) exception).getSupportedMediaTypes());
            List<Argument> args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
            return new HandledException(GenericErrorCode.NOT_ACCEPTABLE, null, args, exception);
        }

        if (exception instanceof HttpMediaTypeNotSupportedException) {
            MediaType contentType = ((HttpMediaTypeNotSupportedException) exception).getContentType();
            List<Argument> arguments = null;
            if (contentType != null) arguments = singletonList(arg("type", contentType.toString()));
            return new HandledException(GenericErrorCode.NOT_SUPPORTED, null, arguments == null ? emptyList() : arguments, exception);
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            String method = ((HttpRequestMethodNotSupportedException) exception).getMethod();
            return new HandledException(GenericErrorCode.METHOD_NOT_ALLOWED, null, singletonList(arg("method", method)), exception);
        }

        if (exception instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException actualException = (MissingServletRequestParameterException) exception;
            String name = actualException.getParameterName();
            String type = actualException.getParameterType();
            return new HandledException(MissingRequestParamErrorCode.MISSING_PART, null, asList(arg("name", name), arg("expected", type)), exception);
        }

        if (exception instanceof MissingServletRequestPartException) {
            String name = ((MissingServletRequestPartException) exception).getRequestPartName();
            return new HandledException(MissingRequestParamErrorCode.MISSING_PART, null, singletonList(arg("name", name)), exception);
        }

        if (exception instanceof NoHandlerFoundException) {
            String url = ((NoHandlerFoundException) exception).getRequestURL();
            return new HandledException(GenericErrorCode.NO_HANDLER, null, asList(arg("path", url)), exception);
        }

        return new HandledException(GenericErrorCode.UNKNOWN_ERROR, null, null, exception);
    }

    private Set<String> getMediaTypes(List<MediaType> mediaTypes) {
        if (mediaTypes == null) return emptySet();

        return mediaTypes.stream().map(MediaType::toString).collect(toSet());
    }
}
