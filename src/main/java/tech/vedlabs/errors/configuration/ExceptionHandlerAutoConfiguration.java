package tech.vedlabs.errors.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.vedlabs.errors.*;
import tech.vedlabs.errors.handlers.*;
import tech.vedlabs.errors.message.DefaultErrorMessageSource;
import tech.vedlabs.errors.message.ErrorMessageSource;
import tech.vedlabs.errors.message.TemplateAwareMessageSource;

import java.util.List;
import java.util.Set;

@Configuration
@ConditionalOnProperty(value = "errors.enabled", havingValue = "true")
@ConditionalOnWebApplication
public class ExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BaseExceptionHandler.class)
    public BaseExceptionHandler exceptionHandler() {
        return new BaseExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(TypeMismatchWebExceptionHandler.class)
    public TypeMismatchWebExceptionHandler typeMismatchWebExceptionHandler() {
        return new TypeMismatchWebExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(MultipartWebExceptionHandler.class)
    public MultipartWebExceptionHandler multipartWebExceptionHandler() {
        return new MultipartWebExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(MissingRequestParametersExceptionHandler.class)
    public MissingRequestParametersExceptionHandler missingRequestParametersExceptionHandler() {
        return new MissingRequestParametersExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(SpringSecurityWebExceptionHandler.class)
    public SpringSecurityWebExceptionHandler springSecurityWebExceptionHandler() {
        return new SpringSecurityWebExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(ConversionFailedExceptionHandler.class)
    public ConversionFailedExceptionHandler conversionFailedExceptionHandler() {
        return new ConversionFailedExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(SpringValidationWebExceptionHandler.class)
    public SpringValidationWebExceptionHandler conversionFailedExceptionHandler(TypeMismatchWebExceptionHandler typeMismatchWebExceptionHandler, TemplateAwareMessageSource templateAwareMessageSource) {
        return new SpringValidationWebExceptionHandler(typeMismatchWebExceptionHandler, templateAwareMessageSource);
    }


    @Bean
    @ConditionalOnMissingBean(ResponseStatusExceptionHandler.class)
    public ResponseStatusExceptionHandler responseStatusExceptionHandler(SpringValidationWebExceptionHandler springValidationWebExceptionHandler, TypeMismatchWebExceptionHandler typeMismatchWebExceptionHandler) {
        return new ResponseStatusExceptionHandler(springValidationWebExceptionHandler, typeMismatchWebExceptionHandler);
    }

    @Bean
    @ConditionalOnMissingBean(ServletWebExceptionHandler.class)
    public ServletWebExceptionHandler servletWebExceptionHandler() {
        return new ServletWebExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionLogger exceptionLogger() {
        return ExceptionLogger.NoOp.INSTANCE;
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorMessageSource errorMessageReader(final MessageSource messageSource) {
        return new DefaultErrorMessageSource(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebResponseExceptionTranslator webResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TemplateAwareMessageSource errorMessageSource(final ErrorMessageSource errorMessageSource) {
        return new TemplateAwareMessageSource(errorMessageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerRegistry exceptionHandlerRegistry(
                List<ExceptionHandler> exceptionHandlers,
                ExceptionLogger exceptionLogger,
                @Autowired(required = false) Set<ExceptionHandlerPostProcessor> postProcessors,
                TemplateAwareMessageSource errorMessageSource
            ) {
        return new ExceptionHandlerRegistry(exceptionHandlers, new DefaultExceptionHandler(), exceptionLogger, postProcessors, errorMessageSource);
    }

    @Bean
    @ConditionalOnBean({ExceptionHandlerRegistry.class, WebResponseExceptionTranslator.class})
    public ExceptionControllerAdvice exceptionControllerAdvice(
            ExceptionHandlerRegistry exceptionHandlerRegistry,
            WebResponseExceptionTranslator webResponseExceptionTranslator) {
        return new ExceptionControllerAdvice(exceptionHandlerRegistry, webResponseExceptionTranslator);
    }
}
