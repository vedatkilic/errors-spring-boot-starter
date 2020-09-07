package tech.vedlabs.errors;

import org.springframework.lang.Nullable;

import java.util.Locale;

public interface ExceptionHandler {
    boolean canHandle(@Nullable Throwable exception);
    HandledException handle(@Nullable Throwable exception, Locale locale);
}
