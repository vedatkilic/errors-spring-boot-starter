package tech.vedlabs.errors.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.*;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.MissingRequestParamErrorCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toSet;
import static tech.vedlabs.errors.Argument.arg;

@Order(4)
@RequiredArgsConstructor
public class ResponseStatusExceptionHandler implements ExceptionHandler {

    private final SpringValidationWebExceptionHandler validationWebErrorHandler;
    private final TypeMismatchWebExceptionHandler typeMismatchWebExceptionHandler;

    @Override
    public boolean canHandle(Throwable exception) {
        return exception instanceof ResponseStatusException;
    }

    @Override
    public HandledException handle(Throwable exception, Locale locale) {
        List<Argument> args = null;
        ErrorCode errorCode = GenericErrorCode.UNKNOWN_ERROR;

        if (exception instanceof WebExchangeBindException) {
            return validationWebErrorHandler.handle(exception, locale);
        }

        if (exception instanceof ServerWebInputException) {
            MethodParameter parameter = ((ServerWebInputException) exception).getMethodParameter();
            if (exception.getCause() instanceof TypeMismatchException) {
                TypeMismatchException cause = ((TypeMismatchException) exception.getCause());
                if (parameter != null) {
                    cause.initPropertyName(Objects.requireNonNull(parameter.getParameterName()));
                }

                return typeMismatchWebExceptionHandler.handle(cause, locale);
            }

            HandledException handledException = handleMissingParameters(parameter);
            if (handledException != null) return handledException;
            errorCode = GenericErrorCode.INVALID_OR_MISSING_BODY;
        }

        if (exception instanceof UnsupportedMediaTypeStatusException) {
            Set<String> types = getMediaTypes(((UnsupportedMediaTypeStatusException) exception).getSupportedMediaTypes());
            args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
            errorCode = GenericErrorCode.NOT_SUPPORTED;
        }

        if (exception instanceof NotAcceptableStatusException) {
            Set<String> types = getMediaTypes(((NotAcceptableStatusException) exception).getSupportedMediaTypes());
            args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
            errorCode = GenericErrorCode.NOT_ACCEPTABLE;
        }

        if (exception instanceof MethodNotAllowedException) {
            String httpMethod = ((MethodNotAllowedException) exception).getHttpMethod();
            args = singletonList(arg("method", httpMethod));
            errorCode = GenericErrorCode.METHOD_NOT_ALLOWED;
        }

        if (exception instanceof ResponseStatusException) {
            HttpStatus status = ((ResponseStatusException) exception).getStatus();
            if (status == HttpStatus.NOT_FOUND) errorCode = GenericErrorCode.NO_HANDLER;
        }

        return new HandledException(errorCode.getHttpStatus(), new ErrorMessage(errorCode.getCode(), args, exception.getMessage()));
    }

    private HandledException handleMissingParameters(MethodParameter parameter) {
        if (parameter == null) return null;

        ErrorCode code = null;
        String parameterName = null;

        RequestHeader requestHeader = parameter.getParameterAnnotation(RequestHeader.class);
        if (requestHeader != null) {
            code = MissingRequestParamErrorCode.MISSING_HEADER;
            parameterName = extractParameterName(requestHeader, parameter);
        }

        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            code = MissingRequestParamErrorCode.MISSING_PARAMETER;
            parameterName = extractParameterName(requestParam, parameter);
        }

        RequestPart requestPart = parameter.getParameterAnnotation(RequestPart.class);
        if (requestPart != null) {
            code = MissingRequestParamErrorCode.MISSING_PART;
            parameterName = extractParameterName(requestPart, parameter);
        }

        CookieValue cookieValue = parameter.getParameterAnnotation(CookieValue.class);
        if (cookieValue != null) {
            code = MissingRequestParamErrorCode.MISSING_COOKIE;
            parameterName = extractParameterName(cookieValue, parameter);
        }

        MatrixVariable matrixVariable = parameter.getParameterAnnotation(MatrixVariable.class);
        if (matrixVariable != null) {
            code = MissingRequestParamErrorCode.MISSING_MATRIX_VARIABLE;
            parameterName = extractParameterName(matrixVariable, parameter);
        }

        if (code != null) {
            return new HandledException(code.getHttpStatus(), new ErrorMessage(code.getCode(), asList(arg("name", parameterName), arg("expected",parameter.getParameterType().getName())), null));
        }

        return null;
    }

    private String extractParameterName(Annotation annotation, MethodParameter parameter) {
        String name = getNameAttribute(annotation);

        return name.isEmpty() ? parameter.getParameterName() : name;
    }

    private String getNameAttribute(Annotation annotation) {
        try {
            Method method = annotation.getClass().getMethod("name");
            return (String) method.invoke(annotation);
        } catch (Exception e) {
            return "";
        }
    }

    private Set<String> getMediaTypes(List<MediaType> mediaTypes) {
        if (mediaTypes == null) return emptySet();

        return mediaTypes.stream().map(MediaType::toString).collect(toSet());
    }
}
