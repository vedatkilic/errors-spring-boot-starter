package tech.vedlabs.errors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tech.vedlabs.errors.message.TemplateAwareMessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerRegistry {

    private final List<ExceptionHandler> exceptionHandlers;

    private final ExceptionHandler exceptionHandler;

    private final ExceptionLogger exceptionLogger;

    private final Set<ExceptionHandlerPostProcessor> exceptionHandlerPostProcessors;

    private final TemplateAwareMessageSource templateAwareMessageSource;

    public ApiError handle(Exception exception, HttpServletRequest httpRequest, Locale locale) {
        if (locale == null) locale = Locale.ROOT;
        exceptionLogger.log(exception);
        log.debug("About to handle an exception", exception);

        ExceptionHandler handler = findHandler(exception);
        log.debug("The '{}' is going to handle the '{}' exception", className(handler), className(exception));

        HandledException handledException = handler.handle(exception);

        String message = templateAwareMessageSource.interpolate(handledException, locale);

        ApiError apiError = ApiError.builder()
                .errorCode(handledException.getErrorCode())
                .message(message)
                .errorDetail(handledException.getErrorDetail())
                .request(httpRequest)
                .exception(exception)
                .build();

        if(!CollectionUtils.isEmpty(exceptionHandlerPostProcessors)) {
            exceptionHandlerPostProcessors.forEach(postProcessor -> postProcessor.process(apiError));
        }

        return apiError;
    }

    private ExceptionHandler findHandler(Throwable exception) {
        if (exception == null) return exceptionHandler;

        return exceptionHandlers
                .stream()
                .filter(p -> p.canHandle(exception))
                .findFirst()
                .orElse(exceptionHandler);
    }

    private String className(Object toInspect) {
        if (toInspect == null) return "null";

        return toInspect.getClass().getName();
    }

}
