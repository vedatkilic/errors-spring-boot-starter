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
import tech.vedlabs.errors.Argument;
import tech.vedlabs.errors.ErrorCode;
import tech.vedlabs.errors.ExceptionHandler;
import tech.vedlabs.errors.HandledException;
import tech.vedlabs.errors.codes.GenericErrorCode;
import tech.vedlabs.errors.codes.MissingRequestParamErrorCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
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
    public HandledException handle(Throwable exception) {
        if (exception instanceof UnsupportedMediaTypeStatusException) {
            Set<String> types = getMediaTypes(((UnsupportedMediaTypeStatusException) exception).getSupportedMediaTypes());
            List<Argument> args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
            return new HandledException(GenericErrorCode.NOT_SUPPORTED, null, args, exception);
        }

        if (exception instanceof NotAcceptableStatusException) {
            Set<String> types = getMediaTypes(((NotAcceptableStatusException) exception).getSupportedMediaTypes());
            List<Argument> args = types.isEmpty() ? emptyList() : singletonList(arg("types", types));
            return new HandledException(GenericErrorCode.NOT_ACCEPTABLE, null, args, exception);
        }

        if (exception instanceof MethodNotAllowedException) {
            String httpMethod = ((MethodNotAllowedException) exception).getHttpMethod();
            return new HandledException(GenericErrorCode.METHOD_NOT_ALLOWED, null, singletonList(arg("method", httpMethod)), exception);
        }

        if (exception instanceof WebExchangeBindException) {
            return validationWebErrorHandler.handle(exception);
        }

        if (exception instanceof ServerWebInputException) {
            MethodParameter parameter = ((ServerWebInputException) exception).getMethodParameter();
            if (exception.getCause() instanceof TypeMismatchException) {
                TypeMismatchException cause = ((TypeMismatchException) exception.getCause());
                if (parameter != null) {
                    cause.initPropertyName(Objects.requireNonNull(parameter.getParameterName()));
                }

                return typeMismatchWebExceptionHandler.handle(cause);
            }

            HandledException handledException = handleMissingParameters(parameter);
            if (handledException != null) return handledException;
            return new HandledException(GenericErrorCode.INVALID_OR_MISSING_BODY, null, null, exception);
        }

        if (exception instanceof ResponseStatusException) {
            HttpStatus status = ((ResponseStatusException) exception).getStatus();
            if (status == HttpStatus.NOT_FOUND) return new HandledException(GenericErrorCode.NO_HANDLER, null, null, exception);
            return new HandledException(GenericErrorCode.UNKNOWN_ERROR, null, null, exception);
        }
        return new HandledException(GenericErrorCode.UNKNOWN_ERROR, null, null, exception);
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
            return new HandledException(code, null, asList(arg("name", parameterName), arg("expected",parameter.getParameterType().getName())), null);
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
