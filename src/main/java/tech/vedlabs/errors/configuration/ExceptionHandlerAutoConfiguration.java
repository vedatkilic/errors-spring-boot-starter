package tech.vedlabs.errors.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.handlers.*;
import tech.vedlabs.errors.message.DefaultErrorMessageReader;
import tech.vedlabs.errors.message.ErrorMessageReader;
import tech.vedlabs.errors.message.TemplateAwareMessageSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@ConditionalOnProperty(value = "errors.enabled", havingValue = "true")
@ConditionalOnWebApplication
public class ExceptionHandlerAutoConfiguration {

    private static final List<ExceptionHandler> BUILT_IN_HANDLERS = Arrays.asList(
            new ConversionFailedExceptionHandler(),
            new BaseExceptionHandler(),
            new TypeMismatchWebErrorHandler(),
            new MultipartWebErrorHandler(),
            new MissingRequestParametersExceptionHandler(),
            new SpringSecurityWebExceptionHandler()
    );

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandler exceptionHandler() {
        return new DefaultExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLogger exceptionLogger() {
        return ExceptionLogger.NoOp.INSTANCE;
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorMessageReader errorMessageReader(final MessageSource messageSource) {
        return new DefaultErrorMessageReader(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateAwareMessageSource errorMessageSource(final ErrorMessageReader errorMessageReader) {
        return new TemplateAwareMessageSource(errorMessageReader);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerRegistry exceptionHandlerRegistry(
                List<ExceptionHandler> exceptionHandlers,
                ExceptionHandler exceptionHandler,
                ExceptionLogger exceptionLogger,
                @Autowired(required = false) Set<ExceptionHandlerPostProcessor> postProcessors,
                TemplateAwareMessageSource errorMessageSource
            ) {

        List<ExceptionHandler> handlers = new ArrayList<>(BUILT_IN_HANDLERS);

        if (exceptionHandlers != null && !exceptionHandlers.isEmpty()) {
            exceptionHandlers.remove(exceptionHandler);
            exceptionHandlers.removeIf(Objects::isNull);
            exceptionHandlers.sort(AnnotationAwareOrderComparator.INSTANCE);

            handlers.addAll(exceptionHandlers);
        }

        return new ExceptionHandlerRegistry(handlers, exceptionHandler, exceptionLogger, postProcessors, errorMessageSource);
    }

    @Bean
    @ConditionalOnBean({ExceptionHandlerRegistry.class, WebResponseExceptionTranslator.class})
    public ExceptionControllerAdvice exceptionControllerAdvice(
            ExceptionHandlerRegistry exceptionHandlerRegistry,
            WebResponseExceptionTranslator webResponseExceptionTranslator) {
        return new ExceptionControllerAdvice(exceptionHandlerRegistry, webResponseExceptionTranslator);
    }
}
