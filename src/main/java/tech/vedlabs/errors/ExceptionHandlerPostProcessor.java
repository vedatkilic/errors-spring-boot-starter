package tech.vedlabs.errors;

public interface ExceptionHandlerPostProcessor {
    void process(ExceptionResponse exceptionResponse);
}
