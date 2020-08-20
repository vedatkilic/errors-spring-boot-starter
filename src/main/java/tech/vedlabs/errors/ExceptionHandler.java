package tech.vedlabs.errors;

import org.springframework.lang.Nullable;

public interface ExceptionHandler {
    boolean canHandle(@Nullable Throwable exception);
    HandledException handle(@Nullable Throwable exception);
}
